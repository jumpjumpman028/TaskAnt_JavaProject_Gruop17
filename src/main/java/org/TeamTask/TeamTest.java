package org.TeamTask;

import org.Task.TaskManager;
import org.UserInfo;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class TeamTest {

    public static void main(String[] args) {
        UserInfo user  = new UserInfo("test",3);// 模擬設定當前使用者
        System.out.println("userID is: "+ UserInfo.userID);
        //創建Team Member
//        List<Integer> teamList = new ArrayList<>();
//        teamList.add(1);
//        teamList.add(2);
        //創建Team
        //Team adminTeam = new Team("TestTeam",teamList);

        //抓取資料庫
        TeamManager.getInstance().fetchTeamsFromDatabase();
//        List<Team> teamList = TeamManager.getInstance().getTeamList();
//        for (Team team : teamList) {
//            System.out.println(team.toString());
//        }
//        //加入Team
        //TeamManager.getInstance().joinTeam("C1IV5T"); //加入 Team "WeAreNoob"
        //加入Team到資料庫
        //TeamManager.getInstance().addTeam(adminTeam);
        //TeamManager.getInstance().updateTeamsToDatabase();

        //創建teamtask
//        TeamTask teamTask = new TeamTask(1, "task 2", "Description 1", "WaterFall",
//                LocalDate.now(), LocalTime.of(9, 0), LocalDate.now().plusDays(1), LocalTime.of(17, 0),
//                TeamTask.Status.IN_PROGRESS, TeamTask.Type.GENERAL, List.of(java.time.DayOfWeek.MONDAY));

        // 獲取 TeamTaskManager 實例
        TeamTaskManager teamTaskManager = TeamTaskManager.getInstance();
//        teamTaskManager.addTeamTaskToDatabase(teamTask);
//        // 執行 fetchTasksByTeam 方法
        teamTaskManager.fetchTeamTasks();
        List<TeamTask> taskList = teamTaskManager.getTeamTaskList();
        for (TeamTask task : taskList) {
            task.setDescription("Task Description");
            System.out.println(task.toString());
        }
        teamTaskManager.UploadDataToDatabase();
//
//        // 驗證結果
//        if (fetchedTasks.isEmpty()) {
//            System.out.println("測試結果：當前使用者未加入任何團隊或沒有相關任務。");
//        } else {
//            System.out.println("測試結果：成功抓取與當前使用者相關的任務！");
//            System.out.println("抓取的任務數量：" + fetchedTasks.size());
//            for (TeamTask task : fetchedTasks) {
//                System.out.println("任務 ID：" + task.getID() + ", 任務名稱：" + task.getName() + ", 所屬團隊 ID：" + task.getTeamID());
//            }
//        }
//
//        System.out.println("===== 測試結束 =====");
    }
}
