package com.example.team.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.LoadingView;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.RecentItem;
import com.example.team.myapplication.util.RefreshableView;
import com.example.team.myapplication.util.ScrollViewListener;

import org.json.JSONObject;

import java.util.ArrayList;


public class RecentActivity extends GeneralActivity implements ScrollViewListener {
    private LinearLayout scrollContent;
    private LinearLayout scrollContentLeft;
    private LinearLayout scrollContentRight;

    private LoadingView loadingView;
    private ArrayList<RecentItem> recentItems;
    private MyToast myToast;
    private GetPicture getPicture = null;
    private int pictureCount = 0;
    private RefreshableView refreshableView;
    private int page = 1;
    private DB db;
    private boolean end = false;
    private Context context=this;
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
        MyScrollView myScrollView = (MyScrollView) findViewById(R.id.scrollView4);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        scrollContentLeft = (LinearLayout) findViewById(R.id.linearLayout6);
        scrollContentRight = (LinearLayout) findViewById(R.id.linearLayout7);
        myToast = new MyToast(this);
        loadingView = new LoadingView(this);
        db = new DB(this);
        /**
         * 添加监听器
         */

        refreshableView.setOnRefreshListener(new MyRefreshListener(), 0);
        /**
         * 请在getRecent里获得所有动态
         */
        getPicture=new GetPicture();
        getPicture.execute();
        //getRecent();

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
    public void refreshRecentItems() {
        scrollContentLeft.removeAllViews();
        scrollContentRight.removeAllViews();
        for (int i = 0; i < recentItems.size(); i++) {
            if (i % 2 == 0) {
                scrollContentLeft.addView(recentItems.get(i));
            } else {
                scrollContentRight.addView(recentItems.get(i));
            }
        }

    }

    public void getRecent() {
        //TODO 刷出来几个新图片，按照时间排序，新的先加进去，如果使用 新线程 来实现，请在执行完成之后在 UI线程 使用方法 refreshRecentItems()

    }

    public void toViewPictureActivity(View view) {
        Intent intent = new Intent(this, ViewPictureActivity.class);
        //view.setDrawingCacheEnabled(true);
        //Bitmap bitmap = view.getDrawingCache();\
        try {
            String imageviewJsonString = view.getContentDescription().toString();
            JSONObject imageviewJson = new JSONObject(imageviewJsonString);
            String bigurl = imageviewJson.getString("imagebigurl");
            String id = imageviewJson.getString("imageid");
            intent.putExtra("type", "online");
            intent.putExtra("bigurl", bigurl);
            intent.putExtra("imageid", id);
            startActivity(intent);
        } catch (Exception e) {
            //解码错误
        }
    }

    /**
     * 上划查看更多图片
     *
     * @param scrollView
     * @param x
     * @param y
     * @param oldX
     * @param oldY
     */
    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (!end && scrollContent.getChildAt(scrollContent.getChildCount() - 1) != loadingView) {
                if (getPicture == null) {
                   /* RecentItem recentItems[] = new RecentItem[8];
                    for (int i = 0; i < 8; i++) {
                        recentItems[i] = new RecentItem(this);
                    }
                    getPicture = new GetPicture(recentItems);*/
                    getPicture=new GetPicture();
                    getPicture.execute();
                }
            }
        }
    }

    //上拉刷新
    class GetPicture extends AsyncTask<Void, Void, Boolean> {
        private RecentItem newrecentItems[];

        /*public GetPicture(RecentItem recentItem[]) {
            this.newrecentItems = recentItem;
        }*/
        @Override
        protected void onPreExecute() {
            newrecentItems=new RecentItem[8];
            for (int i = 0; i < 8; i++) {

                newrecentItems[i] = new RecentItem(context);
            }
            scrollContent.addView(loadingView);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url1 = "http://192.168.253.1/" + LoginState.username + "/concerned_image/page/" + page + "/";
                new JsonGet(url1, newrecentItems, db, "recent");
                //TODO 在这里写上拉刷新的操作
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            } catch (MyException.zeroException e) {
                //TODO:没有下一页图片了
                end = true;
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            scrollContent.removeView(loadingView);
            getPicture = null;
            if (success) {
                for (RecentItem _recentItem : recentItems) {
                    if (_recentItem.imageView.getContentDescription() != null) {
                        recentItems.add(_recentItem);
                        _recentItem.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toViewPictureActivity(v);
                            }
                        });
                    }
                }
                refreshRecentItems();
                scrollContent.postInvalidate();
                page++;
            } else {
                //TODO:获取图片错误
            }
        }
    }

    //下拉刷新
    class MyRefreshListener implements RefreshableView.PullToRefreshListener {

        public MyRefreshListener() {
        }

        @Override
        public void onRefresh() {
            try {
                recentItems.clear();
                page = 1;
                getPicture = new GetPicture();
                getPicture.execute();
                //TODO 在这里写下拉刷新时的操作
                /*
                Thread.sleep(3000);
                scrollContent.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshRecentItems();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                myToast.show(getString(R.string.toast_refreshing_error));
            }*/
                refreshableView.finishRefreshing();
            } catch (Exception e) {

            }
        }
    }
}