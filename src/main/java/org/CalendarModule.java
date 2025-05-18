package org;
import org.Task.Task;
import org.Task.TaskManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
public class CalendarModule {
    // 取得 2025 年 5 月的所有任務（以每日分組）
    //使用範例: Map<LocalDate, List<Task>> mayTasks = calendar.getMonthlyTasks(2025, 5);
    private final TaskManager taskManager;
    private final Map<LocalDate, List<Task>> taskCache = new HashMap<>();

    public CalendarModule(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // 取得該月所有任務，依照每天分類
    public Map<LocalDate, List<Task>> getMonthlyTasks(int year, int month) {
        Map<LocalDate, List<Task>> monthMap = new HashMap<>();
        LocalDate date = LocalDate.of(year, month, 1);
        int daysInMonth = date.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate day = LocalDate.of(year, month, i);
            monthMap.put(day, getTasksByDate(day));
        }

        return monthMap;
    }

    // 點某一天會拿到該日任務（快取）
    public List<Task> getTasksByDate(LocalDate date) {
        if (taskCache.containsKey(date)) {
            return taskCache.get(date);
        }

        List<Task> allTasks = taskManager.getTaskList();
        List<Task> tasksForDate = allTasks.stream()
                .filter(task -> isTaskActiveOnDate(task, date))
                .collect(Collectors.toList());

        taskCache.put(date, tasksForDate);
        return tasksForDate;
    }

    // 判斷任務是否在特定日期執行
    private boolean isTaskActiveOnDate(Task task, LocalDate date) {
        // 一般一次性任務
        if (task.getRecurringDays() == null || task.getRecurringDays().isEmpty()) {
            return date.equals(task.getStartDate());
        }

        // 有重複的任務
        if ((task.getStartDate() != null && date.isBefore(task.getStartDate())) ||
                (task.getEndDate() != null && date.isAfter(task.getEndDate()))) {
            return false;
        }

        return task.getRecurringDays().contains(date.getDayOfWeek());
    }

    // 當任務新增/修改/刪除時清除快取
    public void refresh() {
        taskCache.clear();
    }
}

