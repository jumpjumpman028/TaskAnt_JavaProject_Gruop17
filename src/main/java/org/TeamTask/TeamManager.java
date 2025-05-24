package org.TeamTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.DatabaseConnectionPool;
import org.UserInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private List<Team> teamList = new ArrayList<>();
    private static final TeamManager instance = new TeamManager();
    private static final ObjectMapper objectMapper = new ObjectMapper(); // 用於 JSON 處理

    // 單例模式
    public static TeamManager getInstance() {
        return instance;
    }

    // 從資料庫抓取 Team 資料並過濾出包含當前使用者的 Team
    public void fetchTeamsFromDatabase() {
        String selectSQL = "SELECT team_id, team_name, team_code, team_members FROM teams";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            List<Team> loadedTeams = new ArrayList<>();

            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String teamName = resultSet.getString("team_name");
                String teamCode = resultSet.getString("team_code");
                String teamMembersJson = resultSet.getString("team_members");

                // 將 JSON 字串轉換為 List<Integer>
                List<Integer> teamMembers = Team.teamMembersFromJson(teamMembersJson);

                // 檢查 UserInfo.userID 是否在 teamMembers 中
                if (teamMembers != null && teamMembers.contains(UserInfo.userID)) {
                    // 如果包含該使用者，創建 Team 物件並加入到 teamList
                    Team team = new Team(teamId, teamName, teamCode, teamMembers);
                    loadedTeams.add(team);
                }
            }

            // 更新 teamList
            this.teamList = loadedTeams;

            System.out.println("成功載入包含當前使用者的 Teams: " + teamList.size());

        } catch (SQLException e) {
            System.err.println("從資料庫載入 Teams 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 取得 teamList
    public List<Team> getTeamList() {
        return teamList;
    }
}
