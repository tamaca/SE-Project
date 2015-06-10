package com.example.team.myapplication.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

public class Comment extends LinearLayout {
    public TextView textView1;
    public TextView textView2;

    public String getCommentid() {
        return commentid;
    }

    private String commentid;
    public Comment(Context context, final String _userName,String _comment,String commentid) {
        super(context);
        this.commentid=commentid;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment, null);
        textView1 = (TextView)view.findViewById(R.id.user_name_in_comment);
        textView2 = (TextView)view.findViewById(R.id.comment);
        textView1.setText(_userName);
        textView2.setText(_comment);
        addView(view);
    }
}
