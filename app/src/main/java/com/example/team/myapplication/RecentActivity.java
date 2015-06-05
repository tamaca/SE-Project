package com.example.team.myapplication;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.RecentItem;
import com.example.team.myapplication.util.ScrollViewListener;

import java.util.ArrayList;


public class RecentActivity extends GeneralActivity implements ScrollViewListener {
    private LinearLayout scrollContent;
    private ArrayList<RecentItem> recentItems;
    private MyScrollView myScrollView;
    private ProgressBar inLoadingPicture;
    private Toast toast = null;
    private GetPicture getPicture = null;
    private int pictureCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //变量初始化
        scrollContent = (LinearLayout) findViewById(R.id.linearLayout5);
        recentItems = new ArrayList<>();
        myScrollView = (MyScrollView) findViewById(R.id.scrollView4);
        inLoadingPicture = (ProgressBar) findViewById(R.id.progressBar6);
        ////////
        myScrollView.setScrollViewListener(this);
        getRecent();

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
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addRecentItem(String author,String date ,Bitmap bitmap){
        RecentItem recentItem = new RecentItem(RecentActivity.this,author,date,bitmap);
        recentItems.add(recentItem);
    }
    public void addRecentItemsToView(){//在第一次加载完成 和刷新 之后调用
        scrollContent.removeAllViews();
        for (int i = 0;i < recentItems.size();i++){
            scrollContent.addView(recentItems.get(i));
        }
    }
    public void getRecent(){
        //TODO 刷出来几个新图片，按照时间排序，新的先加进去。

    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {

        //处理上划查看更多图片
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (recentItems.size() != pictureCount) {
                if (inLoadingPicture.getVisibility() == View.GONE) {
                    inLoadingPicture.setVisibility(View.VISIBLE);
                    if (getPicture == null) {
                        getPicture = new GetPicture();
                        getPicture.execute((Void) null);
                    }
                }
            } else {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "没有更多图片了", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "没有更多图片了", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    class GetPicture extends AsyncTask<Void, Void, Boolean> {
        public GetPicture() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            inLoadingPicture.setVisibility(View.GONE);
            getPicture = null;
            if (success) {

            } else {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }
}
