package org.TeamTask;

import org.Task.Task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TeamTask extends Task {
    private int teamID;
    public TeamTask(int teamID, String name, String description, String assignee, LocalDate startDate , LocalTime startTime, LocalDate endDate, LocalTime endTime, List<DayOfWeek> recurringDays, Type type) {
        //重複性任務範例
        super(name, description, assignee, startDate, startTime, endDate, endTime, recurringDays, type);
        this.teamID = teamID;

    }

    public TeamTask(int teamID, String name, String description, String assignee,LocalDate startDate, LocalTime startTime) {
        //一次性任務範例
        super(name, description, assignee, startDate, startTime);
        this.teamID = teamID;

    }

    public TeamTask(int teamID, String name, String description, String assignee,LocalDate startDate, LocalTime startTime,LocalDate endDate, LocalTime endTime,Status status, Type type,List<DayOfWeek> recurringDays) {
        //資料庫抓下來的所有任務
        super(name, description, assignee, startDate, startTime, endDate, endTime, status, type, recurringDays);
        this.teamID = teamID;

    }

    public int getTeamID() {
        return teamID;
    }

    @Override
    public String toString() {
        return "TeamTask{" +
                "teamID=" + teamID +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", assignee='" + getAssignee() + '\'' +
                ", startDate=" + getStartDate() +
                ", startTime=" + getStartTime() +
                ", endDate=" + getEndDate() +
                ", endTime=" + getEndTime() +
                ", status=" + getStatus() +
                ", type=" + getType() +
                ", recurringDays=" + getRecurringDays() +
                '}';
    }

}
