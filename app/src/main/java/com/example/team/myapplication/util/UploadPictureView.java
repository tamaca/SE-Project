package com.example.team.myapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.team.myapplication.R;

public class UploadPictureView extends LinearLayout {
    public static final int nothing = 0;
    public static final int have_picture = 1;
    public static final int only_background = 2;
    public int currentState = nothing;
    public ImageView noPicture;
    public ImageView picture;
    public ImageView removePicture;

    public void showCurrentState() {
        switch (currentState){
            case nothing:
                noPicture.setVisibility(GONE);
                picture.setVisibility(GONE);
                removePicture.setVisibility(GONE);
                break;
            case have_picture:
                noPicture.setVisibility(GONE);
                picture.setVisibility(VISIBLE);
                removePicture.setVisibility(VISIBLE);
                break;
            case only_background:
                noPicture.setVisibility(VISIBLE);
                picture.setVisibility(GONE);
                removePicture.setVisibility(GONE);
                break;
        }
    }

    public void changeState(int state){
        if(state<0||state>2){
            Toast.makeText(getContext(),"状态错误",Toast.LENGTH_LONG).show();
            return;
        }
        currentState = state;
        showCurrentState();

    }
    public UploadPictureView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_upload_picture, null);
        noPicture = (ImageView) view.findViewById(R.id.imageView2);
        picture = (ImageView) view.findViewById(R.id.imageView3);
        removePicture = (ImageView) view.findViewById(R.id.imageView4);
        addView(view);
        showCurrentState();
    }


    public UploadPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_upload_picture, null);
        noPicture = (ImageView) view.findViewById(R.id.imageView2);
        picture = (ImageView) view.findViewById(R.id.imageView3);
        removePicture = (ImageView) view.findViewById(R.id.imageView4);
        addView(view);
        showCurrentState();
    }

    public UploadPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_upload_picture, null);
        noPicture = (ImageView) view.findViewById(R.id.imageView2);
        picture = (ImageView) view.findViewById(R.id.imageView3);
        removePicture = (ImageView) view.findViewById(R.id.imageView4);
        addView(view);
        showCurrentState();
    }
}
