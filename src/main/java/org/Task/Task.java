package org.Task;
import org.DeBugConsole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        public Task(String name, String description, String assignee,LocalDate startDate , LocalTime startTime,LocalDate endDate, LocalTime endTime,List<DayOfWeek> recurringDays,Type type) {
        //制式化任務範例
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.TODO;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurringDays = recurringDays;
        this.type = type;
    }
    public Task(String name, String description, String assignee,LocalDate startDate, LocalTime startTime) {
        //一次性任務範例
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.TODO;
        this.type = Type.Experience;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = null;
        this.endTime = null;
        this.recurringDays = null;

    }

    public Task(String name, String description, String assignee,LocalDate startDate, LocalTime startTime,LocalDate endDate, LocalTime endTime,Status status, Type type,List<DayOfWeek> recurringDays) {
        //資料庫抓下來的所有任務
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.status = status;
        this.type = type;
        this.recurringDays = recurringDays;

    }

    public String getName() {
        if(name == null || name.isEmpty()) return "Unknown";
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
            DeBugConsole.log("已將任務 "+name+" 調至完成");
        }
        if(status == Status.IN_PROGRESS && this.status == Status.TODO ){
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
    public int getRecurringDaysInt() {
        int result = 0;
        for (DayOfWeek day : recurringDays) {
            int bitIndex = day.getValue() - 1; // 0~6
            result |= (1 << bitIndex);
        }
        DeBugConsole.log( Integer.toString(result) );
        return result;
    }
    public static List<DayOfWeek> intToRecurringDays(int value) {
        List<DayOfWeek> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if ((value & (1 << i)) != 0) {
                days.add(DayOfWeek.of(i + 1));
            }
        }
        return days;
    }

    public void setRecurringDays(List<DayOfWeek> recurringDays) {
        this.recurringDays = recurringDays;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public String getStartDateString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            if(startDate == null){
                return "無開始日期";
            }
            return startDate.format(formatter);
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public String getEndDateString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            if(endDate == null){
                return "無結束日期";
            }
            return endDate.format(formatter);
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public String getStartTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if(startTime == null){
            return "XX:XX";
        }
        return startTime.format(formatter);
    }
    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    public void setStartTime(LocalTime startTime) {
        if(endTime != null && startTime.isAfter(endTime)){
            DeBugConsole.log("setStartTime is Error!,startTime is after endTime");
            return;
        }
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {

        return endTime;
    }
    public String getEndTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if(endTime == null){
            return "XX:XX";
        }
        return endTime.format(formatter);
    }
    public void setEndTime(LocalTime endTime) {
        if(startTime != null && endTime.isBefore(startTime)){
            DeBugConsole.log("setEndTime is Error!,endTime is before startTime");
            return;
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

    public boolean isTaskOnTimeCheck() {
            if((startDate != null && !startDate.isAfter(LocalDate.now())&& !type.equals(Task.Type.Experience))){
                //TODO:通知使用者 日期已到
                return true;    //非一次性任務時，當開始日期>=目前日期 任務開始
            }else if (startDate !=null && startTime != null && !startDate.isAfter(LocalDate.now()) && startTime.isAfter(LocalTime.now()) && type.equals(Task.Type.Experience)) {
                //TODO 通知使用者 時間已到
                return true;    //一次性任務時，當開始時間 >= 目前時間 任務開始
            }
            return false;
    }


    public enum Status {
            //使用範例 : 獲得code Status.IN_PROGRESS.getCode(); => 1
            //使用範例  code=>Status Status.getStatus(1) => Status.IN_PROGRESS
        TODO(0), IN_PROGRESS(1), COMPLETED(2), LOCKED(3);
        Status(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public static Status getStatus(int code) {
            for (Status status : Status.values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
        public String GetString(){
            switch (this){
                case TODO:
                    return "尚未開始";
                    case IN_PROGRESS:
                        return "進行中";
                        case COMPLETED:
                            return "完成";
                            case LOCKED:
                                return "封鎖";
                default : return "未知";
            }
        }
        private final int code;
    }


    ///  GENERAL 一般性任務 Experience 一次性任務 BOSS 每天重複性任務
    public enum Type {
        //使用範例 : 獲得code Type.GENERAL.getCode(); => 0
        //使用範例  code=>Status Type.getStatus(0) => Type.GENERAL
        GENERAL(0), Experience(1), BOSS(2);
        Type(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public static Type getType(int code) {
            for (Type type : Type.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
        public String GetString() {
            switch (this){
                case GENERAL:
                    return "一般";
                    case BOSS:
                        return "重要";
                case Experience:
                    return "一次性";
                default: return  "未知";

            }
        }
        private final int code;
    }
    @Override
    public String toString() {
        return String.format("name=%s description=%s assignee=%s startTime=%s endTime=%s type=%s", name, description, assignee, startTime, endTime, type);
    }
}
