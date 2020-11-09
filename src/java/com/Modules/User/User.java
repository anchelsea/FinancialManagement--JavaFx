package com.Modules.User;


import com.Infrastructure.Base.BaseModel;

public class User extends BaseModel {

    private String username;
    private String email;
    private String password;
    private int confirm;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username,String email,  String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String username,String email,  String password, int confirm) {
        this.email = email;
        this.username = username;
        this.confirm = confirm;
        this.password = password;
    }

    public static String getTable() {
        return "users";
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() { return password; }
    public int getConfirm() { return confirm; }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password;}



    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='"+
                ", confirm=" + confirm +
                '}';
    }


}
