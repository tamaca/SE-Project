package com.example.team.myapplication.util;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GeneralActivity extends ActionBarActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this,this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }
    @Override
    public boolean onDown(MotionEvent motionEvent) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if(v>0){
            GeneralActivity.this.finish();
        }
        return false;
    }
}
