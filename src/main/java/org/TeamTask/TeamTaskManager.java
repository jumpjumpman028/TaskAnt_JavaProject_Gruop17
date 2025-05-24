package org.TeamTask;

import org.DatabaseConnectionPool;
import org.Task.Task;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TeamTaskManager {

    private static final TeamTaskManager instance = new TeamTaskManager();

    // 單例模式
    public static TeamTaskManager getInstance() {
        return instance;
    }

    // 將任務分配給 Team
    public void assignTaskToTeam(int teamId, Task task) {
        String insertSQL = "INSERT INTO team_tasks (team_id, task_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, task.getID());

            preparedStatement.executeUpdate();
            System.out.println("成功將任務分配給 Team！");
        } catch (SQLException e) {
            System.err.println("分配任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 從資料庫抓取某個 Team 的任務
    public List<Task> fetchTasksByTeam(int teamId) {
        String query = "SELECT t.* FROM tasks t INNER JOIN team_tasks tt ON t.task_id = tt.task_id WHERE tt.team_id = ?";
        List<Task> taskList = new ArrayList<>();

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, teamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // 從結果集中提取任務資料
                    int taskId = resultSet.getInt("task_id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    String assignee = resultSet.getString("assignee");

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

                    // 創建任務物件
                    Task task = new Task(name, description, assignee, startDate, startTime, endDate, endTime, status, type, recurringDays);
                    task.SetID(taskId);

                    taskList.add(task);
                }

                System.out.println("成功抓取 Team 的任務，共有 " + taskList.size() + " 條任務");
            }
        } catch (SQLException e) {
            System.err.println("抓取 Team 的任務時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }

        return taskList;
    }
}
