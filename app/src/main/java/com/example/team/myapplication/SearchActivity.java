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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.CheckValid;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchActivity extends GeneralActivity {
    private Button searchUserButton;
    private Button searchTagButton;
    private ListView userNameListView;
    private MyScrollView pictureScrollView;
    private LinearLayout scrollViewContent;
    private LinearLayout scrollViewContentLeft;
    private LinearLayout scrollViewContentRight;

    public ArrayList<String> resultUsers;
    public ArrayList<GalleryItem> resultPictures;
    private SearchUserTask searchUserTask;
    private SearchTagTask searchTagTask;
    private ProgressBar onSearchProgressBar;
    private EditText textView;
    private MyToast myToast;
    private TextView noResultTextView;
    private List<Map<String, Object>> listItems;
    private int page = 1;
    private boolean end = false;
    private Context context=this;
    private DB db = new DB(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("搜索");
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        listItems = new ArrayList<>();
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
        scrollViewContentLeft = (LinearLayout) findViewById(R.id.scroll_content_left);
        scrollViewContentRight = (LinearLayout) findViewById(R.id.scroll_content_right);

        resultUsers = new ArrayList<>();
        resultPictures = new ArrayList<>();
        searchUserTask = null;
        myToast = new MyToast(getApplicationContext());

        /**
         * 设置返回结果界面初始化
         */
        userNameListView.setVisibility(View.GONE);
        userNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toUserPageActivity(view.findViewById(R.id.Names));
            }
        });
        pictureScrollView.setVisibility(View.GONE);

        /**
         * 添加 搜索用户 监听器
         */
        searchUserButton.setOnClickListener(new MyOnClickListener1());
        /**
         * 添加 搜索具有该标签的图片 监听器
         */
        searchTagButton.setOnClickListener(new MyOnClickListener2());

    }

    public void toUserPageActivity(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("user_name", ((TextView) view).getText());
        //TODO 加入查看个人主页时传入的其他参数
        startActivity(intent);
    }

    /**
     * 更新搜素 用户名 的结果
     */
    public void refreshListView() {
        if (resultUsers.size() == 0) {
            noResultTextView.setText(getString(R.string.no_search_result_users));
            noResultTextView.setVisibility(View.VISIBLE);
        }
        listItems.clear();
        for (int i = 0; i < resultUsers.size(); i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("用户名", resultUsers.get(i));
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.layout_user_name, new String[]{"用户名"}, new int[]{R.id.Names});
        userNameListView.setAdapter(simpleAdapter);
        userNameListView.setVisibility(View.VISIBLE);
    }

    /**
     * 更新搜素 图片 的结果
     */
    public void refreshScrollView() {
        if (resultPictures.size() == 0) {
            noResultTextView.setText(getString(R.string.no_search_result_tags));
            noResultTextView.setVisibility(View.VISIBLE);
        }
        scrollViewContentLeft.removeAllViews();
        scrollViewContentRight.removeAllViews();
        for (int i = 0; i < resultPictures.size(); i++) {
            resultPictures.get(i).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toViewPictureActivity(view);
                }
            });
            if (i % 2 == 0) {
                scrollViewContentLeft.addView(resultPictures.get(i));
            } else {
                scrollViewContentRight.addView(resultPictures.get(i));
            }
        }
        scrollViewContent.setVisibility(View.VISIBLE);
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
        HashMap<String, String> returnmap;

        public SearchUserTask(String _searchContent) {
            searchContent = _searchContent;
        }

        @Override
        protected void onPreExecute() {
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
                String url = "http://192.168.253.1/search/user/page/" + page + "/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", searchContent);
                returnmap = new JsonPost(map, url, "searchuser").getReturnmap();
                Thread.sleep(1000);
            } catch (MyException.zeroException e) {
                end = true;
                return false;
                //TODO:搜索结果为空
            } catch (Exception e) {
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
                    resultUsers.add(returnmap.get("name" + i));
                }
                refreshListView();
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
        HashMap<String, String> returnmap;
        private GalleryItem galleryItem[];

        public SearchTagTask(String _searchContent, GalleryItem[] galleryItem) {
            searchContent = _searchContent;
            this.galleryItem = galleryItem;
        }

        @Override
        protected void onPreExecute() {
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
                String url = "http://192.168.253.1/search/tag/page/" + page + "/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("tag", searchContent);
                new JsonPost(map, url, db,galleryItem);
                Thread.sleep(1000);
            } catch (MyException.zeroException e) {
                end=true;
                return false;
                //TODO:搜索结果为空
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            onSearchProgressBar.setVisibility(View.GONE);
            searchTagTask = null;
            if (success) {
                for (GalleryItem _galleryitem : galleryItem) {
                    if (_galleryitem.imageView.getContentDescription() != null) {
                        resultPictures.add(_galleryitem);
                    }
                }
                refreshScrollView();
            }
                /*
                if (searchUserTask == search_users) {
                    refreshListView();
                } else {
                    refreshScrollView();
                }*/
            else {
                //TODO:搜索错误
            }
        }
    }

    class MyOnClickListener1 implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            resultUsers.clear();
            noResultTextView.setVisibility(View.GONE);
            userNameListView.setVisibility(View.GONE);
            scrollViewContent.setVisibility(View.GONE);
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
            if (searchUserTask == null && searchTagTask == null) {
                /*
                onSearchProgressBar.setVisibility(View.VISIBLE);
                searchTask = new SearchTask(view == searchUserButton ? search_users : search_by_tags, content);
                searchTask.execute((Void) null);
                */
                searchUserTask = new SearchUserTask(content);
                searchUserTask.execute();

            } else {
                myToast.show(getString(R.string.toast_in_searching));
            }
        }
    }

    class MyOnClickListener2 implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            resultUsers.clear();
            resultPictures.clear();
            noResultTextView.setVisibility(View.GONE);
            userNameListView.setVisibility(View.GONE);
            scrollViewContent.setVisibility(View.GONE);
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
            if (searchUserTask == null && searchTagTask == null) {
                /*
                onSearchProgressBar.setVisibility(View.VISIBLE);
                searchTask = new SearchTask(view == searchUserButton ? search_users : search_by_tags, content);
                searchTask.execute((Void) null);
                */
                GalleryItem galleryItems[] = new GalleryItem[8];
                for (int i = 0; i < 8; i++) {
                    galleryItems[i] = new GalleryItem(context);
                }
                searchTagTask = new SearchTagTask(content,galleryItems);
                searchTagTask.execute();

            } else {
                myToast.show(getString(R.string.toast_in_searching));
            }
        }
    }
}