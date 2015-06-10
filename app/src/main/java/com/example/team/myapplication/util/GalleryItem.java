package com.example.team.myapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.R;

public class GalleryItem extends LinearLayout {
    public TextView textView;
    public ImageView imageView;
    public ImageButton removeButton;

    public GalleryItem(Context context) {
        super(context);
    }

    public GalleryItem(Context context, Bitmap bitmap) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_gallery_item, null);
        imageView = (ImageView) view.findViewById(R.id.imageView5);
        imageView.setImageBitmap(bitmap);
        removeButton = (ImageButton) view.findViewById(R.id.imageButton6);
        addView(view);
    }

    public GalleryItem(Context context, Bitmap bitmap, String date) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_gallery_item, null);
        textView = (TextView) view.findViewById(R.id.textView2);
        textView.setText(date);
        imageView = (ImageView) view.findViewById(R.id.imageView5);
        imageView.setImageBitmap(bitmap);
        removeButton = (ImageButton) view.findViewById(R.id.imageButton6);
        addView(view);

    }

    public void setRemovable(boolean removable) {
        if (removable)
            removeButton.setVisibility(VISIBLE);
        else
            removeButton.setVisibility(GONE);
    }

    public GalleryItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}