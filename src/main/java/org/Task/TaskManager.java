package org.Task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.*;
import org.Node.NodeMapView;

import java.awt.*;
import java.io.IOException;
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
            String insertTaskSQL = "INSERT INTO tasks (user_id, task_name, task_description, start_date, start_time, end_date, end_time , status, type, recurring_day, x, y, google, parent_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                preparedStatement.setDouble(11, task.);//x
                preparedStatement.setDouble(12, task.);//y
                preparedStatement.setString(13, task.);//裡面放JSON
                preparedStatement.setInt(14, task.);//parent_id


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
    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;

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

                    double x = resultSet.getDouble("x");

                    double y = resultSet.getDouble("y");

                    //List<String> tokens = 你的轉LIST函式(resultSet.getString("googele"));

                    int parentID = resultSet.getInt("parent_id");

                    // 創建 Task 物件並設置屬性
                    Task task = new Task(taskName, taskDescription, UserInfo.username, startDate, startTime, endDate, endTime, status, type, recurringDays, x, y, tokens, parentID);
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
        String updateTaskSQL = "UPDATE tasks SET task_description = ?, start_date = ?, start_time = ?, end_date = ?, end_time = ?, status = ?, type = ?, recurring_day = ?, x = ?, y = ?, google = ?, parent_id = ? WHERE user_id = ? AND task_name = ? AND task_id = ?";
        // SQL 語句：插入新任務
        String insertTaskSQL = "INSERT INTO tasks (user_id, task_name, task_description, start_date, start_time, end_date, end_time, status, type, recurring_day, x, y, google, parent_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                        if (task.getStartDate() != null) {//start date
                            updateStmt.setDate(2, java.sql.Date.valueOf(task.getStartDate()));
                        } else {
                            updateStmt.setNull(2, java.sql.Types.DATE);
                        }

                        if (task.getStartTime() != null) { // start time
                            updateStmt.setTime(3, java.sql.Time.valueOf(task.getStartTime()));
                        } else {
                            updateStmt.setNull(3, java.sql.Types.VARCHAR);
                        }

                        if (task.getEndDate() != null) {// end date
                            updateStmt.setDate(4, java.sql.Date.valueOf(task.getEndDate()));
                        } else {
                            updateStmt.setNull(4, java.sql.Types.DATE);
                        }

                        if (task.getEndTime() != null) {//end time
                            updateStmt.setTime(5, java.sql.Time.valueOf(task.getEndTime()));
                        } else {
                            updateStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        updateStmt.setInt(6, task.getStatus().getCode()); //status
                        updateStmt.setInt(7, task.getType().getCode()); //type

                        // 如果任務類型是 Experience，將 recurring_day 設為 NULL
                        if (task.getType() == Task.Type.Experience) {
                            updateStmt.setNull(8, java.sql.Types.INTEGER);
                        } else {
                            updateStmt.setInt(8, task.getRecurringDaysInt());
                        }

                        updateStmt.setDouble(9, task.getX());
                        updateStmt.setDouble(10, task.getY());
                        //updateStmt.setString(11,轉LIST);
                        updateStmt.setInt(12, task.getParentId());

                        updateStmt.setInt(13, UserInfo.userID); // user_id
                        updateStmt.setString(14, task.getName()); // task_name
                        updateStmt.setInt(15, task.getID());

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

                        if (task.getStartTime() != null) {
                            insertStmt.setTime(3, java.sql.Time.valueOf(task.getStartTime()));
                        } else {
                            insertStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        if (task.getEndDate() != null) {
                            insertStmt.setDate(6, java.sql.Date.valueOf(task.getEndDate()));
                        } else {
                            insertStmt.setNull(6, java.sql.Types.DATE);
                        }

                        if (task.getEndTime() != null) {
                            insertStmt.setTime(3, java.sql.Time.valueOf(task.getEndTime()));
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

                        insertStmt.setDouble(11, task.);//x
                        insertStmt.setDouble(12, task.);//y
                        insertStmt.setString(13, task.);//裡面放JSON
                        insertStmt.setInt(14, task.);//parent_id

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

    //刪除任務
    public boolean deleteDataFromDatabase(Task task) {
        String deleteTaskSQL = "DELETE FROM tasks WHERE user_id = ? AND task_name = ? AND task_id = ?";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteTaskSQL)) {

            int userID =  UserInfo.userID;
            String taskName = task.getName();
            int taskID = task.getID();
            // 設置參數
            preparedStatement.setInt(1, userID); // 使用者 ID
            preparedStatement.setString(2, taskName); // 任務名稱
            preparedStatement.setInt(3, taskID); // 任務 ID

            // 執行刪除操作
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // 從本地 taskList 中移除對應的任務
                taskList.remove(task);
                System.out.println("任務已成功刪除：" + taskName);
                return true;
            } else {
                System.out.println("未找到該任務，刪除失敗：" + taskName);
            }
        } catch (SQLException e) {
            System.err.println("從資料庫刪除任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
        return false; // 刪除失敗
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
                GoogleCalendarAuthorization.addTaskToGoogleCalendarForNextRecurringDays(task);
                System.out.println("Task added to Google Calendar: " + task.getName());
            }else if(task.getStatus() == Task.Status.COMPLETED){
                GoogleCalendarAuthorization.deleteAllGoogleEventsForTask(task);
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
    public void DeleteTask(Task task) {
        // 先刪除 Google Calendar 事件
        try {
            GoogleCalendarAuthorization.deleteAllGoogleEventsForTask(task);
        } catch (Exception e) {
            System.err.println("刪除 Google Calendar 事件失敗：" + e.getMessage());
            e.printStackTrace();
        }
        // 再從本地列表移除
        taskList.remove(task);
        DeBugConsole.log("刪除task");
        ///這邊寫得有夠爛 但暫時這樣 原本想用interface的fresh，但有點怪
        if(WaterTest.getInstance() != null) WaterTest.getInstance().refreshTaskList();
        if(NodeMapView.GetInstance() != null) NodeMapView.GetInstance().FreshEvent();
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

    /// 方便 但ShowInfo不應該在這裡
    public static void ShowInfo(Task task, Stage ownerStage){
        try {
            FXMLLoader loader = new FXMLLoader(instance.getClass().getResource("/org/TaskInfo.fxml"));
            Parent root = loader.load();
            TaskInfoController controller = loader.getController();
            controller.setTask(task); // 傳遞任務資料

            Stage infoStage  = new Stage();
            infoStage.initStyle(StageStyle.UNDECORATED);
            infoStage.setScene(new Scene(root));
            infoStage.initOwner(ownerStage);

            // 這裡設定新視窗的位置與大小與主視窗一樣
            infoStage.setX(ownerStage.getX());
            infoStage.setY(ownerStage.getY());
            infoStage.getScene().setFill(Color.TRANSPARENT);
            infoStage.getScene().getStylesheets().add(instance.getClass().getResource("/styles/textArea.css").toExternalForm());
            infoStage.initModality(Modality.APPLICATION_MODAL); // 視窗為模態
            infoStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}