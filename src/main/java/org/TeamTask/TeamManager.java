package org.TeamTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.DatabaseConnectionPool;
import org.Task.Task;
import org.Task.TaskInfoController;
import org.UserInfo;

import java.io.IOException;
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
        String selectSQL = "SELECT team_id, team_name, team_code, team_member, team_leader, team_description FROM teams";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            teamList.clear();

            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String teamName = resultSet.getString("team_name");
                String teamCode = resultSet.getString("team_code");
                String teamMembersJson = resultSet.getString("team_member");
                String teamLeader = resultSet.getString("team_leader");
                String teamDescription = resultSet.getString("team_description");

                // 將 JSON 字串轉換為 List<Integer>
                List<Integer> teamMembers = Team.teamMembersFromJson(teamMembersJson);
//                System.out.println(teamMembers);
//                System.out.println(teamMembers.contains(UserInfo.userID));

                // 檢查 UserInfo.userID 是否在 teamMembers 中
                if (teamMembers != null && teamMembers.contains(UserInfo.userID)) {
                    // 如果包含該使用者，創建 Team 物件並加入到 teamList
                    Team team = new Team(teamId, teamName, teamCode, teamMembers, teamLeader, teamDescription);
                    teamList.add(team);
                }
            }


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

    // 新增新 Team 到資料庫並更新 teamList
    public boolean addTeam(Team newTeam) {
        String insertSQL = "INSERT INTO teams (team_name, team_code, team_member, member_count, team_leader, team_description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // 設定 SQL 參數
            preparedStatement.setString(1, newTeam.getTeamName());
            preparedStatement.setString(2, newTeam.getTeamCode());
            preparedStatement.setString(3, Team.teamMembersToJson(newTeam.getTeamMembers())); // 將 teamMembers 轉為 JSON 儲存
            preparedStatement.setInt(4, newTeam.getMemberCount());
            preparedStatement.setString(5, newTeam.getLeader());
            preparedStatement.setString(6, newTeam.getDescription());
            // 執行插入操作
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // 獲取自動生成的 team_id
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedTeamId = generatedKeys.getInt(1);
                        newTeam.setTeamId(generatedTeamId); // 設定新 Team 的 ID
                    }
                }

                // 將新 Team 加入 teamList
                teamList.add(newTeam);

                System.out.println("成功新增新 Team: " + newTeam.getTeamName() + "，Team ID: " + newTeam.getTeamId());
                return true;
            } else {
                System.err.println("新增 Team 失敗，未影響任何資料列。");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("新增 Team 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //把團隊資訊更新到SQL
    public boolean updateTeamsToDatabase() {
        String updateSQL = "UPDATE teams SET team_name = ?, team_code = ?, team_member = ?, member_count = ?, team_description WHERE team_id = ?";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            for (Team team : teamList) {
                System.out.println("Team ID: " + team.getTeamId() +
                        ", Member Count: " + team.getMemberCount());

                // 設定 SQL 參數
                preparedStatement.setString(1, team.getTeamName());
                preparedStatement.setString(2, team.getTeamCode());
                preparedStatement.setString(3, Team.teamMembersToJson(team.getTeamMembers())); // 將 teamMembers 轉為 JSON 儲存
                preparedStatement.setInt(4, team.getMemberCount());
                preparedStatement.setInt(5, team.getTeamId());
                preparedStatement.setString(6, team.getDescription());

                // 執行更新操作
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    System.err.println("更新 Team 失敗，Team ID: " + team.getTeamId() + " 未影響任何資料列。");
                    return false;
                }
            }

            System.out.println("成功更新所有 Teams。");
            return true;

        } catch (SQLException e) {
            System.err.println("更新 Teams 時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean joinTeam(String teamcode) {
        // 查詢團隊的 SQL
        String selectSQL = "SELECT team_id, team_name, team_code, team_member, team_leader, team_description FROM teams WHERE team_code = ?";
        String updateSQL = "UPDATE teams SET team_member = ?, member_count = ? WHERE team_code = ?";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {

            // 設定查詢參數
            selectStatement.setString(1, teamcode);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // 取得團隊資料
                    int teamId = resultSet.getInt("team_id");
                    String teamName = resultSet.getString("team_name");
                    String teamCode = resultSet.getString("team_code");
                    String teamMembersJson = resultSet.getString("team_member");
                    String teamLeader = resultSet.getString("team_leader");
                    String teamDescription = resultSet.getString("team_description");

                    // 將 JSON 字串轉換為 List<Integer>
                    List<Integer> teamMembers = Team.teamMembersFromJson(teamMembersJson);

                    // 檢查當前使用者是否已經在團隊中
                    if (teamMembers.contains(UserInfo.userID)) {
                        System.out.println("您已經是團隊的一員，無需再次加入！");
                        return false;
                    }

                    // 將當前使用者加入團隊成員列表
                    teamMembers.add(UserInfo.userID);

                    // 更新資料庫中的團隊成員資料
                    updateStatement.setString(1, Team.teamMembersToJson(teamMembers)); // 將成員列表轉為 JSON
                    updateStatement.setInt(2, teamMembers.size()); // 更新成員數量
                    updateStatement.setString(3, teamcode);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("成功加入團隊：" + teamName);

                        // 更新本地的 teamList
                        Team updatedTeam = new Team(teamId, teamName, teamCode, teamMembers, teamLeader, teamDescription);
                        teamList.add(updatedTeam);
                        return true;
                    } else {
                        System.err.println("更新團隊成員時發生錯誤，請稍後再試！");
                        return false;
                    }
                } else {
                    // 如果查詢不到團隊
                    System.out.println("無效的團隊代碼，請確認後再試！");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("加入團隊時發生錯誤：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTeam(Team team) {
        String deleteSQL = "DELETE FROM teams WHERE team_id = ?";

        try (Connection connection = DatabaseConnectionPool.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            // 設置 SQL 參數
            preparedStatement.setInt(1, team.getTeamId());

            // 執行刪除操作
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // 從本地 teamList 移除對應的團隊
                teamList.remove(team);
                System.out.println("成功刪除團隊，Team ID: " + team.getTeamId());
                return true;
            } else {
                System.out.println("未找到該團隊，刪除失敗，Team ID: " + team.getTeamId());
            }
        } catch (SQLException e) {
            System.err.println("刪除團隊時發生錯誤： " + e.getMessage());
            e.printStackTrace();
        }
        return false; // 刪除失敗
    }

//    public static void ShowInfo(Team team, Stage ownerStage){
//        try {
//            FXMLLoader loader = new FXMLLoader(instance.getClass().getResource("/org/TeamInfo.fxml"));
//            Parent root = loader.load();
//            TeamInfo controller = loader.getController();
//            controller.setTeam(team); // 傳遞任務資料
//
//            Stage infoStage  = new Stage();
//            infoStage.initStyle(StageStyle.UNDECORATED);
//            infoStage.setScene(new Scene(root));
//            infoStage.initOwner(ownerStage);
//
//            // 這裡設定新視窗的位置與大小與主視窗一樣
//            infoStage.setX(ownerStage.getX());
//            infoStage.setY(ownerStage.getY());
//            infoStage.getScene().setFill(Color.TRANSPARENT);
//            infoStage.getScene().getStylesheets().add(instance.getClass().getResource("/styles/textArea.css").toExternalForm());
//            infoStage.initModality(Modality.APPLICATION_MODAL); // 視窗為模態
//            infoStage.showAndWait();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
