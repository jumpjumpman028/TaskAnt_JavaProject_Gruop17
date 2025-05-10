package org;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TaskManager {

    private List<Task> taskList = new ArrayList<>();
    private final static TaskManager instance = new TaskManager();

    public static TaskManager getInstance() {
        return instance;
    }

    public void AddTask(Task task) {
        try {
            // 將任務加入本地列表
            taskList.add(task);
            CheckLocalDateTimeInProcess(task);

            // 同步到 Google Calendar
            try {
                CheckAndUpdateTaskInGoogleCalendar(task);
            } catch (Exception e) {
                System.err.println("同步到 Google Calendar 時發生錯誤：" + e.getMessage());
                e.printStackTrace();
                // 如果需要，可以記錄到日誌或通知用戶
            }

            // 插入資料庫
            String insertTaskSQL = "INSERT INTO tasks (user_id, task_name, task_description, start_date, start_time, end_date, end_time, status, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL)) {

                preparedStatement.setInt(1, 1); // user_id
                preparedStatement.setString(2, task.getName()); // task_name
                preparedStatement.setString(3, task.getDescription()); // task_description

                if (task.getStartDate() != null) {
                    preparedStatement.setDate(4, java.sql.Date.valueOf(task.getStartDate())); // start_date
                } else {
                    preparedStatement.setNull(4, java.sql.Types.DATE);
                }

                if (task.getStartTimeString() != null && !task.getStartTimeString().isEmpty()) {
                    preparedStatement.setString(5, task.getStartTimeString()); // start_time
                } else {
                    preparedStatement.setNull(5, java.sql.Types.VARCHAR);
                }

                if (task.getEndDate() != null) {
                    preparedStatement.setDate(6, java.sql.Date.valueOf(task.getEndDate())); // end_date
                } else {
                    preparedStatement.setNull(6, java.sql.Types.DATE);
                }

                if (task.getEndTimeString() != null && !task.getEndTimeString().isEmpty()) {
                    preparedStatement.setString(7, task.getEndTimeString()); // end_time
                } else {
                    preparedStatement.setNull(7, java.sql.Types.VARCHAR);
                }

                preparedStatement.setInt(8, task.getStatus().getCode());
                preparedStatement.setInt(9, task.getType().getCode());

                preparedStatement.executeUpdate();
                System.out.println("任務已成功新增至資料庫！");
            } catch (SQLException e) {
                System.err.println("新增任務到資料庫時發生錯誤：" + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("資料庫操作失敗", e);
            }
        } catch (Exception e) {
            System.err.println("新增任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void CreateTask(String taskName, String description, LocalDate startDate, Integer startHour, Integer startMinute,
                           LocalDate endDate, Integer endHour, Integer endMinute, List<DayOfWeek> recurringDays, Task.Type taskType) {
        LocalTime startTime = null;
        LocalTime endTime = null;
        if (startHour != null && startMinute != null) {
            startTime = LocalTime.of(startHour, startMinute);
        }
        if (endHour != null && endMinute != null) {
            endTime = LocalTime.of(endHour, endMinute);
        }

        if (taskType == Task.Type.Experience) {
            AddTask(new Task(taskName, description, "使用者", startDate, startTime));
        } else {
            AddTask(new Task(taskName, description, "使用者", startDate, startTime, endDate, endTime, recurringDays, taskType));
        }
    }

    public void updateStatus(Task task, Task.Status newStatus) {
        task.setStatus(newStatus);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public List<Task> getTasksByStatus(Task.Status status) {
        return taskList.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean FetchDataFromDatabase() {
        String query = "SELECT * FROM tasks";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            taskList.clear();

            while (resultSet.next()) {
                String taskName = resultSet.getString("task_name");
                String taskDescription = resultSet.getString("task_description");
                LocalDate startDate = resultSet.getDate("start_date") != null ? resultSet.getDate("start_date").toLocalDate() : null;
                LocalTime startTime = resultSet.getTime("start_time") != null ? resultSet.getTime("start_time").toLocalTime() : null;
                LocalDate endDate = resultSet.getDate("end_date") != null ? resultSet.getDate("end_date").toLocalDate() : null;
                LocalTime endTime = resultSet.getTime("end_time") != null ? resultSet.getTime("end_time").toLocalTime() : null;

                // 抓取 status 和 type 欄位
                int statusCode = resultSet.getInt("status");
                Task.Status status = Task.Status.getStatus(statusCode); // 假設有一個方法從代碼轉換為枚舉

                int typeCode = resultSet.getInt("type");
                Task.Type type = Task.Type.getType(typeCode); // 假設有一個方法從代碼轉換為枚舉

                // 創建 Task 物件並設置屬性
                Task task = new Task(taskName, taskDescription, UserInfo.username, startDate, startTime, endDate, endTime, status, type);
                task.setStatus(status);
                task.setType(type);

                taskList.add(task);
            }

            System.out.println("成功從資料庫抓取任務，共有 " + taskList.size() + " 條任務");
            return true;
        } catch (SQLException e) {
            System.err.println("從資料庫抓取任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean UploadDataToDatabase() {
        NotifyAllUsersDataChanged(taskList);
        return true;
    }

    public void NotifyAllUsersDataChanged(List<Task> taskList) {
        // TODO: 當任務更新時通知團隊中的其他人
    }

    public void CheckAndUpdateTaskInGoogleCalendar(Task task) throws Exception {
        if (task.getType() == Task.Type.Experience) {
            return;
        }
        try {
            if (task.getStatus() == Task.Status.IN_PROGRESS) {
                GoogleCalendarAuthorization calendarAuthorization = new GoogleCalendarAuthorization();
                calendarAuthorization.addTaskToGoogleCalendar(task);
                System.out.println("Task added to Google Calendar: " + task.getName());
            }
        } catch (Exception e) {
            throw new Exception("CheckAndUpdateTaskInGoogleCalendar 有錯" + e.getMessage());
        }
    }

    public void CheckLocalDateTimeInProcess(Task task) {
        if (task.getStatus() == Task.Status.TODO && task.getStartDate().isAfter(LocalDate.now())) {
            return;
        } else if (task.getStatus() == Task.Status.TODO && task.getStartDate().isEqual(LocalDate.now()) && task.getStartTime().isAfter(LocalTime.now())) {
            return;
        }
        task.setStatus(Task.Status.IN_PROGRESS);
        DeBugConsole.log("成功將任務 " + task.getName() + " 調至進行");
    }
}
