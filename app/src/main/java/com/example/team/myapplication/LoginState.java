package com.example.team.myapplication;


public class LoginState {
    public static boolean logined = false;
    public LoginState(){

    }
    public static void setLogined(boolean _logined){
        logined = _logined;
    }
    public static boolean getLogined(){
        return logined;
    }
}
