package com.example.team.myapplication.util;

/**
 * Created by coco on 2015/6/11.
 */
public class myException {
    public static class getException extends java.lang.Exception {
        public String name = "get";
    }

    public  static class nullException extends java.lang.Exception {
        public String name = "null";
    }

    public static  class executeException extends java.lang.Exception {
        public String name = "execute";
    }
    public static class zeroException extends java.lang.Exception {
        public String name = "countzero";
    }
}
