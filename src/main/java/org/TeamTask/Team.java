package org.TeamTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Random;

public class Team {
    public  int teamId;                 // 團隊 ID（由 MySQL 自動生成）
    private final String teamCode;      // 團隊代碼
    private String teamName;            // 團隊名稱
    private List<Integer> teamMembers;   // 團隊成員
    private int memberCount;            // 成員數量
    private String leader;
    private String description;


    //用於創建已存在任務加入TeamList
    public Team(int teamId,String teamName,String teamCode, List<Integer> teamMembers, String leader , String description) {
        this.teamId = teamId;
        this.teamCode = teamCode; // 如果未提供 teamCode，自動生成
        this.teamName = teamName;
        this.teamMembers = teamMembers;
        this.memberCount = teamMembers.size();
        this.leader = leader;
        this.description = description;
    }
    //用於創建新任務
    public Team(String teamName, List<Integer> teamMembers,String leader, String description) {
        this.teamCode = generateRandomTeamCode();
        this.teamName = teamName;
        this.teamMembers = teamMembers;
        this.memberCount = teamMembers != null ? teamMembers.size() : 0;
        this.leader = leader;
        this.description = description;
    }

    // 自動生成長度為 6 的隨機字串作為 teamCode
    private String generateRandomTeamCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 可用字元
        StringBuilder teamCodeBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length()); // 隨機選擇字元
            teamCodeBuilder.append(characters.charAt(index));
        }

        return teamCodeBuilder.toString();
    }

    // Getter 和 Setter 方法
    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Integer> getTeamMembers() {//JAVA內部用
        return teamMembers;
    }

    public void setTeamMembers(List<Integer> teamMembers) {
        this.teamMembers = teamMembers;
        this.memberCount = teamMembers != null ? teamMembers.size() : 0;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getLeader() {
        return this.leader;
    }

    // 靜態方法：從 JSON 字串轉換為 teamMembers 屬性
    public static List<Integer> teamMembersFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 將 JSON 字串轉換為 List<Integer>
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 如果發生錯誤，返回 null
        }
    }

    // 將 teamMembers 屬性轉換為 JSON 字串
    public static String teamMembersToJson(List<Integer> teamMembers) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 將 List<int> 轉換為 JSON 字串
            return objectMapper.writeValueAsString(teamMembers);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]"; // 如果發生錯誤，返回空陣列的 JSON
        }
    }


    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", teamCode='" + teamCode + '\'' +
                ", teamName='" + teamName + '\'' +
                ", teamMembers=" + teamMembers +
                ", memberCount=" + memberCount +
                '}';
    }
}
