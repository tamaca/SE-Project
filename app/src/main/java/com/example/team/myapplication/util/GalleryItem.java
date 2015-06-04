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
 * Created by Y400 on 2015/6/4.
 */
public class GalleryItem extends LinearLayout {
    public TextView textView;
    public ImageView imageView;
    public GalleryItem(Context context) {
        super(context);
    }

    public GalleryItem(Context context,Bitmap bitmap) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_gallery_item, null);
        imageView = (ImageView)view.findViewById(R.id.imageView5);
        imageView.setImageBitmap(bitmap);
    }
    public GalleryItem(Context context,Bitmap bitmap,String date) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_gallery_item, null);
        textView = (TextView)view.findViewById(R.id.textView2);
        textView.setText(date);
        imageView = (ImageView)view.findViewById(R.id.imageView5);
        imageView.setImageBitmap(bitmap);


    }

    public GalleryItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
