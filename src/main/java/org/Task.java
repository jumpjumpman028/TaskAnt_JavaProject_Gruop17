package org;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
public class Task {

    private int ID = -1 ;
    private String name;
    private String description;
    private String assignee;
    private Status status;
    private Type type;
    private List<DayOfWeek> recurringDays; //任務每周執行時間(若有)
    private LocalDate startDate; //開始日期
    private LocalDate endDate;  //結束日期
    private LocalTime startTime;    //開始執行時間
    private LocalTime endTime;  //結束執行時間

        public Task(String name, String description, String assignee, String startTime, String endTime,List<DayOfWeek> recurringDays) {
        //制式化任務範例
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.TODO;
        this.type = Type.GENERAL;
        this.startTime = convertStringToLocalTime(startTime);
        this.endTime = convertStringToLocalTime(endTime);
        this.recurringDays = recurringDays;
    }
    public Task(String name, String description, String assignee,LocalDate startDate,String startTime, String endTime) {
        //每天重複任務範例
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.TODO;
        this.type = Type.BOSS;
        this.startDate = startDate;
        this.startTime = convertStringToLocalTime(startTime);
        this.endTime = convertStringToLocalTime(endTime);
        this.recurringDays = List.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY);

    }
    public Task(String name, String description, String assignee,LocalDate startDate, String startTime) {
        //一次性任務範例
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.IN_PROGRESS;
        this.type = Type.Experience;
        this.startDate = startDate;
        this.startTime = convertStringToLocalTime(startTime);
        this.endDate = startDate;
        this.endTime = null;
        this.recurringDays = null;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if(status == Status.COMPLETED && this.status == Status.IN_PROGRESS){
            setEndDate( LocalDate.now());
        }
        if(status == Status.IN_PROGRESS && this.status == Status.TODO){
            setStartDate(LocalDate.now());
            try{
                TaskManager.getInstance().CheckAndUpdateTaskInGoogleCalendar(this);
            }catch (Exception e){
                DeBugConsole.log("Error in checkAndUpdateTaskInGoogleCalendar" + e.getMessage());
            }

        }
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public List<DayOfWeek> getRecurringDays() {
        return recurringDays;
    }

    public void setRecurringDays(List<DayOfWeek> recurringDays) {
        this.recurringDays = recurringDays;
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        if(endDate == null){
            DeBugConsole.log("getEndDate為null,更改為startDate");
            return startDate;
        }
        return endDate;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public String getStartTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter);
    }
    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    public void setStartTime(LocalTime startTime) {
        if(startTime.isAfter(endTime)){
            DeBugConsole.log("setStartTime is Error!,startTime is after endTime");
        }
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        if(endTime == null){
            return startTime.plusHours(+6);
        }
        return endTime;
    }
    public String getEndTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return endTime.format(formatter);
    }
    public void setEndTime(LocalTime endTime) {
        if(endTime.isBefore(startTime)){
            DeBugConsole.log("setEndTime is Error!,endTime is before startTime");
        }
        this.endTime = endTime;
    }
    private static LocalTime convertStringToLocalTime(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format: " + timeString);
            return null;
        }
    }
    public void SetID(int ID) {
        if(ID <0){
            this.ID = ID;
        }else{
            DeBugConsole.log("多次更改"+ getName() +"的ID,不被受理");
        }

    }
    public int getID() {
        return ID;
    }

    public enum Status {
        TODO, IN_PROGRESS, COMPLETED, LOCKED
    }

    ///  GENERAL 一般性任務 Experience 一次性任務 BOSS 每天重複性任務
    public enum Type {
        GENERAL, Experience, BOSS
    }
    @Override
    public String toString() {
        return String.format("name=%s description=%s assignee=%s startTime=%s endTime=%s type=%s", name, description, assignee, startTime, endTime, type);
    }
}
