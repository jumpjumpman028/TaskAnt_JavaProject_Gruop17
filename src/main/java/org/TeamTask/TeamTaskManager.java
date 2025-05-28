package org.TeamTask;

import org.DatabaseConnectionPool;
import org.Task.Task;
import org.UserInfo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TeamTaskManager {

    private static final TeamTaskManager instance = new TeamTaskManager();
    private List<TeamTask> teamTaskList = new ArrayList<>(); // 暫存團隊任務

    // 單例模式
    public static TeamTaskManager getInstance() {
        return instance;
    }

    // 獲取 teamTaskList
    public List<TeamTask> getTeamTaskList() {
        return teamTaskList;
    }


    // 清空 teamTaskList
    public void clearTeamTaskList() {
        teamTaskList.clear();
    }

    /// 新增單個 TeamTask 到資料庫
    public boolean addTeamTaskToDatabase(TeamTask teamTask) {
        teamTaskList.add(teamTask);

        // SQL 插入語句，包含所有欄位
        String insertSQL = "INSERT INTO teamtasks (team_id, task_name, task_description, start_date, start_time, end_date, end_time, status, type, recurring_day) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // 設定參數
            preparedStatement.setInt(1, teamTask.getTeamID());
            preparedStatement.setString(2, teamTask.getName()); // task_name
            preparedStatement.setString(3, teamTask.getDescription()); // task_description

            if (teamTask.getStartDate() != null) {
                preparedStatement.setDate(4, java.sql.Date.valueOf(teamTask.getStartDate())); // start_date
            }
            else {
                preparedStatement.setNull(4, java.sql.Types.DATE);
            }

            if (teamTask.getStartTime() != null) {
                preparedStatement.setString(5, teamTask.getStartTimeString()); // start_time
            }
            else {
                preparedStatement.setNull(5, java.sql.Types.VARCHAR);
            }

            if (teamTask.getEndDate() != null) {
                preparedStatement.setDate(6, java.sql.Date.valueOf(teamTask.getEndDate())); // end_date
            }
            else {
                preparedStatement.setNull(6, java.sql.Types.DATE);
            }

            if (teamTask.getEndTime() != null) {
                preparedStatement.setString(7, teamTask.getEndTimeString());
            } else {
                preparedStatement.setNull(7, java.sql.Types.VARCHAR);
            }


            preparedStatement.setInt(8, teamTask.getStatus().getCode());
            preparedStatement.setInt(9, teamTask.getType().getCode());
            if( teamTask.getType()== Task.Type.Experience) {
                preparedStatement.setNull(10, java.sql.Types.INTEGER);
            }else{
                preparedStatement.setInt(10, teamTask.getRecurringDaysInt());
            }
            // 執行插入操作
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("成功新增 TeamTask 到資料庫！");

                // 插入成功後，將該任務加入本地的 teamTaskList
                teamTaskList.add(teamTask);

                return true;
            } else {
                System.err.println("新增 TeamTask 到資料庫失敗，請檢查資料是否正確！");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("新增 TeamTask 到資料庫時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean UploadDataToDatabase() {
        // SQL 語句：檢查任務是否存在
        String checkTaskExistsSQL = "SELECT COUNT(*) FROM teamtasks WHERE team_id = ? AND task_name = ?";
        // SQL 語句：更新任務
        String updateTaskSQL = "UPDATE teamtasks SET task_description = ?, start_date = ?, start_time = ?, end_date = ?, end_time = ?, status = ?, type = ?, recurring_day = ? WHERE team_id = ? AND task_name = ?";
        // SQL 語句：插入新任務
        String insertTaskSQL = "INSERT INTO teamtasks (team_id, task_name, task_description, start_date, start_time, end_date, end_time, status, type, recurring_day) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection()) {
            for (TeamTask task : teamTaskList) {
                boolean taskExists = false;

                // 檢查任務是否已存在於資料庫
                try (PreparedStatement checkStmt = connection.prepareStatement(checkTaskExistsSQL)) {
                    checkStmt.setInt(1, task.getTeamID()); // team_id
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

                        updateStmt.setInt(9, task.getTeamID()); // team_id
                        updateStmt.setString(10, task.getName()); // task_name

                        updateStmt.executeUpdate();
                        System.out.println("任務已更新：" + task.getName());
                    }
                } else {
                    // 任務不存在，執行插入操作
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertTaskSQL)) {
                        // 插入欄位
                        insertStmt.setInt(1, task.getTeamID()); // team_id
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

    // 將 teamTaskList 中的任務同步到資料庫
//    public void updateTeamTaskToDatabase() {
//        String upsertSQL = "INSERT INTO team_tasks (team_id, task_id) VALUES (?, ?) " +
//                "ON DUPLICATE KEY UPDATE team_id = VALUES(team_id), task_id = VALUES(task_id)";
//
//        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(upsertSQL)) {
//
//            for (TeamTask teamTask : teamTaskList) {
//                preparedStatement.setInt(1, teamTask.getTeamID());
//                preparedStatement.setInt(2, teamTask.getID());
//
//                preparedStatement.addBatch(); // 將操作加入批次
//            }
//
//            int[] updateCounts = preparedStatement.executeBatch(); // 執行批次操作
//            System.out.println("成功更新或新增 " + updateCounts.length + " 條 Team 任務到資料庫！");
//
//        } catch (SQLException e) {
//            System.err.println("更新或新增 Team 任務到資料庫時發生錯誤：" + e.getMessage());
//            e.printStackTrace();
//        }
//    }



    // 從資料庫抓取與當前使用者相關的所有 Team 任務
    public boolean fetchTeamTasks() {

        // 清空 teamTaskList
        clearTeamTaskList();

        // 查詢與當前使用者相關的 TeamID
        String teamQuery = "SELECT team_id, team_member FROM teams";
        List<Integer> teamIDs = new ArrayList<>();

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement teamStatement = connection.prepareStatement(teamQuery);
             ResultSet resultSet = teamStatement.executeQuery()) {

            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String teamMembersJson = resultSet.getString("team_member");

                // 將 JSON 轉換為 List<Integer>
                List<Integer> teamMembers = Team.teamMembersFromJson(teamMembersJson);

                // 檢查當前使用者是否在 teamMembers 中
                if (teamMembers != null && teamMembers.contains(UserInfo.userID)) {
                    teamIDs.add(teamId);
                }
            }

        } catch (SQLException e) {
            System.err.println("查詢 TeamID 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }

        if (teamIDs.isEmpty()) {
            System.out.println("當前使用者未加入任何 Team！");
            return false;
        }

        // 查詢與這些 TeamID 相關的任務
        String taskQuery = "SELECT t.*, tt.team_id FROM teamtasks t " +
                "INNER JOIN teamtasks tt ON t.task_id = tt.task_id " +
                "WHERE tt.team_id IN (" + generatePlaceholders(teamIDs.size()) + ")";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement taskStatement = connection.prepareStatement(taskQuery)) {

            // 替換查詢中的佔位符
            for (int i = 0; i < teamIDs.size(); i++) {
                taskStatement.setInt(i + 1, teamIDs.get(i));
            }

            try (ResultSet resultSet = taskStatement.executeQuery()) {
                while (resultSet.next()) {
                    // 從結果集中提取任務資料
                    int taskId = resultSet.getInt("task_id");
                    String name = resultSet.getString("task_name");
                    String description = resultSet.getString("task_description");
                    int fetchedTeamID = resultSet.getInt("team_id");

                    // 時間相關資料
                    LocalDate startDate = resultSet.getDate("start_date") != null
                            ? resultSet.getDate("start_date").toLocalDate()
                            : null;
                    LocalDate endDate = resultSet.getDate("end_date") != null
                            ? resultSet.getDate("end_date").toLocalDate()
                            : null;
                    LocalTime startTime = resultSet.getTime("start_time") != null
                            ? resultSet.getTime("start_time").toLocalTime()
                            : null;
                    LocalTime endTime = resultSet.getTime("end_time") != null
                            ? resultSet.getTime("end_time").toLocalTime()
                            : null;

                    // 狀態與類型
                    Task.Status status = Task.Status.getStatus(resultSet.getInt("status"));
                    Task.Type type = Task.Type.values()[resultSet.getInt("type")];

                    // 處理 recurringDays
                    int recurringDaysInt = resultSet.getInt("recurring_day");
                    List<java.time.DayOfWeek> recurringDays = Task.intToRecurringDays(recurringDaysInt);

                    // 創建 TeamTask 物件
                    TeamTask teamTask = new TeamTask(fetchedTeamID, name, description, "assignee", startDate, startTime, endDate, endTime, status, type, recurringDays);

                    teamTaskList.add(teamTask);
                }

                System.out.println("成功抓取與當前使用者相關的任務，共有 " + teamTaskList.size() + " 條任務");
            }
        } catch (SQLException e) {
            System.err.println("抓取任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
    // 生成 SQL IN 子句的佔位符
    private String generatePlaceholders(int count) {
        return String.join(",", java.util.Collections.nCopies(count, "?"));
    }

}
