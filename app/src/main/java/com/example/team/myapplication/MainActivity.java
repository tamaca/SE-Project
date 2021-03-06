package com.example.team.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.ImageGet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    private TabHost mTabHost;
    private ListView listView;
    public final static int friend_list = 1;
    public final static int blacklist = 2;
    public View squareView;
    public View meView;
    private View loginView;
    private View userOptions;
    private ViewPager viewPager;
    private List <View>listOfViews;

    private DB db=new DB(this);
    public static String getCurrentTag() {
        return currentTag;
    }

    public static void setCurrentTag(String currentTag) {
        MainActivity.currentTag = currentTag;
    }

    private static String currentTag;

    //private ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //变量初始化
        squareView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_square, null);
        meView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_me, null);

        userOptions = meView.findViewById(R.id.user_options);

        mTabHost = (TabHost)findViewById(R.id.tabHost2);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        listOfViews = new ArrayList<>();
        listView = (ListView)meView.findViewById(R.id.listView);
        loginView = meView.findViewById(R.id.login_button);
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

        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("广场").setContent(R.id.linearLayout));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("我").setContent(R.id.linearLayout));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                viewPager.setCurrentItem(mTabHost.getCurrentTab());

            }
        });


        ArrayList<String> items = new ArrayList<String>();
        //0
        items.add("搜索");
        //1
        items.add("关注的人");
        //2
        items.add("黑名单");
        //3
        items.add("修改密码");
        //4
        items.add("注销");
        //5


        final List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("选项", items.get(i));
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.layout_simple_item, new String[]{"选项"}, new int[]{R.id.Option});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        toSearchActivity(listView);

                        break;
                    case 1:
                        toUserListActivity(listView, friend_list);

                        break;
                    case 2:
                        toUserListActivity(listView, blacklist);

                        break;
                    case 3:

                        break;
                    case 4:
                        //
                        logout();
                        break;


                }
            }
        });

        changeView(LoginState.getLogined());
        //Toast.makeText(getBaseContext(),"isLogin?"+LoginState.logined,Toast.LENGTH_LONG).show();
        imagedownload();
        //imageview
    }

    public void imagedownload()
    {
        String picURL1 = "http://7.share.photo.xuite.net/angel890208/1719fd6/4701295/179671143_x.jpg";
        ImageGet imageGet=new ImageGet((ImageView)squareView.findViewById(R.id.imageView6),picURL1,db);

    }


    @Override
    public void onResume(){
        super.onResume();
        changeView(LoginState.getLogined());
        //mTabHost.setCurrentTabByTag(getCurrentTag());
    }
    public void logout(){
        LoginState.setLogined(false,"guest");
        changeView(LoginState.logined);
        
    }
    public void changeView(boolean isLogined){
        loginView.setVisibility(isLogined ? View.GONE : View.VISIBLE);
        userOptions.setVisibility(isLogined ? View.VISIBLE : View.GONE);
    }
    public void toLoginActivity(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
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



    public void toSearchActivity(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        /*CharSequence text = ((Button)view).getText();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();*/
    }

    public void toPictureActivity(View view){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            onStop();
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        /*CharSequence text = ((Button)view).getText();
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();*/
    }

    public boolean isIntentAvailable(Context context,String action){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(action);
        List<ResolveInfo>list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size()>0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            Bitmap image = (Bitmap)data.getExtras().get("data");

            Intent toEditPictureIntent = new Intent(MainActivity.this,EditPictureActivity.class);
            toEditPictureIntent.putExtra("picture",image);

            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:

                    Toast.makeText(getApplicationContext(),"Picture is Taken", Toast.LENGTH_LONG).show();
                    //startActivity(toEditPictureIntent);

                    break;

                    //ImageView imgView = (ImageView)findViewById(R.id.imageView);
                    //imgView.setImageBitmap(image);


            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Picture is not Taken",Toast.LENGTH_SHORT).show();
        }
    }

    public void toEditPictureActivity(View view){
        Intent intent = new Intent(this,EditPictureActivity.class);
        startActivity(intent);
    }
    public void toViewPictureActivity(View view){
        Intent intent = new Intent(this,ViewPictureActivity.class);
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();

        intent.putExtra("pic",bitmap);
        startActivity(intent);
    }
    public void toUserListActivity(View view, int x){

        Intent intent = new Intent(this,UserListActivity.class);
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

}
