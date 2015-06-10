package com.example.team.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.LoadingView;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.ScrollViewListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPageActivity extends GeneralActivity implements ScrollViewListener {
    static public boolean concern;
    static public boolean blacklist;
    private boolean isMe = false;
    private Button concernButton;
    private Button hateButton;
    private Button uploadImageButton;
    private Button manageButton;
    private String userName;
    private LinearLayout gallery;
    private LinearLayout galleryLeft;
    private LinearLayout galleryRight;
    private MyToast myToast;
    private ProgressBar inChangingRelationship;
    private GetPicture getPicture = null;
    private LinearLayout scrollContent;
    private int pictureCount;
    public ArrayList<GalleryItem> galleryItems = null;
    private BlackConcerenTask mAuthTask = null;
    private GetInfomation getInfomationtask = null;
    private LoadingView loadingView;
    private boolean isManagingPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        /**
         * 由intent的信息来决定是自己的主页还是别人的主页
         */
        Intent intent = getIntent();
        /**
         * userName 是这个主页的所有者
         */
        userName = intent.getExtras().get("user_name").toString();
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(userName);
        if (userName.equals(LoginState.username)) {
            isMe = true;
        }
        setTitle((isMe ? "我" : userName) + "的个人主页");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * 初始化变量
         */
        concernButton = (Button) findViewById(R.id.button6);
        hateButton = (Button) findViewById(R.id.button5);
        manageButton = (Button) findViewById(R.id.button8);
        uploadImageButton = (Button) findViewById(R.id.button10);
        gallery = (LinearLayout) findViewById(R.id.gallery);
        galleryItems = new ArrayList<>();
        inChangingRelationship = (ProgressBar) findViewById(R.id.progressBar5);
        scrollContent = (LinearLayout) findViewById(R.id.scroll_content);
        myToast = new MyToast(this);
        loadingView = new LoadingView(this);
        galleryLeft = (LinearLayout) findViewById(R.id.gallery_left);
        galleryRight = (LinearLayout) findViewById(R.id.gallery_right);
        //////////
        concernButton.setOnClickListener(new OnClickConcernListener());
        hateButton.setOnClickListener(new OnClickHateListener());
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromAlbum(view);
            }
        });
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isManagingPicture = !isManagingPicture;
                for (int i = 0; i < galleryItems.size(); i++) {
                    galleryItems.get(i).setRemovable(isManagingPicture);
                }
                manageButton.setText(isManagingPicture ? getString(R.string.OK) : getString(R.string.manage_picture));
            }
        });
        /**
         * 根据isMe变量来判断是加载我的主页还是别人的主页
         */
        loadView(isMe);
    }

    /**
     * 加载主页
     *
     * @param my
     */
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
            uploadImageButton.setVisibility(View.GONE);
            getInfomationtask = new GetInfomation(userName);
            getInfomationtask.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 恢复该界面时刷新图片。
    }

    static final int REQUEST_CODE_PICK_IMAGE = 2;

    protected void getImageFromAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUEST_CODE_PICK_IMAGE:
                break;
        }
    }

    /**
     * 加载主页内容
     *
     * @param concern
     * @param blacklist
     */
    public void loadContent(boolean concern, boolean blacklist) {
        if (concern) {
            gallery.setVisibility(View.VISIBLE);
            if (!getGallery(userName)) {
                findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.textView3).setVisibility(View.GONE);
            concernButton.setText(getString(R.string.remove_from_concern));
        } else {
            gallery.setVisibility(View.INVISIBLE);
            findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            concernButton.setText(getString(R.string.concern));
        }
        if (blacklist) {
            hateButton.setText(getString(R.string.remove_from_blackList));
        } else {
            hateButton.setText(getString(R.string.add_to_blacklist));
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

    /**
     * 跳转到查看图片详细信息
     *
     * @param view
     */
    public void toViewPictureActivity(View view) {
        Intent intent = new Intent(this, ViewPictureActivity.class);
        //view.setDrawingCacheEnabled(true);
        //Bitmap bitmap = view.getDrawingCache();
        String bigurl = view.getContentDescription().toString();
        intent.putExtra("bigurl", bigurl);
        startActivity(intent);
    }

    /**
     * 添加图片进gallery，每次加一张
     * 给ImageView 添加跳转到查看大图监听器
     *
     * @param bitmap
     */
    public void addGalleryItem(Bitmap bitmap, String date) {
        final GalleryItem galleryItem = new GalleryItem(this, bitmap, date);
        galleryItem.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPictureActivity(view);
            }
        });
        galleryItem.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getApplicationContext()).setMessage("删除图片?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO 删除图片操作
                        galleryItems.remove(galleryItem);
                        //UI操作：
                        refreshGallery();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });
        galleryItems.add(galleryItem);
    }

    /**
     * 第一次加载ta的图片完成之后在UI线程刷新gallery
     */
    public void refreshGallery() {
        galleryLeft.removeAllViews();
        galleryRight.removeAllViews();
        for (int i = 0; i < galleryItems.size(); i++) {
            if (i % 2 == 0) {
                galleryLeft.addView(galleryItems.get(i));
            } else {
                galleryRight.addView(galleryItems.get(i));
            }
        }
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
            if (galleryItems.size() != pictureCount) {
                if (gallery.getChildAt(gallery.getChildCount() - 1) != loadingView) {
                    gallery.addView(loadingView);
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

            mAuthTask = new BlackConcerenTask(userName, 1);
            mAuthTask.execute();
        }
    }

    class OnClickHateListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //我 拉黑/取黑 该用户
            if (blacklist) {
                //TODO 把该用户从黑名单中移除
                blacklist = !blacklist;
                hateButton.setText(getString(R.string.add_to_blacklist));
            } else {
                blacklist = !blacklist;
                hateButton.setText(getString(R.string.remove_from_blackList));
            }
        }
    }

    public class BlackConcerenTask extends AsyncTask<Void, Void, Boolean> {
        private String otherUsername;
        private int type;

        BlackConcerenTask(String otherUsername, int type) {
            this.otherUsername = otherUsername;
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url;
                if (type == 1) {
                    if (concern) {
                        url = "http://192.168.253.1/" + LoginState.username + "/concern/delete/";
                    } else {
                        url = "http://192.168.253.1/" + LoginState.username + "/concern/insert/";
                    }
                } else {
                    if (blacklist) {
                        url = "http://192.168.253.1/" + LoginState.username + "/blacklist/delete/";
                    } else {
                        url = "http://192.168.253.1/" + LoginState.username + "/blacklist/insert/";
                    }
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", otherUsername);
                new JsonPost(map, url);
                Thread.sleep(100);

            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Boolean a = success;
            mAuthTask = null;
            if (success) {
                if (type == 1) {
                    if (concern) {
                        concern = !concern;
                        concernButton.setText(getString(R.string.remove_from_concern));
                    } else {
                        concern = !concern;
                        concernButton.setText(getString(R.string.concern));
                    }
                    loadContent(concern, blacklist);
                } else {
                    if (blacklist) {
                        blacklist = !blacklist;
                        hateButton.setText(getString(R.string.remove_from_blackList));
                    } else {
                        blacklist = !blacklist;
                        hateButton.setText(getString(R.string.add_to_blacklist));
                    }
                    loadContent(concern, blacklist);
                }
            } else {
                myToast.show(getString(R.string.toast_operation_error));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

    }

    public class GetInfomation extends AsyncTask<Void, Void, Boolean> {
        private String otherUsername;

        GetInfomation(String otherUsername) {
            this.otherUsername = otherUsername;
        }

        @Override
        protected void onPreExecute() {
            //TODO:加入转圈效果 TO孙晓宇
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://192.168.253.1/" + LoginState.username + "/relation_page/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", otherUsername);
                HashMap<String, String> returnmap = new JsonPost(map, url,"relation").getReturnmap();
                concern = returnmap.get("concern").equals("true");
                blacklist = returnmap.get("blacklist").equals("true");
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                loadContent(concern, blacklist);
            } else {
                //TODO:关系读取错误 提示 TO:孙晓宇
            }
        }

        @Override
        protected void onCancelled() {
            getInfomationtask = null;
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
            gallery.removeView(loadingView);
            getPicture = null;
            if (success) {
                gallery.postInvalidate();
            } else {
                myToast.show(getString(R.string.toast_refreshing_error));
            }
        }
    }
}