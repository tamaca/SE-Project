package com.example.team.myapplication.util;

/**
 * Created by coco on 2015/6/11.
 */
public class MyException {
    public static class getException extends Exception {
        public String name = "get";
    }

    public static class nullException extends Exception {
        public String name = "null";
    }

    public static class executeException extends Exception {
        public String name = "execute";
    }

    public static class zeroException extends Exception {
        public String name = "countzero";
    }

    public static class userNotExistException extends Exception {
        public String name = "usernotexist";
    }
    public static class passwordWrongException extends Exception {
        public String name = "passwordwrong";
    }
    public static class usernameAlreadyExistException extends Exception {
        public String name = "usernamealreadyexist";
    }
    public static class emailAlreadyExistException extends Exception {
        public String name = "emailalreadyexist";
    }
}
