package com.example.team.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.ScrollViewListener;

import java.util.ArrayList;


public class RecentActivity extends GeneralActivity implements ScrollViewListener {
    private ArrayList recentItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (galleryItems.size() != pictureCount) {
                if (inLoadingPicture.getVisibility() == View.GONE) {
                    inLoadingPicture.setVisibility(View.VISIBLE);
                    if (getPicture == null) {
                        getPicture = new GetPicture();
                        getPicture.execute((Void) null);
                    }
                }
            }
        }
    }*/
}