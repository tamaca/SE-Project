package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

/**
 * Created by Y400 on 2015/6/5.
 */
public class RecentItem extends LinearLayout {
    private TextView author;
    private TextView time;
    private ImageView imageView;
    public RecentItem(Context context) {
        super(context);
    }
    public RecentItem(Context context,String author,String time) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recent_item, null);
        this.author = (TextView)view.findViewById(R.id.textView6);
        this.time = (TextView)view.findViewById(R.id.textView7);
        this.imageView = (ImageView)view.findViewById(R.id.imageView9);



    }
    public RecentItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecentItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
