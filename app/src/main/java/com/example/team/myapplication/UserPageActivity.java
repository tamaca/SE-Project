package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.GalleryItem;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.ScrollViewListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPageActivity extends GeneralActivity implements ScrollViewListener {
    static public boolean concern;
    static public boolean blacklist;
    private boolean isMe = false;
    private TextView name;
    private Button concernButton;
    private Button hateButton;
    private Button manageButton;
    private String userName;
    private LinearLayout gallery;
    private Toast toast;
    private ProgressBar inLoadingPicture;
    private ProgressBar inChangingRelationship;
    private GetPicture getPicture = null;
    private LinearLayout scrollContent;
    private int pictureCount;
    public ArrayList<GalleryItem> galleryItems = null;
    private BlackConcerenTask mAuthTask = null;
    private GetInfomation getInfomationtask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Intent intent = getIntent();
        userName = intent.getExtras().get("user_name").toString();
        name = (TextView) findViewById(R.id.name);
        name.setText(userName);
        if (userName.equals(LoginState.username)) {
            isMe = true;
        }
        setTitle((isMe ? "我" : userName) + "的个人主页");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化变量
        concernButton = (Button) findViewById(R.id.button6);
        hateButton = (Button) findViewById(R.id.button5);
        manageButton = (Button) findViewById(R.id.button8);
        gallery = (LinearLayout) findViewById(R.id.gallery);
        galleryItems = new ArrayList<>();
        toast = null;
        inLoadingPicture = (ProgressBar) findViewById(R.id.progressBar4);
        inChangingRelationship = (ProgressBar) findViewById(R.id.progressBar5);
        scrollContent = (LinearLayout) findViewById(R.id.scroll_content);
        //////////
        concernButton.setOnClickListener(new OnClickConcernListener());
        hateButton.setOnClickListener(new OnClickHateListener());
        loadView(isMe);


    }

    public void loadView(boolean my) {
        if (my) {
            //加载我的个人主页
            if (!getGallery(userName)) {
                findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            }
            concernButton.setVisibility(View.GONE);
            hateButton.setVisibility(View.GONE);

        } else {
            //加载别人的主页
            //TODO 获取用户和这个人的关系
            manageButton.setVisibility(View.GONE);
            getInfomationtask = new GetInfomation("Kevin2");
            getInfomationtask.execute();
        }
    }

    public void loadContent(boolean concern, boolean blacklist) {
        if (concern) {
            gallery.setVisibility(View.VISIBLE);
            if (!getGallery(userName)) {
                findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.textView3).setVisibility(View.GONE);
            concernButton.setText(getString(R.string.remove_from_concern));
        } else {
            gallery.setVisibility(View.INVISIBLE);
            findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            concernButton.setText(getString(R.string.concern));
        }
        if (blacklist) {
            hateButton.setText(getString(R.string.remove_from_blackList));
        } else {
            hateButton.setText(getString(R.string.add_to_blacklist));
        }
          /*  case normal:
                gallery.setVisibility(View.GONE);
                findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                concernButton.setText(getString(R.string.concern));
                hateButton.setText(getString(R.string.add_to_blacklist));
                break;
            case concern:
                gallery.setVisibility(View.VISIBLE);
                if (!getGallery(userName)) {
                    findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.textView3).setVisibility(View.GONE);
                concernButton.setText(getString(R.string.remove_from_concern));
                hateButton.setText(getString(R.string.add_to_blacklist));
                break;
            case hate:
                gallery.setVisibility(View.GONE);
                findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                concernButton.setText(getString(R.string.concern));
                hateButton.setText(getString(R.string.remove_from_blackList));
                break;
            case hate | concern:
                gallery.setVisibility(View.VISIBLE);
                if (!getGallery(userName)) {
                    findViewById(R.id.textView4).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.textView3).setVisibility(View.GONE);
                concernButton.setText(getString(R.string.remove_from_concern));
                hateButton.setText(getString(R.string.remove_from_blackList));
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_page, menu);
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
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void toViewPictureActivity(View view) {
        Intent intent = new Intent(this, ViewPictureActivity.class);
        //view.setDrawingCacheEnabled(true);
        //Bitmap bitmap = view.getDrawingCache();
        String bigurl = view.getContentDescription().toString();
        intent.putExtra("bigurl", bigurl);
        startActivity(intent);
    }

    //添加图片进gallery的方法
    public void addGalleryItem(Bitmap bitmap) {
        GalleryItem galleryItem = new GalleryItem(this, bitmap);
        galleryItem.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toViewPictureActivity(view);
            }
        });

        galleryItems.add(galleryItem);
        gallery.addView(galleryItems.get(galleryItems.size() - 1));

    }

    public boolean getGallery(String userName) {
        //TODO 在这里加载该用户的图片，如果用户没有图片，返回false,如果有，把 pictureCount 设置为该用户照片总数；
        // addGalleryItem(bitmap);
        pictureCount = 0;
        return false;
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (galleryItems.size() != pictureCount) {
                if (inLoadingPicture.getVisibility() == View.GONE) {
                    inLoadingPicture.setVisibility(View.VISIBLE);
                    if (getPicture == null) {
                        getPicture = new GetPicture();
                        getPicture.execute((Void) null);
                    }
                }
            }
        }
    }

    class OnClickConcernListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //点击 关注/取消关注 按钮

            mAuthTask = new BlackConcerenTask("Kevin2",1);
            mAuthTask.execute();
        }
    }

    class OnClickHateListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //我 拉黑/取黑 该用户
            if (blacklist) {
                //TODO 把该用户从黑名单中移除
                blacklist = !blacklist;
                hateButton.setText(getString(R.string.add_to_blacklist));
            } else {
                blacklist = !blacklist;
                hateButton.setText(getString(R.string.remove_from_blackList));
            }
        }
    }

    public class BlackConcerenTask extends AsyncTask<Void, Void, Boolean> {
        private String otherUsername;
        private int type;

        BlackConcerenTask(String otherUsername, int type) {
            this.otherUsername = otherUsername;
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = null;
                if (type == 1) {
                    if (concern) {
                        url = "http://192.168.253.1/Kevin/concern/delete/";
                    } else {
                        url = "http://192.168.253.1/Kevin/concern/insert/";
                    }
                } else {
                    if (blacklist) {
                        url = "http://192.168.253.1/Kevin/blacklist/delete/";
                    } else {
                        url = "http://192.168.253.1/Kevin/blacklist/insert/";
                    }
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", otherUsername);
                new JsonPost(map, url, 0);
                Thread.sleep(100);

            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Boolean a = success;
            mAuthTask = null;
            if (success) {
                if(type==1) {
                    if (concern) {
                        concern = !concern;
                        concernButton.setText(getString(R.string.remove_from_concern));
                    } else {
                        concern = !concern;
                        concernButton.setText(getString(R.string.concern));
                    }
                    loadContent(concern, blacklist);
                }else
                {
                    if (blacklist) {
                        blacklist = !blacklist;
                        hateButton.setText(getString(R.string.remove_from_blackList));
                    } else {
                        blacklist = !blacklist;
                        hateButton.setText(getString(R.string.add_to_blacklist));
                    }
                    loadContent(concern, blacklist);
                }
            } else {
                    Toast.makeText(getApplicationContext(), "操作失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

    }

    public class GetInfomation extends AsyncTask<Void, Void, Boolean> {
        private String otherUsername;

        GetInfomation(String otherUsername) {
            this.otherUsername = otherUsername;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = "http://192.168.253.1/Kevin/relation_page/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", otherUsername);
                JsonPost jsonPost = new JsonPost(map, url, 0);
                JSONObject jsonObject = jsonPost.getReturnjsonObject();
                String _concern = jsonObject.getString("concern");
                String _blacklist = jsonObject.getString("blacklist");
                concern = _concern.equals("true");
                blacklist = _blacklist.equals("true");
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            loadContent(concern, blacklist);
        }

        @Override
        protected void onCancelled() {
            getInfomationtask = null;
        }

    }

    class GetPicture extends AsyncTask<Void, Void, Boolean> {
        public GetPicture() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                //TODO 添加几个图片到 galleryItems 里
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            inLoadingPicture.setVisibility(View.GONE);
            getPicture = null;
            if (success) {
                gallery.postInvalidate();
            } else {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "刷新图片失败", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }


}