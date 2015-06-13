package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.team.myapplication.R;

/**
 * Created by Y400 on 2015/6/10.
 */
public class LoadingView extends LinearLayout {
    public LoadingView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        addView(view);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        addView(view);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        addView(view);
    }
}
