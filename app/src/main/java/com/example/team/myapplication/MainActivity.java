package com.example.team.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.ImagePost;
import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.Network.NetworkState;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.LoadingView;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.ScrollViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements ScrollViewListener {
    private TabHost mTabHost;
    public final static int concernList = 1;
    public final static int blacklist = 2;
    public View squareView;
    public View meView;
    private View loginView;
    private View userOptions;
    private ViewPager viewPager;
    private List<View> listOfViews;
    private RelativeLayout mainLayout;
    private ImageButton search;
    private DB db = null;
    private ImageButton camera;
    private MyToast myToast;
    private ProgressBar uploadProgressBar;
    private MyScrollView myScrollView;
    private LoadingView loadingView;
    private LinearLayout scrollContent;
    private LinearLayout scrollContentLeft;
    private LinearLayout scrollContentRight;
    private Boolean end = false;
    private ViewGroup.MarginLayoutParams cameraLayoutParams;
    public ArrayList<GalleryItem> galleryItems = null;
    private HideCameraTask hideCameraTask = null;
    private ShowCameraTask showCameraTask = null;
    public static int SCROLL_SPEED = -10;
    private int hideHeaderHeight;
    private int originalBottomMargin;

    public static String getCurrentTag() {
        return currentTag;
    }

    public static void setCurrentTag(String currentTag) {
        MainActivity.currentTag = currentTag;
    }

    private static String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Localstroage
        Localstorage.setpath(this);
        db = new DB(this);
        //
        setContentView(R.layout.activity_main);
        /**
         * 初始化变量
         */
        squareView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_square, null);
        meView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_me, null);
        userOptions = meView.findViewById(R.id.user_options);
        mTabHost = (TabHost) findViewById(R.id.tabHost2);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        listOfViews = new ArrayList<>();
        loginView = meView.findViewById(R.id.login_button);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        camera = (ImageButton) squareView.findViewById(R.id.imageButton);
        myScrollView = (MyScrollView) squareView.findViewById(R.id.scrollView5);
        scrollContent = (LinearLayout) squareView.findViewById(R.id.linearLayout8);
        scrollContentLeft = (LinearLayout) squareView.findViewById(R.id.square_left);
        scrollContentRight = (LinearLayout) squareView.findViewById(R.id.square_right);
        galleryItems = new ArrayList<>();
        myToast = new MyToast(this);
        search = new ImageButton(this);
        loadingView = new LoadingView(this);
        search.setImageResource(android.R.drawable.ic_menu_search);
        cameraLayoutParams = (ViewGroup.MarginLayoutParams) camera.getLayoutParams();
        originalBottomMargin = cameraLayoutParams.bottomMargin;
        /**
         * 设置搜索按钮背景为透明
         */
        search.setBackgroundColor(Color.argb(0, 0, 0, 0));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSearchActivity(view);
            }
        });

        myScrollView.setScrollViewListener(this);
        /**
         * 我关注的人的按钮添加监听器
         */
        Button myConcern = (Button) meView.findViewById(R.id.my_concern_button);
        myConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserListActivity(view, concernList);
            }
        });
        /**
         * 黑名单按钮添加监听器
         */
        Button myBlacklist = (Button) meView.findViewById(R.id.my_blacklist_button);
        myBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserListActivity(view, blacklist);
            }
        });
        /**
         * 初始化ViewPager
         */
        listOfViews.add(squareView);
        listOfViews.add(meView);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /**
         * 初始化TabHost
         */
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("广场").setContent(R.id.linearLayout));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("我").setContent(R.id.linearLayout));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTabHost.getTabWidget().addView(search, layoutParams);
        for (int i = 0; i < 2; i++) {
            TextView textView = (TextView)
                    mTabHost.getTabWidget().getChildTabViewAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
            textView.setTextSize(20f);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                viewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mTabHost.setCurrentTab(0);
        /**
         * 展示出相应的界面
         */
        changeView(LoginState.getLogined());
        if (!LoginState.fresh) {
            imagedownload();
        }
    }

    public void imagedownload() {
        if (NetworkState.isNetworkConnected(this)) {
            int page = LoginState.getPage() + 1;
            String picURL1 = "http://192.168.253.1/square_page/" + (page) + "/";
            page++;
            String picURL2 = "http://192.168.253.1/square_page/" + (page) + "/";
            GalleryItem galleryItems[] = new GalleryItem[8];
            for (int i = 0; i < 8; i++) {
                galleryItems[i] = new GalleryItem(this);
            }
            DownloadPictureProgress downloadPictureProgress1 = new DownloadPictureProgress(picURL1, db, galleryItems);
            DownloadPictureProgress downloadPictureProgress2 = new DownloadPictureProgress(picURL2, db, null);
            downloadPictureProgress1.execute();
            downloadPictureProgress2.execute();
        } else {
            Cursor mCursor = db.lobbyimageselectpage(LoginState.getPage());
            if (!(mCursor.getCount() == 0)) {
                LoginState.setPage(LoginState.getPage() + 1);
            } else {
                end = true;
                scrollContent.removeView(loadingView);
            }
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                String id = mCursor.getString((mCursor.getColumnIndex("m_lobbyimage_imageid")));
                String smallfilepath = Localstorage.getImageFilePath(id, "small");
                Bitmap bitmap = Localstorage.getBitmapFromSDCard(smallfilepath);
                GalleryItem galleryItem = new GalleryItem(this);
                galleryItem.imageView.setImageBitmap(bitmap);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "offline");
                    jsonObject.put("imageid", id);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "数据传输错误", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                galleryItem.setContentDescription(jsonObject.toString());
                galleryItems.add(galleryItem);
                if (scrollContent.getChildAt(scrollContent.getChildCount() - 1) == loadingView) {
                    scrollContent.removeView(loadingView);
                }
            }
            refreshSquare();
        }
    }

    /**
     * 重写函数，当返回到MainActivity的时候显示相应的界面
     */
    @Override
    public void onResume() {
        super.onResume();
        changeView(LoginState.getLogined());
        if (LoginState.fresh) {
            imagedownload();
        }
    }

    public void logout(View view) {
        LoginState.setLogined(false, "guest");
        changeView(LoginState.logined);
        db.lastuserdelete();
    }

    /**
     * 根据是否登录成功改变主页
     *
     * @param isLogined
     */
    public void changeView(boolean isLogined) {
        loginView.setVisibility(isLogined ? View.GONE : View.VISIBLE);
        userOptions.setVisibility(isLogined ? View.VISIBLE : View.GONE);
    }

    /**
     * 转到注册页面
     *
     * @param view
     */
    public void toLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 转到动态页面
     *
     * @param view
     */
    public void toRecentActivity(View view) {
        Intent intent = new Intent(this, RecentActivity.class);
        startActivity(intent);
    }

    /**
     * 转到我的个人主页
     *
     * @param view
     */
    public void toUserPageActivity(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("user_name", LoginState.username);
        startActivity(intent);
    }

    /**
     * 转到搜索页面
     *
     * @param view
     */
    public void toSearchActivity(View view) {
        if (LoginState.getLogined()) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else {
            myToast.show(getString(R.string.toast_before_login));
        }
    }

    /**
     * 转到更改密码页面
     *
     * @param view
     */
    public void toChangePasswordActivity(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到查看图片详细信息
     *
     * @param view
     */
    public void toViewPictureActivity(View view) {
        if (LoginState.logined) {
            if (((ImageView) view).getDrawable() != null) {
                Intent intent = new Intent(this, ViewPictureActivity.class);
                //view.setDrawingCacheEnabled(true);
                //Bitmap bitmap = view.getDrawingCache();
                String imageviewJsonString = view.getContentDescription().toString();
                try {
                    JSONObject imageviewJson = new JSONObject(imageviewJsonString);
                    //获取大图时联网
                    if (NetworkState.isNetworkConnected(this)) {
                        String type = imageviewJson.getString("type");
                        if (type.equals("online")) {
                            //获取缩略图时联网 直接有大图地址
                            String bigurl = imageviewJson.getString("imagebigurl");
                            String id = imageviewJson.getString("imageid");
                            intent.putExtra("type", "online");
                            intent.putExtra("bigurl", bigurl);
                            intent.putExtra("imageid", id);
                            startActivity(intent);
                        } else {
                            //获取缩略图时未联网 无大图地址 获取大图时联网
                            String id = imageviewJson.getString("imageid");
                            intent.putExtra("type", "halfline");
                            intent.putExtra("imageid", id);
                            startActivity(intent);
                        }
                    }
                    //获取大图时未联网
                    //直接获取id找数据库
                    else {
                        String id = imageviewJson.getString("imageid");
                        String filePath = Localstorage.getImageFilePath(id, "big");
                        intent.putExtra("type", "offline");
                        intent.putExtra("filepath", filePath);
                        intent.putExtra("imageid", id);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    myToast.show(getString(R.string.toast_picture_error));
                }
            } else {
                myToast.show(getString(R.string.toast_picture_error));
            }
        } else {
            myToast.show(getString(R.string.toast_before_login));
        }
    }

    /**
     * 转到我的黑名单或我关注的人
     *
     * @param view
     * @param x
     */
    public void toUserListActivity(View view, int x) {

        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("message", x);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y < 0 || oldY < 0) {
            y = 0;
            oldY = 0;
        }
        if (y > scrollContent.getMeasuredHeight() - scrollView.getMeasuredHeight() || oldY > scrollContent.getMeasuredHeight() - scrollView.getMeasuredHeight()) {
            y = scrollContent.getMeasuredHeight() - scrollView.getMeasuredHeight();
            oldY = scrollContent.getMeasuredHeight() - scrollView.getMeasuredHeight();

        }
        hideHeaderHeight = -camera.getHeight();
        Log.d("Y", String.valueOf(y));
        Log.d("oldY", String.valueOf(oldY));
        if (y - oldY >= 10) {
            if (hideCameraTask == null && showCameraTask == null) {
                hideCameraTask = new HideCameraTask();
                hideCameraTask.execute();
            }
        }
        if (oldY - y >= 10) {
            if (showCameraTask == null && hideCameraTask == null) {
                showCameraTask = new ShowCameraTask();
                showCameraTask.execute();
            }
            if (end) {
                end = false;
            }
        }

        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (!end) {
                if (scrollContent.getChildAt(scrollContent.getChildCount() - 1) != loadingView) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;

                    scrollContent.addView(loadingView, params);
                    imagedownload();
                }
            }
        }

    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return listOfViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(listOfViews.get(position));
            return listOfViews.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {

            ((ViewPager) container).removeView(listOfViews.get(position));
        }
    }

    /**
     * 以下是获取拍摄的照片
     */
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;

    public void dispatchTakePictureIntent(View view) {
        if (LoginState.logined) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    LoginState.photo=true;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            myToast.show(getString(R.string.toast_before_login));
        }
    }

    private String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

    /**
     * 从相机中获取拍摄的照片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    /*setPic();*/
                    Toast.makeText(getApplicationContext(), "Picture is Taken", Toast.LENGTH_LONG).show();
                    Map<String, String> params = new HashMap<String, String>();
                    ;
                    params.clear();
                    File file = new File(mCurrentPhotoPath);
                    StringBuffer sbFileTypes = new StringBuffer();
                    String fileName = file.getName();
                    sbFileTypes.append(getFileType(fileName));
                    params.put("fileTypes", sbFileTypes.toString());
                    String actionUrl = "http://192.168.253.1/upload/" + LoginState.username + "/";
                    ImagePost imagePost = new ImagePost(this, actionUrl, params, file, 50,null);
                    imagePost.execute();
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Picture is not Taken", Toast.LENGTH_SHORT).show();
        }
        LoginState.photo=false;
    }


    /**
     * 把拍摄的图片写入储存卡 没有问题
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void refreshSquare() {
        scrollContentLeft.removeAllViews();
        scrollContentRight.removeAllViews();
        for (int i = 0; i < galleryItems.size(); i++) {
            if (i % 2 == 0) {
                scrollContentLeft.addView(galleryItems.get(i));
            } else {
                scrollContentRight.addView(galleryItems.get(i));
            }
        }
    }


    public class DownloadPictureProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private DB db;
        private GalleryItem galleryItem[];

        DownloadPictureProgress(String url, DB db, GalleryItem[] galleryItems) {
            this.url = url;
            this.galleryItem = galleryItems;
            this.db = db;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                new JsonGet(url, db, galleryItem, "lobby").getReturnmap();
            } catch (MyException.zeroException e) {
                end = true;
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (scrollContent.getChildAt(scrollContent.getChildCount() - 1) == loadingView)
                scrollContent.removeView(loadingView);
            if (success) {
                if (galleryItem != null) {
                    LoginState.setPage(LoginState.getPage() + 1);
                    for (GalleryItem _galleryitem : galleryItem) {
                        if (_galleryitem.imageView.getContentDescription() != null) {
                            galleryItems.add(_galleryitem);
                            _galleryitem.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    toViewPictureActivity(view);
                                }
                            });
                        }
                    }
                    refreshSquare();
                    squareView.postInvalidate();
                }
                if (end) {
                    myToast.show("没有更多图片了");
                }
            } else {
                myToast.show(getString(R.string.toast_downloading_picture_error));

            }
        }
    }

    class HideCameraTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int bottomMargin = cameraLayoutParams.bottomMargin;
            while (true) {
                bottomMargin = bottomMargin + SCROLL_SPEED;
                if (bottomMargin <= hideHeaderHeight) {
                    bottomMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(bottomMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return bottomMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... bottomMargin) {
            cameraLayoutParams.bottomMargin = bottomMargin[0];
            camera.setLayoutParams(cameraLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer bottomMargin) {
            cameraLayoutParams.bottomMargin = bottomMargin;
            camera.setLayoutParams(cameraLayoutParams);
            hideCameraTask = null;
        }
    }

    class ShowCameraTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int bottomMargin = cameraLayoutParams.bottomMargin;
            while (true) {
                bottomMargin = bottomMargin - SCROLL_SPEED;
                if (bottomMargin >= originalBottomMargin) {
                    bottomMargin = originalBottomMargin;
                    break;
                }
                publishProgress(bottomMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return bottomMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... bottomMargin) {
            cameraLayoutParams.bottomMargin = bottomMargin[0];
            camera.setLayoutParams(cameraLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer bottomMargin) {
            cameraLayoutParams.bottomMargin = bottomMargin;
            camera.setLayoutParams(cameraLayoutParams);
            showCameraTask = null;
        }
    }
}
