package com.example.team.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.team.myapplication.util.GeneralActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserListActivity extends GeneralActivity {
    private ListView listView;
    private ArrayList<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        Intent intent = getIntent();
        setContentView(R.layout.activity_user_list);

        listView = (ListView)findViewById(R.id.user_name_list);
        userNames = new ArrayList<String>();

        int message = (int)intent.getExtras().get("message");
        switch (message){
            case MainActivity.friend_list:
                setTitle("我关注的人");
                //Toast.makeText(getApplicationContext(), "将会列出关注的人的名单！", Toast.LENGTH_LONG).show();
                break;
            case MainActivity.blacklist:
                setTitle("黑名单");
                //Toast.makeText(getApplicationContext(),"将会列出黑名单！",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }


        showList(message);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showList(int type){

        final List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
        if(type == MainActivity.friend_list){
            //获得关注的人的名单，名单是Arraylist数组,放置到userName里
            //以下测试用
            userNames.add("好友1");
            userNames.add("好友2");
            //以上---
            for(int i = 0;i<userNames.size();i++){
                Map<String,Object> listItem = new HashMap<String, Object>();
                listItem.put("用户名",userNames.get(i));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,R.layout.layout_user_name,new String[] {"用户名"},new int[] {R.id.Names});
            listView.setAdapter(simpleAdapter);

        }
        if(type == MainActivity.blacklist){
            //获得黑名单，名单是Arraylist数组,放置到userName里
            //以下测试用
            userNames.add("黑1");
            userNames.add("黑2");
            //以上---
            for(int i = 0;i<userNames.size();i++){
                Map<String,Object> listItem = new HashMap<String, Object>();
                listItem.put("用户名",userNames.get(i));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,R.layout.layout_user_name,new String[] {"用户名"},new int[] {R.id.Names});
            listView.setAdapter(simpleAdapter);
        }
    }
}
