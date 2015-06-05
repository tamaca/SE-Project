package com.example.team.myapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Network.JsonGet;
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
        Intent intent = getIntent();
        setContentView(R.layout.activity_user_list);
        ActionBar actionBar = getActionBar();

        listView = (ListView)findViewById(R.id.user_name_list);
        userNames = new ArrayList<String>();

        int message = (int)intent.getExtras().get("message");
        switch (message){
            case MainActivity.concernList:
                setTitle("我关注的人");
                if(actionBar!=null)
                    actionBar.setLogo(R.mipmap.ic_user_like);
                //Toast.makeText(getApplicationContext(), "将会列出关注的人的名单！", Toast.LENGTH_LONG).show();
                break;
            case MainActivity.blacklist:
                setTitle("黑名单");
                if(actionBar!=null)
                    actionBar.setLogo(R.mipmap.ic_blacklist);
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

    public void toUserPageActivity(View view,String name){
        Intent intent = new Intent(this,UserPageActivity.class);
        intent.putExtra("user_name",name);
        startActivity(intent);
    }

    public void showList(int type){

        final List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
        if(type == MainActivity.concernList){
            //获得关注的人的名单，名单是Arraylist<String>数组,放置到userName里
            //以下测试用
            userNames.add("好友1");
            userNames.add("好友2");
            userNames.add("好友3");
            userNames.add("好友4");
            userNames.add("好友5");
            userNames.add("好友6");
            userNames.add("好友7");
            userNames.add("好友8");
            userNames.add("好友9");
            userNames.add("好友10");
            userNames.add("好友11");
            userNames.add("好友12");
            //以上---
            for(int i = 0;i<userNames.size();i++){
                Map<String,Object> listItem = new HashMap<String, Object>();
                listItem.put("用户名",userNames.get(i));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,R.layout.layout_user_name,new String[] {"用户名"},new int[] {R.id.Names});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = ((TextView)view.findViewById(R.id.Names)).getText().toString();
                    toUserPageActivity(view,name);
                }
            });

        }
        if(type == MainActivity.blacklist){
            /*//获得黑名单，名单是Arraylist<String>数组,放置到userName里
            //以下测试用
            userNames.add("黑1");
            userNames.add("黑2");
            userNames.add("黑3");
            userNames.add("黑4");
            userNames.add("黑5");
            userNames.add("黑6");
            userNames.add("黑7");
            userNames.add("黑8");
            userNames.add("黑9");
            userNames.add("黑10");
            userNames.add("黑11");
            userNames.add("黑12");
            //以上---
            for(int i = 0;i<userNames.size();i++){
                Map<String,Object> listItem = new HashMap<String, Object>();
                listItem.put("用户名",userNames.get(i));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,R.layout.layout_user_name,new String[] {"用户名"},new int[] {R.id.Names});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = ((TextView) view.findViewById(R.id.Names)).getText().toString();
                    toUserPageActivity(view, name);
                }
            });
            */
            String url = "";
            DownloadFriendList downloadFriendList = new DownloadFriendList(url, this);
            downloadFriendList.execute();
        }
    }
    public class DownloadFriendList extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private Toast toast = null;
        private Context context;
        private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        DownloadFriendList(String url, Context context) {
            this.url = url;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                userNames = new JsonGet(url).getUserNames();
                for (int i = 0; i < userNames.size(); i++) {
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    listItem.put("用户名", userNames.get(i));
                    listItems.add(listItem);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems, R.layout.layout_user_name, new String[]{"用户名"}, new int[]{R.id.Names});
                listView.setAdapter(simpleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name = ((TextView) view.findViewById(R.id.Names)).getText().toString();
                        toUserPageActivity(view, name);
                    }
                });
            } else {
                {
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), "获取列表出错", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        toast.cancel();
                        toast = Toast.makeText(getApplicationContext(), "获取列表出错", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        }
    }
}
