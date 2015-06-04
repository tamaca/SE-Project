package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.ScrollViewListener;

import java.util.ArrayList;

public class UserPageActivity extends GeneralActivity implements ScrollViewListener {
    static final public int normal = 0;
    static final public int concern = 1;
    static final public int isConcerned = 2;
    static final public int hate = 4;
    static final public int isHated = 8;

    static public int relationship = normal;

    private boolean isMe = false;
    private TextView name;
    private Button concernButton;
    private Button hateButton;
    private Button manageButton;
    private String userName;
    private LinearLayout gallery;
    private Toast toast;
    private ProgressBar progressBar;
    private GetPicture getPicture = null;
    private LinearLayout scrollContent;
    private Thread changeRelationship;
    private int pictureCount;
    public ArrayList<GalleryItem> galleryItems = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Intent intent = getIntent();
        userName = intent.getExtras().get("user_name").toString();
        name = (TextView) findViewById(R.id.name);
        name.setText(userName);
        if (userName.equals(LoginState.username)) {
            isMe = true;
        }
        setTitle((isMe ? "我" : userName) + "的个人主页");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化变量
        concernButton = (Button) findViewById(R.id.button6);
        hateButton = (Button) findViewById(R.id.button5);
        manageButton = (Button) findViewById(R.id.button8);
        gallery = (LinearLayout) findViewById(R.id.gallery);
        galleryItems = new ArrayList<>();
        toast = null;
        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        scrollContent = (LinearLayout) findViewById(R.id.scroll_content);
        changeRelationship = null;
        //////////

        loadView(isMe);


    }

    public void loadView(boolean my) {
        if (my) {
            //加载我的个人主页
            if (!getGallery(userName)) {
                findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            }
            concernButton.setVisibility(View.GONE);
            hateButton.setVisibility(View.GONE);

        } else {
            //加载别人的主页
            //TODO 获取用户和这个人的关系
            manageButton.setVisibility(View.GONE);
            concernButton.setOnClickListener(new OnClickConcernListener());
            hateButton.setOnClickListener(new OnClickHateListener());

            relationship = hate;//测试用
            switch (relationship) {
                case normal:
                case isConcerned:
                case isHated:
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case concern:
                case concern | isConcerned:
                    if (!getGallery(userName)) {
                        findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                    }
                    concernButton.setText(getString(R.string.remove_from_concern));
                    break;
                case hate:
                case hate | isHated:
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    gallery.setVisibility(View.GONE);
                    hateButton.setText(getString(R.string.remove_from_blackList));
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_page, menu);
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
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void toViewPictureActivity(View view) {
        Intent intent = new Intent(this, ViewPictureActivity.class);
        //view.setDrawingCacheEnabled(true);
        //Bitmap bitmap = view.getDrawingCache();
        String bigurl = view.getContentDescription().toString();
        intent.putExtra("bigurl", bigurl);
        startActivity(intent);
    }

    //添加图片进gallery的方法
    public void addGalleryItem(Bitmap bitmap) {
        GalleryItem galleryItem = new GalleryItem(this, bitmap);
        galleryItem.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPictureActivity(view);
            }
        });

        galleryItems.add(galleryItem);
        gallery.addView(galleryItems.get(galleryItems.size() - 1));

    }

    public boolean getGallery(String userName) {
        //TODO 在这里加载该用户的图片，如果用户没有图片，返回false,如果有，把 pictureCount 设置为该用户照片总数；
        // addGalleryItem(bitmap);
        pictureCount = 0;
        return false;
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if(galleryItems.size()!=pictureCount) {
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (getPicture == null) {
                        getPicture = new GetPicture();
                        getPicture.execute((Void) null);
                    }
                }
            }
        }
    }

    class OnClickConcernListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //点击 关注/取消关注 按钮
            switch (relationship) {
                case hate:
                    //TODO 把该用户从黑名单中移除。

                case normal:
                    //TODO 关注该用户。
                    findViewById(R.id.textView3).setVisibility(View.GONE);
                    concernButton.setText(getString(R.string.remove_from_concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = concern;
                    findViewById(R.id.textView4).setVisibility(View.GONE);
                    gallery.setVisibility(View.VISIBLE);
                    if (!getGallery(userName)) {
                        findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                    }
                    break;
                case isConcerned:
                    //TODO 关注该用户。
                    concernButton.setText(getString(R.string.remove_from_concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = concern | isConcerned;
                    findViewById(R.id.textView4).setVisibility(View.GONE);
                    gallery.setVisibility(View.VISIBLE);
                    if (!getGallery(userName)) {
                        findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                    }
                    break;
                case isHated:
                case hate | isHated:
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), "您已被该用户拉入黑名单", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast.cancel();
                        toast = Toast.makeText(getApplicationContext(), "您已被该用户拉入黑名单", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                case concern:
                    //TODO 取关该用户
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = normal;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case concern | isConcerned:
                    //TODO 取关该用户
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = isConcerned;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class OnClickHateListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //我 拉黑/取黑 该用户
            switch (relationship) {
                case concern:
                    //TODO 取关该用户
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                case normal:
                    //TODO 拉黑该用户
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.remove_from_blackList));
                    relationship = hate;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case isHated:
                    //TODO 拉黑该用户
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.remove_from_blackList));
                    relationship = hate | isHated;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case isConcerned:
                    //TODO 我拉黑该用户，该用户被迫取关我
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.remove_from_blackList));
                    relationship = hate;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case hate:
                    //TODO 把该用户从黑名单中移除
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = normal;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case hate | isHated:
                    //TODO 把该用户从黑名单中移除
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.add_to_blacklist));
                    relationship = isHated;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
                case concern | isConcerned:
                    //TODO 取关该用户并拉黑，该用户被迫取关我
                    concernButton.setText(getString(R.string.concern));
                    hateButton.setText(getString(R.string.remove_from_blackList));
                    relationship = hate;
                    gallery.setVisibility(View.GONE);
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class GetPicture extends AsyncTask<Void, Void, Boolean> {
        public GetPicture() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                //TODO 添加几个图片到 galleryItems 里
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            getPicture = null;
            if (success) {
                gallery.postInvalidate();
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