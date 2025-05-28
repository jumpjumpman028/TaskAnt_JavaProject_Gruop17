package org;

import java.time.LocalDate;

public class UserInfo {
    public static String username;
    private LocalDate createDate;
    public static int userID = 1;

    // 靜態變數，存儲目前登入的使用者
    private static UserInfo currentUser;

    public UserInfo(String username, LocalDate createDate, int userID) {
        this.username = username;
        this.createDate = createDate;
        this.userID = userID;
    }

    public UserInfo(String username, int userID) {
        this.username = username;
        this.userID = userID;
    }

    // 設定目前登入的使用者
    public static void setCurrentUser(UserInfo user) {
        currentUser = user;
    }

    // 取得目前登入的使用者
    public static UserInfo getCurrentUser() {
        return currentUser;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }
}
