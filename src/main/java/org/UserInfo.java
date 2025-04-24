package org;

import java.time.LocalDate;

public class UserInfo {
    private final String username;
    private String password;
    private final LocalDate createDate;
//    private final int userID;
    private UserInfo(String username, String password) {
//        userID希望可以根據申請人數給編號 ex:000001 , 000123
        this.username = username;
        this.password = password;
        this.createDate = LocalDate.now();
    }

    public String getUsername() {
        return username;
    }

    public String getCreateDate(){
        return createDate.toString();
    }

    public void chagePassword(String newPassword) {
        if (password.equals(newPassword)) {
            System.out.println("You cannot  reset your password to an original password, please change your password.");
        }
        else{
            password = newPassword;
        }
    }

}
