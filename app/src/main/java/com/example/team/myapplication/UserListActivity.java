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

import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserListActivity extends GeneralActivity {
    private ListView listView;
    private ArrayList<String> userNames;
    private MyToast myToast;
    private int message;
    // private DownloadList downloadList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_user_list);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.user_name_list);
        userNames = new ArrayList<String>();
        myToast = new MyToast(this);
        message = (int) intent.getExtras().get("message");
        switch (message) {
            case MainActivity.concernList:
                setTitle("我关注的人");
                if (actionBar != null)
                    actionBar.setLogo(R.mipmap.ic_user_like);
                break;
            case MainActivity.blacklist:
                setTitle("黑名单");
                if (actionBar != null)
                    actionBar.setLogo(R.mipmap.ic_blacklist);
                break;
            default:
                break;
        }
        showList(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 回到该界面时要重新加载名单
        showList(message);
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

    public void toUserPageActivity(View view, String name) {
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    public void showList(int type) {
        if (type == MainActivity.concernList) {
            //获得关注的人的名单，名单是Arraylist<String>数组,放置到userName里
            //以下测试用
            /*userNames.add("好友1");
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
            for (int i = 0; i < userNames.size(); i++) {
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("用户名", userNames.get(i));
                listItems.add(listItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.layout_user_name, new String[]{"用户名"}, new int[]{R.id.Names});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = ((TextView) view.findViewById(R.id.Names)).getText().toString();
                    toUserPageActivity(view, name);
                }
            });*/
            DownloadList downloadList = new DownloadList("concern", this);
            downloadList.execute();
        }
        if (type == MainActivity.blacklist) {
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
            DownloadList downloadList = new DownloadList("black", this);
            downloadList.execute();
        }
    }

    public class DownloadList extends AsyncTask<Void, Void, Boolean> {

        private String type;
        private Context context;
        private List<Map<String, Object>> listItems;

        public DownloadList(String type, Context context) {
            this.type = type;
            this.context = context;
            listItems = new ArrayList<Map<String, Object>>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url;
                if (type.equals("concern")) {
                    url = "http://192.168.253.1/" + LoginState.username + "/concern/show/";
                } else {
                    url = "http://192.168.253.1/" + LoginState.username + "/blacklist/show/";
                }
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
                myToast.show(getString(R.string.toast_fetching_list_error));
            }
            //downloadList=null;
        }
    }
}