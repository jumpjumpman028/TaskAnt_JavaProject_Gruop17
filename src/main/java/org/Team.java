package org;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Team {
    private int teamId;                 // 團隊 ID
    private String teamCode;            // 團隊代碼
    private String teamName;            // 團隊名稱
    private List<String> teamMembers;   // 團隊成員
    private int memberCount;            // 成員數量


    public Team() {}

    public Team(int teamId, String teamCode, String teamName, List<String> teamMembers) {
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.teamMembers = teamMembers;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<String> teamMembers) {
        this.teamMembers = teamMembers;
        this.memberCount = teamMembers != null ? teamMembers.size() : 0;
    }

    public int getMemberCount() {
        return memberCount;
    }

    // 靜態方法：從 JSON 字串轉換為 Team 物件
    public static Team fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 將 JSON 字串轉換為 Team 物件
            return objectMapper.readValue(json, Team.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 如果發生錯誤，返回 null
        }
    }

    // 將 Team 物件轉換為 JSON 字串
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 將物件轉換為 JSON 字串
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 如果發生錯誤，返回 null
        }
    }

    // toString 方法
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
