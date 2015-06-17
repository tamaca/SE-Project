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
    public static class packageException extends Exception {
        public String name = "package";
    }
    public static class postException extends Exception {
        public String name = "post";
    }
    public static class emailInvalidException extends Exception {
        public String name = "emailinvalid";
    }
    public static class emailNotMatchException extends Exception {
        public String name = "emailnotmatch";
    }
    public static class passwordWrongException extends Exception {
        public String name = "passwordwrong";
    }
    public static class oldPasswordNotMatchException extends Exception {
        public String name = "oldpasswordnotmatch";
    }
    public static class passwordInvalidException extends Exception {
        public String name = "passwordinvalid";
    }
}
