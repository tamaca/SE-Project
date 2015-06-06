package com.example.team.myapplication.util;

import android.content.Context;
import android.widget.Toast;
public class MyToast {
    private Toast toast = null;
    private Context context = null;

    public MyToast(Context context) {
        this.context = context;
    }

    public void show(String message) {
        if(toast == null){
            toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            toast.cancel();
            toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
