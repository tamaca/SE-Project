package com.example.team.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    private TabHost mTabHost;
    private ListView listView;
    private ArrayList<String> items;
    public final static int friend_list = 1;
    public final static int blacklist = 2;
    public static boolean isSigned = false;
    private View main_view;
    private View login_view;
    private View userOptions;


    //private ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        setContentView(R.layout.activity_main);
        login_view = findViewById(R.id.login_button);
        listView = (ListView) findViewById(R.id.listView);
        userOptions = findViewById(R.id.user_options);
        main_view = findViewById(R.id.linearLayout3);
        mTabHost = (TabHost)findViewById(R.id.tabHost2);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator("广场").setContent(R.id.linearLayout));
        //mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("我").setContent(R.id.linearLayout2));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("我").setContent(R.id.linearLayout3));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }

        });

        items = new ArrayList<String>();
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
        items.add("");
        items.add("And Whatever You Want to Add");

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

                        break;
                    case 5:

                        break;


                }
            }
        });
        changeView(isSigned);
        UIThread uiThread = new UIThread();
        uiThread.run();


    }
    public void changeView(boolean isLogined){
        login_view.setVisibility(isLogined ? View.GONE : View.VISIBLE);
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
            Intent toEditPictureIntent = new Intent(getApplicationContext(),EditPictureActivity.class);
            toEditPictureIntent.putExtra("picture",image);
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    Toast.makeText(getApplicationContext(),"whatHappens", Toast.LENGTH_LONG).show();
                    break;
                    //startActivity(toEditPictureIntent);
                    //ImageView imgView = (ImageView)findViewById(R.id.imageView);
                    //imgView.setImageBitmap(image);


            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            Intent it =new Intent(getApplicationContext(), MainActivity.class);

            startActivity(it);
            finish();
            return;
        }
        else
            return;
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
    class UIThread implements Runnable {
        public void run() {
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                    main_view.postInvalidate();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 使用postInvalidate可以直接在线程中更新界面

            }

        }
    }


}
