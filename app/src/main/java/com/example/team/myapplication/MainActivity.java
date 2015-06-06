package com.example.team.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private String capturePath = null;
    public View squareView;
    public View meView;
    private View loginView;
    private View userOptions;
    private ViewPager viewPager;
    private List<View> listOfViews;
    private RelativeLayout mainLayout;
    private RelativeLayout uploadPictureLayout;
    private ImageView imageView;
    private Button upload;
    private Button cancel;
    private DB db = null;
    private ImageButton camera;
    private Toast toast = null;
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
        //变量初始化
        squareView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_square, null);
        meView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_me, null);
        //searchView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_search, null);
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
        camera = (ImageButton) findViewById(R.id.imageButton);
        Button myConcern = (Button) meView.findViewById(R.id.my_concern_button);
        myConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserListActivity(view, concernList);
            }
        });
        Button myBlacklist = (Button) meView.findViewById(R.id.my_blacklist_button);
        myBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserListActivity(view, blacklist);
            }
        });
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadView(false);

            }
        });
        listOfViews.add(squareView);
        listOfViews.add(meView);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    camera.setVisibility(View.GONE);
                } else {
                    camera.setVisibility(View.VISIBLE);
                }
                mTabHost.setCurrentTab(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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


        changeView(LoginState.getLogined());
        //Toast.makeText(getBaseContext(),"isLogin?"+LoginState.logined,Toast.LENGTH_LONG).show();
        /**
         * 如果imagedownload()的线程一直跑会占用很多cpu资源，请解决
         */
        imagedownload();
        //imageview

    }

    public void imagedownload() {
        String picURL1 = "http://192.168.253.1/square_page/1/";
        String picURL2 = "http://192.168.253.1/square_page/2/";
        DownloadPictureProgress downloadPictureProgress1 = new DownloadPictureProgress(picURL1, db, squareView);
        DownloadPictureProgress downloadPictureProgress2 = new DownloadPictureProgress(picURL2, db, null);
        downloadPictureProgress1.execute();
        downloadPictureProgress2.execute();
        //  JsonGet jsonGet1 = new JsonGet(picURL1, db, squareView);
        // JsonGet jsonGet2 = new JsonGet(picURL2, db);
        //String picURL1 = "http://192.168.253.1/square_page/1/";
        //ImageGet imageGet=new ImageGet((ImageView)squareView.findViewById(R.id.imageView1),picURL1,db);
    }

    public void showUploadView(boolean show) {
        uploadPictureLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mainLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

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
     * @param isLogined
     */
    public void changeView(boolean isLogined) {
        loginView.setVisibility(isLogined ? View.GONE : View.VISIBLE);
        userOptions.setVisibility(isLogined ? View.VISIBLE : View.GONE);
    }

    /**
     * 转到注册页面
     * @param view
     */
    public void toLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 转到动态页面
     * @param view
     */
    public void toRecentActivity(View view) {
        Intent intent = new Intent(this, RecentActivity.class);
        startActivity(intent);
    }

    /**
     * 转到我的个人主页
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
     * 转到搜索界面
     * @param view
     */
    public void toSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        /*CharSequence text = ((Button)view).getText();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();*/
    }

    public void toPictureActivity(View view) {
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    public void toChangePasswordActivity(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
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
     * 把从路径读到的图片压缩 (可能是这里出了问题，读得了图片，但是bitmap不能显示)
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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 跳转到查看图片详细信息
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
                String bigurl = imageviewJson.getString("imagebigurl");
                String id = imageviewJson.getString("imageid");
                intent.putExtra("bigurl", bigurl);
                intent.putExtra("imageid", id);
                startActivity(intent);
            } catch (Exception e) {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "图片出错", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "图片出错", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        } else {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "图片出错", Toast.LENGTH_LONG);
                toast.show();
            } else {
                toast.cancel();
                toast = Toast.makeText(getApplicationContext(), "图片出错", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /**
     * 转到我的黑名单或我关注的人
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
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "下载图片出错", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "下载图片出错", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }
}
