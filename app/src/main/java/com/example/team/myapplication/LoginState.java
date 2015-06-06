package com.example.team.myapplication;


public class LoginState {
    public static boolean logined = false;
    public static String username="guest";
    public static int page=0;//0为初始页
    public LoginState(){
    }
    public static void setLogined(boolean _logined,String username){
        logined = _logined;
        LoginState.username=username;
    }
    public static boolean getLogined(){
        return logined;
    }
}
