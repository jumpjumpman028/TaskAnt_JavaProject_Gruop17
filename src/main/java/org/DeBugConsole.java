package org;
public class DeBugConsole {
    //用於紀錄錯誤發生，
    //使用方法 DeBugConsole.(報錯內容); or DeBugConsole.(Level,報錯內容)
    //開發介面可使用?
    public enum Level {
        INFO, WARNING, ERROR
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        String prefix = "[" + level.name() + "] ";
        System.out.println(prefix + message);
    }

    public static void logException(Exception e) {
        log(Level.ERROR, "Exception occurred: " + e.getMessage());
        log(Level.ERROR, "Stack trace: ");
        for (StackTraceElement element : e.getStackTrace()) {
            log(Level.ERROR, element.toString());
        }
    }
}
