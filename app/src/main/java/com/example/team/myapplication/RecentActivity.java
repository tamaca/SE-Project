package com.example.team.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.RecentItem;
import com.example.team.myapplication.util.RefreshableView;
import com.example.team.myapplication.util.ScrollViewListener;

import java.util.ArrayList;


public class RecentActivity extends Activity implements ScrollViewListener {
    private LinearLayout scrollContent;
    private ArrayList<RecentItem> recentItems;
    private MyScrollView myScrollView;
    private ProgressBar inLoadingPicture;
    private Toast toast = null;
    private GetPicture getPicture = null;
    private int pictureCount = 0;
    private RefreshableView refreshableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 变量初始化
         */
        scrollContent = (LinearLayout) findViewById(R.id.linearLayout5);
        recentItems = new ArrayList<>();
        myScrollView = (MyScrollView) findViewById(R.id.scrollView4);
        inLoadingPicture = (ProgressBar) findViewById(R.id.progressBar6);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        ////////
        myScrollView.setScrollViewListener(this);
        refreshableView.setOnRefreshListener(new MyRefreshListener(), 0);
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

    /**
     * 添加图片时请使用这个函数，注意每次只能添加一个进recentItems
     */
    public void addRecentItem(String author, String date, Bitmap bitmap) {
        RecentItem recentItem = new RecentItem(RecentActivity.this, author, date, bitmap);
        recentItems.add(recentItem);
    }

    /**
     * 这是添加完成之后的UI操作。
     */
    public void addRecentItemsToView() {
        scrollContent.removeAllViews();
        for (int i = 0; i < recentItems.size(); i++) {
            scrollContent.addView(recentItems.get(i));
        }

    }

    public void getRecent() {
        //TODO 上拉刷出来几个新图片，按照时间排序，新的先加进去。

    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {

        //处理上划查看更多图片
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {

            if (inLoadingPicture.getVisibility() == View.GONE) {
                inLoadingPicture.setVisibility(View.VISIBLE);
                if (getPicture == null) {
                    getPicture = new GetPicture();
                    getPicture.execute((Void) null);
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
                //TODO 在这里写上拉刷新的操作
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
                scrollContent.postInvalidate();
            } else {

            }
        }
    }

    class MyRefreshListener implements RefreshableView.PullToRefreshListener {
        public MyRefreshListener() {

        }

        @Override
        public void onRefresh() {
            try {
                recentItems.clear();
                //TODO 在这里写下拉刷新时的操作
                Thread.sleep(3000);
                scrollContent.post(new Runnable() {
                    @Override
                    public void run() {
                        addRecentItemsToView();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            refreshableView.finishRefreshing();
        }
    }
}