package org.TeamTask;

import org.DatabaseConnectionPool;
import org.UserInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamManager {

    private List<Team> teamList = new ArrayList<>();
    private static final TeamManager instance = new TeamManager();

    // 單例模式
    public static TeamManager getInstance() {
        return instance;
    }

    // 從資料庫抓取 Team 資料
    public boolean fetchTeamsFromDatabase() {
        String query = "SELECT * FROM teams WHERE user_id = ?";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, UserInfo.userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // 清空本地 teamList，避免重複添加
                teamList.clear();

                while (resultSet.next()) {
                    int teamId = resultSet.getInt("team_id");
                    String teamCode = resultSet.getString("team_code");
                    String teamName = resultSet.getString("team_name");

                    // 處理 team_members 資料（假設資料庫中 team_members 是以逗號分隔的字串存儲）
                    String teamMembersStr = resultSet.getString("team_members");
                    List<String> teamMembers = teamMembersStr != null && !teamMembersStr.isEmpty()
                            ? Arrays.asList(teamMembersStr.split(","))
                            : new ArrayList<>();

                    // 創建 Team 物件並添加到列表
                    Team team = new Team(teamId, teamCode, teamName, teamMembers);
                    teamList.add(team);
                }

                System.out.println("成功從資料庫抓取 Team，共有 " + teamList.size() + " 個 Team");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("從資料庫抓取 Team 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 創建新 Team 並插入資料庫
    public void createTeam(String teamCode, String teamName, List<String> teamMembers) {
        String insertSQL = "INSERT INTO teams (user_id, team_code, team_name, team_members) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, UserInfo.userID);
            preparedStatement.setString(2, teamCode);
            preparedStatement.setString(3, teamName);

            // 將 teamMembers 轉換為逗號分隔的字串
            String teamMembersStr = String.join(",", teamMembers);
            preparedStatement.setString(4, teamMembersStr);

            preparedStatement.executeUpdate();

            // 獲取自動生成的 team_id
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int teamId = generatedKeys.getInt(1);

                    // 同步到本地 teamList
                    teamList.add(new Team(teamId, teamCode, teamName, teamMembers));
                }
            }

            System.out.println("成功創建 Team 並插入資料庫！");
        } catch (SQLException e) {
            System.err.println("創建 Team 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 獲取本地 Team 列表
    public List<Team> getTeamList() {
        return teamList;
    }
}
