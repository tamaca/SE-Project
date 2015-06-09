package com.example.team.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.ImageGet;
import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.Network.NetworkState;
import com.example.team.myapplication.util.CheckValid;
import com.example.team.myapplication.util.Comment;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewPictureActivity extends GeneralActivity {
    ImageView imgview;
    private EditText editText;
    private LinearLayout commentView;
    private LinearLayout tagView;
    private ProgressBar progressBar;
    private List<Comment> comments;
    public ArrayList<Tag> tags;
    private UploadComment uploadComment = null;
    private TextView author;
    private TextView uploadTime;
    private TextView manageTagsButton;
    private DB db = new DB(this);
    private int likeNumber;
    private ImageButton like;
    private MyToast myToast;
    private TextView likeText;
    private boolean isLike = false;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private getImageInformationProgress mAuthTask;
    private EditText editTextInDialog;
    private boolean isEditing = false;
    private getImageFromIdProgress mAuthTask2;
    //TODO 当作者名和用户相同时设置为true
    private boolean isMe = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查看图片");
        setContentView(R.layout.activity_view_picture);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 初始化变量
         */
        imgview = (ImageView) findViewById(R.id.imageView8);
        editText = (EditText) findViewById(R.id.comment_text);
        commentView = (LinearLayout) findViewById(R.id.comment_view);
        tagView = (LinearLayout) findViewById(R.id.tags_in_view_picture);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        author = (TextView) findViewById(R.id.author);
        like = (ImageButton) findViewById(R.id.like_button);
        uploadTime = (TextView) findViewById(R.id.upload_time);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout4);
        likeText = (TextView) findViewById(R.id.textView5);
        manageTagsButton = (TextView) findViewById(R.id.manage_tag);
        comments = new ArrayList<>();
        tags = new ArrayList<>();
        myToast = new MyToast(this);
        /**
         * 获取所有的标签控件
         * 标签控件总共有四个状态，设置标签内容时注意设置状态。
         */
        tags.add((Tag) this.findViewById(R.id.tag1));
        tags.add((Tag) this.findViewById(R.id.tag2));
        tags.add((Tag) this.findViewById(R.id.tag3));
        tags.add((Tag) this.findViewById(R.id.tag4));
        tags.add((Tag) this.findViewById(R.id.tag5));

        /**
         * 给每个标签控件设置监听器
         * 对每个添加，删除标签操作都使用对话框来确认
         */
        for (int i = 0; i < 5; i++) {
            final int j = i;
            tags.get(i).removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ViewPictureActivity.this).setMessage("删除标签?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //TODO 删除该用户的这个标签，请把UI操作放在删除完成之后


                            /**
                             * 这是在删除后的UI操作
                             * tagContent是标签内容
                             */

                            tagView.post(new Runnable() {
                                @Override
                                public void run() {
                                    String tagContent = tags.get(j).tagText.getText().toString();
                                    tags.get(j).changeState(Tag.addable);
                                    refreshTags();
                                }
                            });


                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();
                }
            });

            tags.get(i).addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTextInDialog = new EditText(getApplicationContext());
                    editTextInDialog.setTextColor(Color.argb(255, 0, 0, 0));
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ViewPictureActivity.this)
                                    .setView(editTextInDialog)
                                    .setMessage("请输入标签内容");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            tagView.post(new Runnable() {
                                @Override
                                public void run() {

                                    /**
                                     * 检查输入合法性
                                     */
                                    final String tagContent = editTextInDialog.getText().toString();
                                    if (!CheckValid.isTagUnique(tags, tagContent)) {
                                        return;
                                    }
                                    if (!CheckValid.isTagValid(tagContent)) {
                                        return;
                                    }
                                    if (tagContent.isEmpty()) {
                                        return;
                                    }
                                    //TODO 将标签上传 UI操作放在上传完成之后

                                    /**
                                     * 这是添加标签的UI操作
                                     * tagContent是标签内容
                                     */
                                    tags.get(j).tagText.setText(tagContent);
                                    tags.get(j).changeState(Tag.removable);
                                    refreshTags();

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();
                }
            });
        }
        /**
         * 给作者添加跳转到个人界面的监听器
         */
        author.setOnClickListener(new ToUserPageListener());

        /**
         * 给赞按钮添加监听器
         */
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLike(view);
            }
        });

        /**
         * 给编辑标签添加监听器
         */
        manageTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditing = !isEditing;
                for (int i = 0; i < 5; i++) {
                    tags.get(i).changeToEditState(isEditing);
                }
                manageTagsButton.setText(isEditing ? "确定" : "管理标签");
            }
        });
        getData();
    }

    //获取图片和信息
    public void getData() {
        try {
            Intent intent = getIntent();
            String type = (String) intent.getExtras().get("type");
            if (type.equals("online")) {
                //联网获取图片信息
                String bigurl = (String) intent.getExtras().get("bigurl");
                String informationurl = "http://192.168.253.1/Kevin/image_detail/";
                String imageid = (String) intent.getExtras().get("imageid");
                new ImageGet(imgview, bigurl, imageid, db, "big");
                mAuthTask = new getImageInformationProgress(informationurl, imageid, db);
                mAuthTask.execute();
            } else if (type.equals("offline")) {
                //未联网 先读取数据库中数据
                String filepath = (String) intent.getExtras().get("filepath");
                String imageid = (String) intent.getExtras().get("imageid");
                File imageFile = new File(filepath);
                if (imageFile.exists()) {
                    //数据库中有该图片
                    Bitmap bitmap = Localstorage.getBitmapFromSDCard(filepath);
                    imgview.setImageBitmap(bitmap);
                }
            } else {
                String imageid = (String) intent.getExtras().get("imageid");
                String url = "http://192.168.253.1/big_get/" + imageid + "/";
                 mAuthTask2 = new getImageFromIdProgress(url, imageid);
                mAuthTask2.execute();
            }
        } catch (Exception e) {
            //TODO:获取失败 让用户联网刷新页面 TO孙晓宇
            e.printStackTrace();
        }
    }

    /**
     * 让有内容的tag标签排在前边
     */
    public void refreshTags() {
        int i = 0, j = 0;
        while (j < 5) {
            if (tags.get(j).currentState == Tag.showingTag || tags.get(j).currentState == Tag.removable) {
                if (i == j) {
                    i++;
                } else {
                    tags.get(i).tagText.setText(tags.get(j).tagText.getText().toString());
                    int y = tags.get(j).currentState;
                    tags.get(j).changeState(tags.get(i).currentState);
                    tags.get(i).changeState(y);
                    i++;
                }
            }
            j++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_picture, menu);
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

    /**
     * 提交评论按钮的响应
     *
     * @param view
     */
    public void submitComment(View view) {
        String comment = editText.getText().toString();//获取输入的评论
        if (comment.isEmpty()) {
            return;
        } else if (comment.charAt(0) == ' ') {
            editText.setError("首字为空格");
            return;
        } else if (comment.length() > 120) {
            editText.setError("评论不能超过120字");
            return;
        }
        if (uploadComment == null) {
            showProgress(true);
            uploadComment = new UploadComment(comment);
            uploadComment.execute((Void) null);
        } else {
            Toast.makeText(getApplicationContext(), "5秒内只能上传一次评论", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将评论添加到页面中的UI操作
     *
     * @param comment
     */
    public void addComment(String comment) {
        comments.add(new Comment(getApplicationContext(), LoginState.username, comment));
        commentView.addView(comments.get(comments.size() - 1));
        comments.get(comments.size() - 1).textView1.setOnClickListener(new ToUserPageListener());
        linearLayout.postInvalidate();
        Thread refresh = new Thread(new Refresh());
        refresh.start();
    }

    public class getImageInformationProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private DB db;
        private String imageid;
        private HashMap<String, String> returnmap;

        getImageInformationProgress(String url, String imageid, DB db) {
            this.url = url;
            this.imageid = imageid;
            this.db = db;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", imageid);
                returnmap = new JsonPost(map, url, "imageinfo", db).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                String baseurl = "http://192.168.253.1/media/";
                author.setText(returnmap.get("userid"));
                if (LoginState.username.equals(returnmap.get("userid"))) {
                    manageTagsButton.setVisibility(View.VISIBLE);
                }
                imgview.setContentDescription(returnmap.get(baseurl + "origin"));
                isLike = (returnmap.get("islike").equals("true"));
                String likenumber = returnmap.get("likenumber");
                int _likenumber = Integer.parseInt(likenumber);
                likeText.setText(_likenumber < 10000 ? likenumber : _likenumber / 10000 + "万+");
                //时间显示
                uploadTime.setText(returnmap.get("updatetime"));
                String _tagnum=returnmap.get("tagnum");
                int tagnum=Integer.parseInt(_tagnum);
                for(int i=0;i<tagnum;i++)
                {
                    tags.get(i).tagText.setText(returnmap.get("tagname"+i));
                    tags.get(i).changeState(Tag.showingTag);
                }
                refreshTags();
            } else {
                myToast.show(getString(R.string.toast_fetching_information_failed));
            }
            mAuthTask = null;
        }
    }

    public class getImageFromIdProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private String imageid;
        HashMap<String, String> returnmap;

        getImageFromIdProgress(String url, String imageid) {
            this.url = url;
            this.imageid = imageid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String key[] = {"image_big"};
                returnmap = new JsonGet(url, key).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                String bigurl = returnmap.get("image_big");
                String baseurl = "http://192.168.253.1/media/";
                new ImageGet(imgview, baseurl+bigurl, imageid, db, "big");
                //TODO:后续处理
            } else {
                myToast.show(getString(R.string.toast_downloading_picture_error));
            }
            mAuthTask2 = null;
        }
    }

    public class likeProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private DB db;
        HashMap<String, String> returnmap;

        likeProgress(String url, String imageid, DB db) {
            this.url = url;
            this.db = db;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String key[] = {"like", "islike"};
                returnmap = new JsonGet(url, key, db).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //TODO:后续处理
            } else {
                myToast.show(getString(R.string.toast_fetching_information_failed));
            }
            mAuthTask = null;
        }
    }
    /**
     * 点击赞的相应函数
     *
     * @param view
     */
    public void changeLike(View view) {
        onLikeChange(isLike);
    }

    /**
     * 点击赞之后的UI变化
     *
     * @param zan
     */
    public void onLikeChange(boolean zan) {
        if (!zan) {
            likeNumber++;
            //TODO 上传赞
            like.setBackgroundResource(R.drawable.liked);
            likeText.setText(likeNumber < 10000 ? String.valueOf(likeNumber) : likeNumber / 10000 + "万+");
        } else {
            likeNumber--;
            //TODO 上传取消赞
            like.setBackgroundResource(R.drawable.like);
            if (likeNumber == 0) {
                likeText.setText(null);
            } else {
                likeText.setText(likeNumber < 10000 ? String.valueOf(likeNumber) : likeNumber / 10000 + "万+");
            }
        }
        isLike = !isLike;
    }

    public void showProgress(boolean show) {

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 跳转到查看个人主页
     *
     * @param view
     */
    public void toUserPageActivity(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("user_name", ((TextView) view).getText());
        //TODO 加入查看个人主页时传入的其他参数
        startActivity(intent);
    }

    /**
     * 跳转到查看原图
     *
     * @param view
     */
    public void toPictureActivity(View view) {
        if (NetworkState.isNetworkConnected(getApplicationContext())) {
            if (!NetworkState.isWifiEnable(getApplicationContext())) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewPictureActivity.this).setMessage("将会消耗流量下载，继续？");
                builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                        //TODO 在这里添加想传入查看原图页面的信息，比如图片主人的名字，图片ID 啥的。

                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            } else {
                Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                //TODO 在这里添加想传入查看原图页面的信息，比如图片主人的名字，图片ID 啥的。

                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, PictureActivity.class);
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            intent.putExtra("pic", bitmap);
            startActivity(intent);
        }


    }

    class Refresh implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                scrollView.scrollTo(0, linearLayout.getMeasuredHeight());
                commentView.postInvalidate();
                Log.d("comment--->>", "refreshing");
                Thread.currentThread().interrupt();
            }
        }
    }
    class GetCommentProgress extends AsyncTask<Void, Void, Boolean> {
        private String url;
        private DB db;
        public GetCommentProgress(String url,DB db) {
            this.url=url;
            this.db=db;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

            } catch (Exception e) {
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            if (success) {
                //addComment(comment);
                editText.setText(null);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            uploadComment = null;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            } else {
                myToast.show(getString(R.string.toast_comment_failed));
                uploadComment = null;
            }
        }

    }
    class UploadComment extends AsyncTask<Void, Void, Boolean> {
        private String comment;

        public UploadComment(String comment) {
            this.comment = comment;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                /*String key = "1234567891234567";
                String username= LoginState.username;
                AES aesEncrypt = new AES(key);
                String encryptUsername=aesEncrypt.encrypt(username);
                String url = "http://192.168.137.1/php22/index.php";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", encryptUsername);
                map.put("content", comment);
                JsonPost post = new JsonPost(map, url,3,db);*/
                Thread.sleep(100);
            } catch (Exception e) {
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            if (success) {
                Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                addComment(comment);
                editText.setText(null);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            uploadComment = null;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            } else {
                myToast.show(getString(R.string.toast_comment_failed));
                uploadComment = null;
            }
        }

    }
    class UploadTagProgress extends AsyncTask<Void, Void, Boolean> {
        private String comment;

        public UploadTagProgress(String comment) {
            this.comment = comment;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                /*String key = "1234567891234567";
                String username= LoginState.username;
                AES aesEncrypt = new AES(key);
                String encryptUsername=aesEncrypt.encrypt(username);
                String url = "http://192.168.137.1/php22/index.php";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", encryptUsername);
                map.put("content", comment);
                JsonPost post = new JsonPost(map, url,3,db);*/
                Thread.sleep(100);
            } catch (Exception e) {
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            if (success) {
                Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                addComment(comment);
                editText.setText(null);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            uploadComment = null;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            } else {
                myToast.show(getString(R.string.toast_comment_failed));
                uploadComment = null;
            }
        }

    }
    /**
     * 跳转到个人主页的监听器
     */
    public class ToUserPageListener implements TextView.OnClickListener {

        @Override
        public void onClick(View view) {
            toUserPageActivity(view);
        }
    }
}