package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener){
        this.scrollViewListener = scrollViewListener;
    }
    @Override
    protected void onScrollChanged(int x,int y,int oldX,int oldY){
        if(scrollViewListener!=null){
            scrollViewListener.onScrollChanged(this,x,y,oldX,oldY);
        }
    }
}
