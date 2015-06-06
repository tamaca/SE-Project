package com.example.team.myapplication.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Y400 on 2015/6/6.
 */
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
