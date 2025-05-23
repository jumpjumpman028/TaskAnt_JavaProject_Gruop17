package org;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TeamTest {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 測試資料
        String jsonWithTeamMembers = "[\"Alice\", \"Bob\", \"Charlie\"]";

        // 測試 1：測試 teamMembers 的 JSON 轉換
        try {
            System.out.println("=== 測試 1：teamMembers 的 JSON 轉換 ===");
            List<String> teamMembers = objectMapper.readValue(jsonWithTeamMembers, new TypeReference<List<String>>() {});
            System.out.println("解析的 teamMembers: " + teamMembers);

            Team team = new Team(1,"1234asda","noob Team",teamMembers);
            System.out.println(team.toString());

        } catch (Exception e) {
            System.err.println("測試 1 失敗：" + e.getMessage());
        }

    }
}
