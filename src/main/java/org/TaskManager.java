package org;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> taskList = new ArrayList<>();
    private final static TaskManager instance = new TaskManager();
    public static TaskManager getInstance() {
        return instance;
    }
    public void addTask(Task task){
        try{
            taskList.add(task);
            DeBugConsole.log("成功新增任務" + task.toString());
            //TODO: 如果Task是In_process 就新增至GOOGLE CALENDAR上
            if (task.getStatus() == Task.Status.IN_PROGRESS) {
                GoogleCalendarAuthorization calendarAuthorization = new GoogleCalendarAuthorization();
                calendarAuthorization.addTaskToGoogleCalendar(task);
                System.out.println("Task added to Google Calendar: " + task.getName());
            }
        }catch(Exception e){
            System.out.println("addTask is Error!");
            System.out.println(e);
        }


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

    public boolean ChangeTaskData(List<Task> taskList) {
        //todo: this.taskList -> DataBase
        this.taskList = taskList;
        return true;
    }

    /**
     * 當準備更新任務節點畫面時，請呼叫此函式
     * @param taskList
     */
    public void UpdateTaskManagerInterface(List<Task> taskList) {

    }
}