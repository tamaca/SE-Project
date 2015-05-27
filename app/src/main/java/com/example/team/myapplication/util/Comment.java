package com.example.team.myapplication.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

public class Comment extends LinearLayout {
    public Comment(Context context,String _userName,String _comment) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment, null);
        TextView textView1 = (TextView)view.findViewById(R.id.user_name_in_comment);
        TextView textView2 = (TextView)view.findViewById(R.id.comment);
        textView1.setText(_userName);
        textView2.setText(_comment);
        addView(view);




    }


}
