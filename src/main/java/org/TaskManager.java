package org;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> taskList = new ArrayList<>();
    private final static TaskManager instance = new TaskManager();
    public static TaskManager getInstance() {
        return instance;
    }
    public void AddTask(Task task){
        try{
            taskList.add(task);
            CheckAndUpdateTaskInGoogleCalendar(task);
            DeBugConsole.log("成功新增任務" + task.toString());

        }catch(Exception e){
            System.out.println("addTask is Error!");
            System.out.println(e);
        }


    }
    public Task CreateTask(String taskName, String description, LocalDate startDate,int startHour, int startMinute, LocalDate endDate, int endHour, int  endMinute){
        //Todo:判斷任務為一次性 重複 又或著是每天 然後新增任務
        return null;
    }
    public void updateStatus(Task task,Task.Status newStatus){
        task.setStatus(newStatus);
    }
    public List<Task> getTaskList(){
        return taskList;
    }
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskList.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     *  當要把資料從資料庫抓下來時使用
     * @param taskList
     * @return bool
     */
    public boolean FetchDataFromDatabase(List<Task> taskList) {
        //todo:  DataBase -> this.taskList
        this.taskList = taskList;
        return true;
    }

    /**
     * 當需要上傳資料至資料庫中使用
     * @return
     */
    public boolean UploadDataToDatabase() {
        //todo: this.taskList -> DataBase
        return true;
    }
    /**
     * 當更新任務時，請呼叫此函式
     * @param taskList
     */
    public void NotifyAllUsersDataChanged(List<Task> taskList) {
        //todo:當任務更新時 通知團隊中的其他人 必須更新資料庫資料
    }

    /**
     * 確認是否需上傳至GoogleCalendar
     * @throws Exception
     */
    public void CheckAndUpdateTaskInGoogleCalendar(Task task) throws Exception{
        //TODO: 如果Task是In_process 就新增至GOOGLE CALENDAR上
        if (task.getStatus() == Task.Status.IN_PROGRESS) {
            GoogleCalendarAuthorization calendarAuthorization = new GoogleCalendarAuthorization();
            calendarAuthorization.addTaskToGoogleCalendar(task);
            System.out.println("Task added to Google Calendar: " + task.getName());
        }
    }
}