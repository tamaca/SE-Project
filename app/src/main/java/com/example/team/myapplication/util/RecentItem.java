package com.example.team.myapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
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
    public TextView author;
    public TextView time;
    public ImageView imageView;

    public RecentItem(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recent_item, null);
        author = (TextView)view.findViewById(R.id.textView6);
        time = (TextView)view.findViewById(R.id.textView7);
        imageView = (ImageView)view.findViewById(R.id.imageView9);
        addView(view);
    }
    public RecentItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecentItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
