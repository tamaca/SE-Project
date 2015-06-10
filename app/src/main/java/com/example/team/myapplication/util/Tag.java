package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

public class Tag extends LinearLayout {
    /**
     * 正常显示标签状态
     */
    public static final int showingTag = 0;
    /**
     * 显示可编辑状态
     */
    public static final int removable = 1;
    /**
     * 显示可添加状态
     */
    public static final int addable = 2;
    /**
     * 什么都不显示
     */
    public static final int showNothing = 3;

    public int currentState = showNothing;
    /**
     * 组件设置为public利于编写
     */
    public TextView tagText = null;
    public ImageButton removeButton = null;
    public ImageButton addButton = null;

    public Tag(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, null);
        tagText = (TextView) view.findViewById(R.id.showing_tag);
        removeButton = (ImageButton) view.findViewById(R.id.imageButton4);
        addButton = (ImageButton) view.findViewById(R.id.imageButton5);
        addView(view);
        changeState(currentState); //初始化为什么都不显示的状态
    }

    public void changeState(int state) {
        switch (state) {
            case showingTag:
                tagText.setVisibility(VISIBLE);
                removeButton.setVisibility(GONE);
                addButton.setVisibility(GONE);
                currentState = showingTag;
                break;
            case removable:
                tagText.setVisibility(VISIBLE);
                removeButton.setVisibility(VISIBLE);
                addButton.setVisibility(GONE);
                currentState = removable;
                break;
            case addable:
                tagText.setVisibility(GONE);
                removeButton.setVisibility(GONE);
                addButton.setVisibility(VISIBLE);
                currentState = addable;
                break;
            case showNothing:
                tagText.setVisibility(GONE);
                removeButton.setVisibility(GONE);
                addButton.setVisibility(GONE);
                currentState = showNothing;
                break;
        }
    }

    public void changeToEditState(boolean edit){
        if(edit){
            if(currentState == showingTag){
                changeState(removable);
            }
            if(currentState == showNothing){
                changeState(addable);
            }
        }
        else{
            if(currentState == addable){
                changeState(showNothing);
            }
            if(currentState == removable){
                changeState(showingTag);
            }
        }
    }

    public Tag(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, null);
        tagText = (TextView) view.findViewById(R.id.showing_tag);
        removeButton = (ImageButton) view.findViewById(R.id.imageButton4);
        addButton = (ImageButton) view.findViewById(R.id.imageButton5);
        addView(view);
    }

    public Tag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tag, null);
        tagText = (TextView) view.findViewById(R.id.showing_tag);
        removeButton = (ImageButton) view.findViewById(R.id.imageButton4);
        addButton = (ImageButton) view.findViewById(R.id.imageButton5);
        addView(view);
    }
}
