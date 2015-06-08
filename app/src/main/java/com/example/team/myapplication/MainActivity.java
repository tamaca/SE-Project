package com.example.team.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;
import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.Network.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
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
    private RelativeLayout uploadPictureLayout;
    private ImageView imageView;
    private ImageButton search;
    private Button upload;
    private Button cancel;
    private DB db = null;
    private ImageButton camera;
    private MyToast myToast;
    private ProgressBar uploadProgressBar;

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
        uploadPictureLayout = (RelativeLayout) findViewById(R.id.upload_picture_layout);
        imageView = (ImageView) findViewById(R.id.imageView);
        upload = (Button) findViewById(R.id.upload_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        camera = (ImageButton) squareView.findViewById(R.id.imageButton);
        search = (ImageButton) squareView.findViewById(R.id.imageButton3);
        myToast = new MyToast(this);
        /**
         * 设置搜索按钮背景为透明
         */
        search.setBackgroundColor(Color.argb(0, 0, 0, 0));
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
         * 上传图片按钮添加监听器
         */
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传图片开始。

                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();
                UploadPictureProgress uploadPictureProgress = new UploadPictureProgress(bitmap);
                uploadPictureProgress.execute((Void) null);
            }
        });
        /**
         * 取消上传按钮添加监听器
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadView(false);

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
        //Toast.makeText(getBaseContext(),"isLogin?"+LoginState.logined,Toast.LENGTH_LONG).show();
        /**
         * 如果imagedownload()的线程一直跑会占用很多cpu资源，请解决
         */
        imagedownload();
        //imageview

    }

    public void imagedownload() {
        if (NetworkState.isNetworkConnected(this)) {
            String picURL1 = "http://192.168.253.1/square_page/1/";
            String picURL2 = "http://192.168.253.1/square_page/2/";
            DownloadPictureProgress downloadPictureProgress1 = new DownloadPictureProgress(picURL1, db, squareView);
            DownloadPictureProgress downloadPictureProgress2 = new DownloadPictureProgress(picURL2, db, null);
            downloadPictureProgress1.execute();
            downloadPictureProgress2.execute();
        } else {
            Cursor mCursor = db.lobbyimageselectpage(LoginState.page);
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                String id = mCursor.getString((mCursor.getColumnIndex("m_lobbyimage_imageid")));
                String rank = mCursor.getString((mCursor.getColumnIndex("m_lobbyimage_rank")));
                String smallfilepath = Localstorage.getImageFilePath(id, "small");
                Bitmap bitmap = Localstorage.getBitmapFromSDCard(smallfilepath);
                Resources res = getResources();
                int imageviewid = res.getIdentifier("imageView" + rank, "id", getPackageName());
                ImageView _imageView = (ImageView) squareView.findViewById(imageviewid);
                _imageView.setImageBitmap(bitmap);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "offline");
                    jsonObject.put("imageid", id);
                } catch (JSONException e) {
                    //TODO:数据传递错误（一般很少发生） TO孙晓宇
                    e.printStackTrace();
                }
                _imageView.setContentDescription(jsonObject.toString());
            }
        }
        //  JsonGet jsonGet1 = new JsonGet(picURL1, db, squareView);
        // JsonGet jsonGet2 = new JsonGet(picURL2, db);
        //String picURL1 = "http://192.168.253.1/square_page/1/";
        //ImageGet imageGet=new ImageGet((ImageView)squareView.findViewById(R.id.imageView1),picURL1,db);
    }

    /**
     * 切换到上传图片页面
     *
     * @param show
     */
    public void showUploadView(boolean show) {
        uploadPictureLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mainLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * 重写函数，当返回到MainActivity的时候显示相应的界面
     */
    @Override
    public void onResume() {
        super.onResume();
        changeView(LoginState.getLogined());
    }

    public void logout(View view) {
        LoginState.setLogined(false, "guest");
        changeView(LoginState.logined);

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
     * 转到查看大图页面
     *
     * @param view
     */
    public void toPictureActivity(View view) {
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
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
                        String id = imageviewJson.getString("imageid");
                        //获取缩略图时未联网 现在要联网获取大图
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
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
                    setPic();
                    showUploadView(true);
                    Toast.makeText(getApplicationContext(), "Picture is Taken", Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Picture is not Taken", Toast.LENGTH_SHORT).show();
        }
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
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /**
     * 把从路径读到的图片压缩 (可能是这里出了问题，读得了图片，但是不能获得真实图片的宽高)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getMaxHeight();
        int targetH = imageView.getMaxWidth();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }


    /**
     * 上传图片的线程
     */
    class UploadPictureProgress extends AsyncTask<Void, Void, Boolean> {

        Bitmap bitmap;

        public UploadPictureProgress(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //TODO 上传图片
                Thread.sleep(1000);
            } catch (InterruptedException e) {


                return false;
            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

                showUploadView(false);
            } else {

            }
        }
    }

    public class DownloadPictureProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private View view;
        private DB db;

        DownloadPictureProgress(String url, DB db, View view) {
            this.url = url;
            this.view = view;
            this.db = db;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                new JsonGet(url, db, view, "lobby");
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                squareView.postInvalidate();
            } else {
                myToast.show(getString(R.string.toast_downloading_picture_error));
            }
        }
    }
}
