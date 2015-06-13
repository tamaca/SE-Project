package com.example.team.myapplication;


public class LoginState {
    public static boolean logined = false;
    public static String username="guest";
    public static boolean fresh=false;
    public static int getPage() {
        return page;
    }

    public static void setPage(int page) {
        LoginState.page = page;
    }

    //public static String username="Burning";//debug
    private static int page=0;
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
