package org;

import java.time.LocalDate;

public class UserInfo {
    private final String username;
    private final LocalDate createDate;
    private static int userID;

    // 靜態變數，存儲目前登入的使用者
    private static UserInfo currentUser;

    public UserInfo(String username, LocalDate createDate, int userID) {
        this.username = username;
        this.createDate = createDate;
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
