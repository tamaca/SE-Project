package com.example.team.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.CheckValid;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends GeneralActivity {
    private Button searchUserButton;
    private Button searchTagButton;
    private ListView userNameListView;
    private MyScrollView pictureScrollView;
    private LinearLayout scrollViewContent;
    public ArrayList<String> resultUsers;
    public ArrayList<GalleryItem> resultPictures;
    private SearchUserTask searchUserTask;
    private ProgressBar onSearchProgressBar;
    private EditText textView;
    private MyToast myToast;
    private TextView noResultTextView;
    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("搜索");
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 初始化变量
         */
        searchUserButton = (Button) findViewById(R.id.search_user_button);
        searchTagButton = (Button) findViewById(R.id.search_tag_button);
        userNameListView = (ListView) findViewById(R.id.user_name_listView);
        pictureScrollView = (MyScrollView) findViewById(R.id.picture_scrollView);
        onSearchProgressBar = (ProgressBar) findViewById(R.id.progressBar7);
        textView = (EditText) findViewById(R.id.editText);
        noResultTextView = (TextView) findViewById(R.id.textView8);
        scrollViewContent = (LinearLayout) findViewById(R.id.scroll_content_in_search_activity);
        resultUsers = new ArrayList<>();
        resultPictures = new ArrayList<>();
        searchUserTask = null;
        myToast = new MyToast(getApplicationContext());

        /**
         * 设置返回结果界面初始化
         */
        userNameListView.setVisibility(View.GONE);
        pictureScrollView.setVisibility(View.GONE);
        /**
         * 添加 搜索用户 监听器
         */
        searchUserButton.setOnClickListener(new MyOnClickListener());
        /**
         * 添加 搜索具有该标签的图片 监听器
         */
        searchTagButton.setOnClickListener(new MyOnClickListener());

    }

    /**
     * 添加图片进搜索结果里，一次添加一张。
     */
    /*
    public void addGalleryItem(Bitmap bitmap) {
        GalleryItem galleryItem = new GalleryItem(this, bitmap);
        galleryItem.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPictureActivity(view);
            }
        });
        resultPictures.add(galleryItem);
    }*/

    /**
     * 更新搜素 用户名 的结果
     */
    public void refreshListView() {
        if (resultUsers.size() == 0) {
            noResultTextView.setText(getString(R.string.no_search_result_users));
            noResultTextView.setVisibility(View.VISIBLE);
        }
        userNameListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, resultUsers));
    }

    /**
     * 更新搜素 图片 的结果
     */
    public void refreshScrollView() {
        if (resultPictures.size() == 0) {
            noResultTextView.setText(getString(R.string.no_search_result_tags));
            noResultTextView.setVisibility(View.VISIBLE);
        }
        scrollViewContent.removeAllViews();
        for (int i = 0; i < resultPictures.size(); i++) {
            scrollViewContent.addView(resultPictures.get(i));
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    class SearchUserTask extends AsyncTask<Void, Void, Boolean> {
        /**
         * 添加变量
         */
        String searchContent;
        HashMap<String,String>returnmap;
        public SearchUserTask(String _searchContent) {
            searchContent = _searchContent;
        }
        @Override
        protected void onPreExecute()
        {
            onSearchProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                //TODO 根据 searchType 确定要搜索什么
                /**
                 * 如果搜索人名，直接把搜索结果放在 resultUsers 里
                 * 如果搜索图片，请调用 addResultPicture(Bitmap )
                 *
                 */
                String url = "http://192.168.253.1/search/user/page/"+page+"/";
                HashMap<String,String>map=new HashMap<String,String>();
                map.put("name",searchContent);
                returnmap=new JsonPost(map,url,"searchuser").getReturnmap();
                Thread.sleep(1000);
            }catch (MyException.zeroException e)
            {
                //TODO:搜索结果为空
            }
            catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            onSearchProgressBar.setVisibility(View.GONE);
            searchUserTask = null;
            if (success) {
                int count = Integer.valueOf(returnmap.get("count"));
                for (int i = 0; i < count; i++) {
                    resultUsers.add(returnmap.get("name"+i));
                }
                /*
                if (searchUserTask == search_users) {
                    refreshListView();
                } else {
                    refreshScrollView();
                }*/
            } else {
                //TODO:搜索错误
            }
        }
    }
    class SearchTagTask extends AsyncTask<Void, Void, Boolean> {
        /**
         * 添加变量
         */
        String searchContent;
        HashMap<String,String>returnmap;
        private GalleryItem galleryItem[];
        public SearchTagTask(String _searchContent,GalleryItem[] galleryItem) {
            searchContent = _searchContent;
            this.galleryItem = galleryItem;
        }
        @Override
        protected void onPreExecute()
        {
            onSearchProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                //TODO 根据 searchType 确定要搜索什么
                /**
                 * 如果搜索人名，直接把搜索结果放在 resultUsers 里
                 * 如果搜索图片，请调用 addResultPicture(Bitmap )
                 *
                 */
                String url = "http://192.168.253.1/search/tag/page/"+page+"/";
                HashMap<String,String>map=new HashMap<String,String>();
                map.put("tag",searchContent);
                returnmap=new JsonPost(map,url,"searchtag").getReturnmap();
                Thread.sleep(1000);
            } catch (MyException.zeroException e)
            {
                //TODO:搜索结果为空
            }catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            onSearchProgressBar.setVisibility(View.GONE);
            searchUserTask = null;
            if (success) {
                int count = Integer.valueOf(returnmap.get("count"));
                for (int i = 0; i < count; i++) {
                    resultUsers.add(returnmap.get("name"+i));
                }
                /*
                if (searchUserTask == search_users) {
                    refreshListView();
                } else {
                    refreshScrollView();
                }*/
            } else {
                //TODO:搜索错误
            }
        }
    }
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            noResultTextView.setVisibility(View.GONE);
            resultUsers.clear();
            String content = textView.getText().toString();
            if (content.isEmpty()) {
                textView.setError(getString(R.string.search_content_missing));
                return;
            }
            if (!CheckValid.isInputValid(content)) {
                textView.setError(getString(R.string.invalid_input_in_search));
                return;
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            if (searchUserTask == null) {
                /*
                onSearchProgressBar.setVisibility(View.VISIBLE);
                searchTask = new SearchTask(view == searchUserButton ? search_users : search_by_tags, content);
                searchTask.execute((Void) null);
                */
            } else {
                myToast.show(getString(R.string.toast_in_searching));
            }
        }
    }
}