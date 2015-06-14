package com.example.team.myapplication.util;

import java.util.ArrayList;

public class CheckValid {
    static public boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() <= 15;
    }

    static public boolean isEmailValid(String email) {

        return email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    }

    static public boolean isUserNameValid(String userName) {
        return userName.getBytes().length >= 2 && userName.getBytes().length <= 14 && userName.matches("^[A-Za-z0-9_\u4e00-\u9fa5]{2,15}$");
    }

    static public boolean isInputValid(String str) {
        return str.length() < 15 && !str.contains(" ") && str.matches("^[A-Za-z0-9_\u4e00-\u9fa5]{2,15}$");
    }

    static public boolean isTagUnique(ArrayList<Tag> tags, String tagContent) {
        for (int i = 0; i < 5; i++) {
            if (tags.get(i).tagText.getText().toString().equals(tagContent)) {
                return false;
            }
        }
        return true;
    }

    static public boolean isTagValid(String tagContent) {
        return tagContent.getBytes().length<12  && !tagContent.contains(" ") && tagContent.matches("^[A-Za-z0-9_\u4e00-\u9fa5]{2,15}$");
    }
}