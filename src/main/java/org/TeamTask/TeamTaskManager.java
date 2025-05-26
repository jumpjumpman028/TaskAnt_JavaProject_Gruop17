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

    // 新增單個 TeamTask 到 teamTaskList
    public void addTaskToTeamList(TeamTask teamTask) {
        teamTaskList.add(teamTask);
    }

    // 清空 teamTaskList
    public void clearTeamTaskList() {
        teamTaskList.clear();
    }

    // 將 teamTaskList 中的任務同步到資料庫
    public void updateTeamTaskToDatabase() {
        String upsertSQL = "INSERT INTO team_tasks (team_id, task_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE team_id = VALUES(team_id), task_id = VALUES(task_id)";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(upsertSQL)) {

            for (TeamTask teamTask : teamTaskList) {
                preparedStatement.setInt(1, teamTask.getTeamID());
                preparedStatement.setInt(2, teamTask.getID());

                preparedStatement.addBatch(); // 將操作加入批次
            }

            int[] updateCounts = preparedStatement.executeBatch(); // 執行批次操作
            System.out.println("成功更新或新增 " + updateCounts.length + " 條 Team 任務到資料庫！");

        } catch (SQLException e) {
            System.err.println("更新或新增 Team 任務到資料庫時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }



    // 從資料庫抓取與當前使用者相關的所有 Team 任務
    public List<TeamTask> fetchTasksByTeam() {
        // 確保已經設定當前使用者的 ID
        int userID = UserInfo.userID;
        if (userID == 0) {
            System.err.println("尚未設定當前使用者的 ID！");
            return new ArrayList<>();
        }

        // 清空 teamTaskList
        clearTeamTaskList();

        // 查詢與當前使用者相關的 TeamID
        String teamQuery = "SELECT team_id, team_members FROM teams";
        List<Integer> teamIDs = new ArrayList<>();

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement teamStatement = connection.prepareStatement(teamQuery);
             ResultSet resultSet = teamStatement.executeQuery()) {

            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String teamMembersJson = resultSet.getString("team_members");

                // 將 JSON 轉換為 List<Integer>
                List<Integer> teamMembers = Team.teamMembersFromJson(teamMembersJson);

                // 檢查當前使用者是否在 teamMembers 中
                if (teamMembers != null && teamMembers.contains(userID)) {
                    teamIDs.add(teamId);
                }
            }

        } catch (SQLException e) {
            System.err.println("查詢 TeamID 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

        if (teamIDs.isEmpty()) {
            System.out.println("當前使用者未加入任何 Team！");
            return new ArrayList<>();
        }

        // 查詢與這些 TeamID 相關的任務
        String taskQuery = "SELECT t.*, tt.team_id FROM tasks t " +
                "INNER JOIN team_tasks tt ON t.task_id = tt.task_id " +
                "WHERE tt.team_id IN (" + generatePlaceholders(teamIDs.size()) + ")";
        List<TeamTask> taskList = new ArrayList<>();

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
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    String assignee = resultSet.getString("assignee");
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
                    int recurringDaysInt = resultSet.getInt("recurring_days");
                    List<java.time.DayOfWeek> recurringDays = Task.intToRecurringDays(recurringDaysInt);

                    // 創建 TeamTask 物件
                    TeamTask teamTask = new TeamTask(fetchedTeamID, name, description, assignee, startDate, startTime, endDate, endTime, status, type, recurringDays);
                    teamTask.SetID(taskId);

                    taskList.add(teamTask);
                }

                System.out.println("成功抓取與當前使用者相關的任務，共有 " + taskList.size() + " 條任務");
            }
        } catch (SQLException e) {
            System.err.println("抓取任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }

        // 將抓取的任務加入 teamTaskList
        teamTaskList.addAll(taskList);

        return taskList;
    }
    // 生成 SQL IN 子句的佔位符
    private String generatePlaceholders(int count) {
        return String.join(",", java.util.Collections.nCopies(count, "?"));
    }

}
