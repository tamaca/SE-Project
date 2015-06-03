package com.example.team.myapplication.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.team.myapplication.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Y400 on 2015/6/3.
 */
public class OverwriteAdapter extends SimpleAdapter {
    // 颜色
    private int[] colors = {R.color.白色,R.color.深灰色};

    public OverwriteAdapter(Context context,
                            List<? extends Map<String, ?>> data, int resource,
                            String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundResource(colors[position % 2]);
        return view;
    }
}