package com.example.team.myapplication.util;

/**
 * Created by Y400 on 2015/6/3.
 */
public class CheckValid {
    static public boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() <= 15;
    }

    static public boolean isEmailValid(String email) {

        return email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    }

    static public boolean isUserNameValid(String userName) {
        return userName.length() >= 2 && userName.length() <= 20;
    }

    static public boolean isInputValid(String str) {
        return str.length() < 20 && str.charAt(0) != ' ';
    }
}