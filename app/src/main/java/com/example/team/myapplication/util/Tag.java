package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

/**
 * Created by Y400 on 2015/6/5.
 */
public class Tag extends LinearLayout {
    private TextView tagView = null;
    public Tag(Context context) {
        super(context);
    }

    public Tag(Context context, String tag) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, null);
        tagView = (TextView) view.findViewById(R.id.showing_tag);
        tagView.setText(tag);
        addView(view);
    }
    public Tag(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Tag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
