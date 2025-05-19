package org.Task;

import org.DatabaseConnectionPool;
import org.DeBugConsole;
import org.GoogleCalendarAuthorization;
import org.UserInfo;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            String insertTaskSQL = "INSERT INTO tasks (user_id, task_name, task_description, start_date, start_time, end_date, end_time , status, type, recurring_day) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertTaskSQL)) {

                preparedStatement.setInt(1, UserInfo.userID); // user_id //UserInfo.userid
                preparedStatement.setString(2, task.getName()); // task_name
                preparedStatement.setString(3, task.getDescription()); // task_description

                if (task.getStartDate() != null) {
                    preparedStatement.setDate(4, java.sql.Date.valueOf(task.getStartDate())); // start_date
                }
                else {
                    preparedStatement.setNull(4, java.sql.Types.DATE);
                }

                if (task.getStartTime() != null) {
                    preparedStatement.setString(5, task.getStartTimeString()); // start_time
                }
                else {
                    preparedStatement.setNull(5, java.sql.Types.VARCHAR);
                }

                if (task.getEndDate() != null) {
                    preparedStatement.setDate(6, java.sql.Date.valueOf(task.getEndDate())); // end_date
                }
                else {
                    preparedStatement.setNull(6, java.sql.Types.DATE);
                }

                if (task.getEndTime() != null) {
                    preparedStatement.setString(7, task.getEndTimeString());
                } else {
                    preparedStatement.setNull(7, java.sql.Types.VARCHAR);
                }


                preparedStatement.setInt(8, task.getStatus().getCode());
                preparedStatement.setInt(9, task.getType().getCode());
                if( task.getType()== Task.Type.Experience) {
                    preparedStatement.setNull(10, java.sql.Types.INTEGER);
                }else{
                    preparedStatement.setInt(10, task.getRecurringDaysInt());
                }

                preparedStatement.executeUpdate();
                System.out.println("任務已成功新增至資料庫！");
            } catch (SQLException e) {
                System.err.println("新增任務到資料庫時發生錯誤：" + e.getMessage());
                e.printStackTrace();
                // 如果需要，可以將錯誤重新拋出
                throw new RuntimeException("資料庫操作失敗", e);
            }
        } catch (Exception e) {
            System.err.println("新增任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            // 如果需要，可以記錄到日誌或通知用戶
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
        DeBugConsole.log("TaskManager請求更改 "+ task.getName() +" 的狀態");
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

    //當要把資料從資料庫抓下來時使用

    public boolean FetchDataFromDatabase() {
        // 假設 UserInfo.userId 是當前使用者的 ID
        String query = "SELECT * FROM tasks WHERE user_id = ?";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // 設置查詢參數（user_id）
            preparedStatement.setInt(1, UserInfo.userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // 清空本地 taskList，避免重複添加
                taskList.clear();

                // 遍歷結果集，將每一條記錄轉換為 Task 並加入 taskList
                while (resultSet.next()) {
                    // 從資料庫中提取欄位值
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

                    int recurringDaysInt = resultSet.getInt("recurring_day");
                    List<DayOfWeek> recurringDays = Task.intToRecurringDays(recurringDaysInt);

                    // 創建 Task 物件並設置屬性
                    Task task = new Task(taskName, taskDescription, UserInfo.username, startDate, startTime, endDate, endTime, status, type, recurringDays);
                    task.setStatus(status);
                    task.setType(type);

                    taskList.add(task);
                }

                System.out.println("成功從資料庫抓取任務，共有 " + taskList.size() + " 條任務");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("從資料庫抓取任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    public boolean UploadDataToDatabase() {
        // SQL 語句：檢查任務是否存在
        String checkTaskExistsSQL = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND task_name = ?";
        // SQL 語句：更新任務
        String updateTaskSQL = "UPDATE tasks SET task_description = ?, start_date = ?, start_time = ?, end_date = ?, end_time = ?, status = ?, type = ?, recurring_day = ? WHERE user_id = ? AND task_name = ?";
        // SQL 語句：插入新任務
        String insertTaskSQL = "INSERT INTO tasks (user_id, task_name, task_description, start_date, start_time, end_date, end_time, status, type, recurring_day) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection()) {
            for (Task task : taskList) {
                boolean taskExists = false;

                // 檢查任務是否已存在於資料庫
                try (PreparedStatement checkStmt = connection.prepareStatement(checkTaskExistsSQL)) {
                    checkStmt.setInt(1, UserInfo.userID); // user_id
                    checkStmt.setString(2, task.getName()); // task_name
                    try (ResultSet resultSet = checkStmt.executeQuery()) {
                        if (resultSet.next()) {
                            taskExists = resultSet.getInt(1) > 0; // 如果查詢結果大於 0，任務存在
                        }
                    }
                }

                if (taskExists) {
                    // 任務已存在，執行更新操作
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateTaskSQL)) {
                        // 更新欄位
                        updateStmt.setString(1, task.getDescription());
                        if (task.getStartDate() != null) {
                            updateStmt.setDate(2, java.sql.Date.valueOf(task.getStartDate()));
                        } else {
                            updateStmt.setNull(2, java.sql.Types.DATE);
                        }

                        if (task.getStartTimeString() != null) {
                            updateStmt.setString(3, task.getStartTimeString());
                        } else {
                            updateStmt.setNull(3, java.sql.Types.VARCHAR);
                        }

                        if (task.getEndDate() != null) {
                            updateStmt.setDate(4, java.sql.Date.valueOf(task.getEndDate()));
                        } else {
                            updateStmt.setNull(4, java.sql.Types.DATE);
                        }

                        if (task.getEndTimeString() != null) {
                            updateStmt.setString(5, task.getEndTimeString());
                        } else {
                            updateStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        updateStmt.setInt(6, task.getStatus().getCode());
                        updateStmt.setInt(7, task.getType().getCode());

                        // 如果任務類型是 Experience，將 recurring_day 設為 NULL
                        if (task.getType() == Task.Type.Experience) {
                            updateStmt.setNull(8, java.sql.Types.INTEGER);
                        } else {
                            updateStmt.setInt(8, task.getRecurringDaysInt());
                        }

                        updateStmt.setInt(9, UserInfo.userID); // user_id
                        updateStmt.setString(10, task.getName()); // task_name

                        updateStmt.executeUpdate();
                        System.out.println("任務已更新：" + task.getName());
                    }
                } else {
                    // 任務不存在，執行插入操作
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertTaskSQL)) {
                        // 插入欄位
                        insertStmt.setInt(1, UserInfo.userID); // user_id
                        insertStmt.setString(2, task.getName()); // task_name
                        insertStmt.setString(3, task.getDescription());

                        if (task.getStartDate() != null) {
                            insertStmt.setDate(4, java.sql.Date.valueOf(task.getStartDate()));
                        } else {
                            insertStmt.setNull(4, java.sql.Types.DATE);
                        }

                        if (task.getStartTimeString() != null) {
                            insertStmt.setString(5, task.getStartTimeString());
                        } else {
                            insertStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        if (task.getEndDate() != null) {
                            insertStmt.setDate(6, java.sql.Date.valueOf(task.getEndDate()));
                        } else {
                            insertStmt.setNull(6, java.sql.Types.DATE);
                        }

                        if (task.getEndTimeString() != null) {
                            insertStmt.setString(7, task.getEndTimeString());
                        } else {
                            insertStmt.setNull(7, java.sql.Types.VARCHAR);
                        }

                        insertStmt.setInt(8, task.getStatus().getCode());
                        insertStmt.setInt(9, task.getType().getCode());

                        // 如果任務類型是 Experience，將 recurring_day 設為 NULL
                        if (task.getType() == Task.Type.Experience) {
                            insertStmt.setNull(10, java.sql.Types.INTEGER);
                        } else {
                            insertStmt.setInt(10, task.getRecurringDaysInt());
                        }

                        insertStmt.executeUpdate();
                        System.out.println("任務已新增：" + task.getName());
                    }
                }
            }
            return true; // 所有任務同步成功
        } catch (SQLException e) {
            System.err.println("同步任務到資料庫時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false; // 同步失敗
        }
    }


    public void Notify() {
        // 遍歷所有任務
        for (Task task : taskList) {
            // 判斷任務的狀態
            if (task.getStatus() == Task.Status.TODO) {
                // 檢查任務是否即將開始
                if (task.getStartDate() != null && task.getStartTime() != null) {
                    LocalDateTime taskStartDateTime = LocalDateTime.of(task.getStartDate(), task.getStartTime());
                    LocalDateTime now = LocalDateTime.now();

                    // 如果任務即將開始（例如提前10分鐘提醒）
                    if (taskStartDateTime.isAfter(now) && taskStartDateTime.isBefore(now.plusMinutes(10))) {
                        // 發送通知
                        sendNotification("任務即將開始", "任務名稱: " + task.getName() + "\n描述: " + task.getDescription());

                        // 更新任務狀態為進行中
                        task.setStatus(Task.Status.IN_PROGRESS);
                        System.out.println("任務狀態已更新為進行中：" + task.getName());
                    }
                }
            } else if (task.getStatus() == Task.Status.IN_PROGRESS) {
                // 如果任務正在進行，檢查是否需要提醒用戶
                if (task.getEndDate() != null && task.getEndTime() != null) {
                    LocalDateTime taskEndDateTime = LocalDateTime.of(task.getEndDate(), task.getEndTime());
                    LocalDateTime now = LocalDateTime.now();

                    // 如果任務即將結束（例如提前10分鐘提醒）
                    if (taskEndDateTime.isAfter(now) && taskEndDateTime.isBefore(now.plusMinutes(10))) {
                        // 發送通知
                        sendNotification("任務即將結束", "任務名稱: " + task.getName() + "\n描述: " + task.getDescription());
                    }
                }
            }
        }
    }

    private void sendNotification(String title, String message) {
        // 檢查系統是否支援 SystemTray
        if (!SystemTray.isSupported()) {
            System.err.println("系統不支援通知功能！");
            return;
        }

        try {
            // 創建系統托盤圖標
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/notification.jpg")); // 確保圖片路徑正確
            TrayIcon trayIcon = new TrayIcon(image, "任務通知");
            trayIcon.setImageAutoSize(true);

            // 添加到系統托盤
            systemTray.add(trayIcon);

            // 顯示通知
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

            // 移除托盤圖標（可選，延遲幾秒後移除）
            Thread.sleep(5000);
            systemTray.remove(trayIcon);
        } catch (Exception e) {
            System.err.println("發送通知時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 當更新任務時，請呼叫此函式
     * 
     * @param taskList
     */
    public void NotifyAllUsersDataChanged(List<Task> taskList) {
        // TODO: 當任務更新時通知團隊中的其他人
    }

    /**
     * 確認是否需上傳至GoogleCalendar
     * 
     * @throws Exception
     */
    public void CheckAndUpdateTaskInGoogleCalendar(Task task) throws Exception {
        //TODO:時間到，需要上傳資料至GoogleCalender，
        if(task.getType() == Task.Type.Experience){
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
        if(task.getStatus() == Task.Status.TODO && task.isTaskOnTimeCheck()){
            task.setStatus(Task.Status.IN_PROGRESS);
            DeBugConsole.log("成功將任務 " + task.getName()+" 調至進行");
        }

    }

    public int getNextTaskNumberForUser(int userId) {
        String query = "SELECT COUNT(*) FROM tasks WHERE user_id = ?";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int taskCount = resultSet.getInt(1); // 獲取任務數量
                    return taskCount + 1; // 返回下一個任務編號
                }
            }
        } catch (SQLException e) {
            // 錯誤處理：記錄日誌或拋出異常
            e.printStackTrace();
        }

        // 如果查詢失敗或發生異常，返回 -1 表示錯誤
        return -1;
    }

}