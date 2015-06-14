package com.example.team.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.ImageGet;
import com.example.team.myapplication.Network.JsonGet;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.Network.NetworkState;
import com.example.team.myapplication.Network.Origindownload;
import com.example.team.myapplication.util.CheckValid;
import com.example.team.myapplication.util.Comment;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.LoadingView;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyScrollView;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.ScrollViewListener;
import com.example.team.myapplication.util.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewPictureActivity extends GeneralActivity implements ScrollViewListener {
    ImageView imgview;
    private EditText editText;
    private LinearLayout commentView;
    private LinearLayout tagView;
    private ProgressBar refreshingProgressBar;
    private List<Comment> comments;
    public ArrayList<Tag> tags;
    private UploadComment uploadComment = null;
    private TextView author;
    private TextView uploadTime;
    private TextView manageTagsButton;
    private DB db = new DB(this);
    private String likeNumber;
    private ImageButton like;
    private MyToast myToast;
    private TextView likeText;
    private boolean isLike = false;
    private MyScrollView scrollView;
    private LinearLayout scrollContent;
    private getImageInformationProgress mAuthTask;
    private EditText editTextInDialog;
    private boolean isEditing = false;
    private View showFail;
    private UploadTagProgress uploadTagProgress = null;
    private String imageid = null;
    private LikeProgress likeProgress = null;
    private LoadingView loadingView;
    private GetTagProgress getTagProgress = null;
    private int commentpage = 1;
    private boolean end = false;
    private GetCommentProgress getCommentProgress = null;
    private GetOrigin getOrigin=null;
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
        refreshingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        author = (TextView) findViewById(R.id.author);
        like = (ImageButton) findViewById(R.id.like_button);
        uploadTime = (TextView) findViewById(R.id.upload_time);
        scrollView = (MyScrollView) findViewById(R.id.scrollView);
        scrollContent = (LinearLayout) findViewById(R.id.linearLayout4);
        likeText = (TextView) findViewById(R.id.textView5);
        manageTagsButton = (TextView) findViewById(R.id.manage_tag);
        showFail = findViewById(R.id.show_fail_layout);
        comments = new ArrayList<>();
        tags = new ArrayList<>();
        scrollView.setScrollViewListener(this);
        myToast = new MyToast(this);
        loadingView = new LoadingView(this);
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

                            uploadTagProgress = new UploadTagProgress(tags.get(j).getTagid(), imageid, "tagdelete", tags.get(j));
                            uploadTagProgress.execute();

                            /**
                             * 这是在删除后的UI操作
                             * tagContent是标签内容
                             */
/*
                            tagView.post(new Runnable() {
                                @Override
                                public void run() {
                                    String tagContent = tags.get(j).tagText.getText().toString();
                                    tags.get(j).changeState(Tag.addable);
                                    refreshTags();
                                }
                            });
*/

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
                            uploadTagProgress = new UploadTagProgress(editTextInDialog.getText().toString(), imageid, "taginsert", tags.get(j));
                            uploadTagProgress.execute();
                            /*tagView.post(new Runnable() {
                                @Override
                                public void run() {
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

                                    tags.get(j).tagText.setText(tagContent);
                                    tags.get(j).changeState(Tag.removable);
                                    refreshTags();

                                }
                            });*/
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
         * 给imgView添加监听器
         */
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPictureActivity(view);
            }
        });
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
                if (likeProgress == null) {
                    likeProgress = new LikeProgress(imageid, db);
                    likeProgress.execute();
                }
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
        /**
         * 设置点击刷新界面的背景色为灰色
         */
        showFail.setBackgroundColor(Color.argb(0xff, 0xcc, 0xcc, 0xcc));
        /**
         * 给点击刷新界面添加监听器
         */

        showFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFail.setVisibility(View.GONE);
                refreshingProgressBar.setVisibility(View.VISIBLE);
                getData();
            }
        });
        getData();
    }

    //获取图片和信息
    public void getData() {
        try {
            Intent intent = getIntent();
            String type = (String) intent.getExtras().get("type");
            Comment newcomments[] = new Comment[8];
            for (int i = 0; i < 8; i++) {
                newcomments[i] = new Comment(this);
            }
            if (type.equals("online")) {
                //联网获取图片信息
                String bigurl = (String) intent.getExtras().get("bigurl");
                String informationurl = "http://192.168.253.1/" + LoginState.username + "/image_detail/";
                imageid = (String) intent.getExtras().get("imageid");
                new ImageGet(imgview, bigurl, imageid, db, "big");
                mAuthTask = new getImageInformationProgress(informationurl, imageid, db);
                mAuthTask.execute();
                getCommentProgress = new GetCommentProgress(db, newcomments);
                getCommentProgress.execute();
            } else if (type.equals("offline")) {
                //未联网 先读取数据库中数据
                String filepath = (String) intent.getExtras().get("filepath");
                imageid = (String) intent.getExtras().get("imageid");
                File imageFile = new File(filepath);
                if (imageFile.exists()) {
                    //数据库中有该图片
                    Bitmap bitmap = Localstorage.getBitmapFromSDCard(filepath);
                    imgview.setImageBitmap(bitmap);
                    Cursor mCursor = db.imageselect(imageid);
                    if (mCursor.moveToFirst()) {
                        String userid = mCursor.getString((mCursor.getColumnIndex("m_image_userid")));
                        String islike = mCursor.getString((mCursor.getColumnIndex("m_image_islike")));
                        likeNumber = mCursor.getString((mCursor.getColumnIndex("m_image_likenumber")));
                        String updatedate = mCursor.getString((mCursor.getColumnIndex("m_image_updatedate")));
                        author.setText(userid);
                        if (LoginState.username.equals(userid)) {
                            manageTagsButton.setVisibility(View.VISIBLE);
                        }
                        imgview.setContentDescription(null);
                        isLike = (islike.equals("true"));
                        changeLike();
                        //时间显示
                        uploadTime.setText(updatedate);
                    } else {
                        //数据库存储错误
                        throw new Exception();
                    }
                    refreshingProgressBar.setVisibility(View.GONE);
                } else {
                    throw new Exception();
                }
            } else {
                imageid = (String) intent.getExtras().get("imageid");
                if (imageid == null) {
                    //id错误
                    throw new Exception();
                }
                String url = "http://192.168.253.1/big_get/" + imageid + "/";
                getImageFromIdProgress getImageFromIdProgress = new getImageFromIdProgress(url, imageid);
                getImageFromIdProgress.execute();
            }
        } catch (Exception e) {
            showFail.setVisibility(View.VISIBLE);
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
        Comment newcomments[] = new Comment[8];
        for (int i = 0; i < 8; i++) {
            newcomments[i] = new Comment(this);
        }
        if (uploadComment != null) {
            myToast.show("5秒内只能上传一次评论");
        } else {
            uploadComment = new UploadComment(db, comment, newcomments);
            uploadComment.execute((Void) null);
        }
    }

    /**
     * 添加一条评论到评论数组里。
     * 给用户名添加监听器。
     */
    /*
    public void addComment(String name, String comment) {
        comments.add(new Comment(getApplicationContext(), name, comment));
        comments.get(comments.size() - 1).textView1.setOnClickListener(new ToUserPageListener());
    }*/

    /**
     * 刷新界面中的评论
     */
    public void refreshComments() {
        commentView.removeAllViews();
        for (int i = 0; i < comments.size(); i++) {
            commentView.addView(comments.get(i));
        }
        commentView.postInvalidate();
        scrollView.scrollTo(0, scrollContent.getMeasuredHeight());
        commentView.postInvalidate();
    }

    @Override
    public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (y + scrollView.getMeasuredHeight() + 50 > scrollContent.getMeasuredHeight()) {
            if (scrollContent.getChildAt(scrollContent.getChildCount() - 1) != loadingView) {
                scrollContent.addView(loadingView);
                Comment newcomments[] = new Comment[8];
                for (int i = 0; i < 8; i++) {
                    newcomments[i] = new Comment(this);
                }
                getCommentProgress = new GetCommentProgress(db, newcomments);
                getCommentProgress.execute();
            }

        }
    }

    private void changeLike() {
        if (isLike) {
            like.setBackgroundResource(R.drawable.liked);
            int _likenumber = Integer.parseInt(likeNumber);
            likeText.setText(_likenumber < 10000 ? likeNumber : _likenumber / 10000 + "万+");
        } else {
            like.setBackgroundResource(R.drawable.like);
            int _likenumber = Integer.parseInt(likeNumber);
            if (_likenumber == 0) {
                likeText.setText(null);
            } else {
                likeText.setText(_likenumber < 10000 ? likeNumber : _likenumber / 10000 + "万+");
            }
        }
    }

    public void showProgress(boolean show) {

        refreshingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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
     * 使用系统图库打开图片
     */
    public void openPicture(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
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
                        String bigfilepath = Localstorage.getImageFilePath(imageid, "big");
                        openPicture(bigfilepath);
                    }
                });
                builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            } else {
                String bigfilepath = Localstorage.getImageFilePath(imageid, "big");
                openPicture(bigfilepath);
            }
        } else {
            //TODO:异常
            /*
            Intent intent = new Intent(this, PictureActivity.class);
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            intent.putExtra("pic", bitmap);
            startActivity(intent);*/
        }


    }

    public void toOriginDownload(View view) {
        getOrigin=new GetOrigin(this);
        getOrigin.execute();
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
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", imageid);
                if (imageid == null) {
                    //图片id错误
                    throw new Exception();
                }
                returnmap = new JsonPost(map, url, "imageinfo", db).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            if (success) {
                String baseurl = "http://192.168.253.1/media/";
                author.setText(returnmap.get("userid"));
                if (LoginState.username.equals(returnmap.get("userid"))) {
                    manageTagsButton.setVisibility(View.VISIBLE);
                }
                imgview.setContentDescription(returnmap.get(baseurl + "origin"));
                isLike = (returnmap.get("islike").equals("true"));
                likeNumber = returnmap.get("likenumber");
                changeLike();
                //时间显示
                uploadTime.setText(returnmap.get("updatetime"));
                String _tagnum = returnmap.get("tagnum");
                int tagnum = Integer.parseInt(_tagnum);
                for (int i = 0; i < tagnum; i++) {
                    tags.get(i).tagText.setText(returnmap.get("tagname" + i));
                    tags.get(i).setTagid(returnmap.get("tagid" + i));
                    tags.get(i).changeState(Tag.showingTag);
                }
                refreshTags();
            } else {
                myToast.show(getString(R.string.toast_fetching_information_failed));
            }
            mAuthTask = null;
        }
    }

    public class GetTagProgress extends AsyncTask<Void, Void, Boolean> {

        private String url;
        private DB db;
        private HashMap<String, String> returnmap;

        GetTagProgress(String url, DB db) {
            this.url = url;
            this.db = db;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                returnmap = new JsonGet(url, db, "gettag").getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            getTagProgress = null;
            if (success) {
                String _tagnum = returnmap.get("tagnum");
                int tagnum = Integer.parseInt(_tagnum);
                for (int i = 0; i < tagnum; i++) {
                    tags.get(i).tagText.setText(returnmap.get("tagname" + i));
                    tags.get(i).setTagid(returnmap.get("tagid" + i));
                    tags.get(i).changeState(Tag.showingTag);
                }
                refreshTags();

            } else {
                myToast.show(getString(R.string.toast_fetching_information_failed));
            }
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
                String key = "image_big";
                returnmap = new JsonGet(url, key, "getimagefromid").getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            refreshingProgressBar.setVisibility(View.GONE);
            if (success) {
                String bigurl = returnmap.get("image_big");
                String baseurl = "http://192.168.253.1/media/";
                new ImageGet(imgview, baseurl + bigurl, imageid, db, "big");
            } else {
                myToast.show(getString(R.string.toast_downloading_picture_error));
            }
        }
    }

    public class LikeProgress extends AsyncTask<Void, Void, Boolean> {
        private DB db;
        HashMap<String, String> returnmap;

        LikeProgress(String imageid, DB db) {
            this.db = db;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (imageid == null) {
                    //图片id错误
                    throw new Exception();
                }
                String url = "http://192.168.253.1/" + LoginState.username + "/like_change/" + imageid + "/";
                returnmap = new JsonGet(url, db).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                isLike = (returnmap.get("islike").equals("true"));
                likeNumber = returnmap.get("likenumber");
                changeLike();
            } else {
                myToast.show(getString(R.string.toast_fetching_information_failed));
            }
            showProgress(false);
            likeProgress = null;
        }
    }

    class GetCommentProgress extends AsyncTask<Void, Void, Boolean> {
        private DB db;
        private HashMap<String, String> returnmap;
        private Comment[] mycomments;

        public GetCommentProgress(DB db, Comment[] mycomments) {
            this.db = db;
            this.mycomments = mycomments;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url = "http://192.168.253.1/comment_show/" + imageid + "/page/" + commentpage + "/";
                returnmap = new JsonGet(url, db, "getcomment").getReturnmap();
            } catch (MyException.zeroException e) {
                end = true;
                return false;
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            getCommentProgress = null;
            if (scrollContent.getChildAt(scrollContent.getChildCount() - 1) == loadingView) {
                scrollContent.removeView(loadingView);
            }
            if (success) {
                commentpage = commentpage + 1;
                String _commentnum = returnmap.get("commentnum");
                int commentnum = Integer.parseInt(_commentnum);
                for (int i = 0; i < commentnum; i++) {
                    mycomments[i].setCommentid(returnmap.get("commentid" + i));
                    mycomments[i].textView1.setText(returnmap.get("commentuser" + i));
                    mycomments[i].textView2.setText(returnmap.get("comment" + i));
                    comments.add(mycomments[i]);
                }
                refreshComments();
            } else {
                if(end){
                    myToast.show("没有更多评论了");
                }
                else {
                    myToast.show("评论获取错误");
                }
            }

        }

    }

    class UploadComment extends AsyncTask<Void, Void, Boolean> {
        private String comment;
        private Comment[] mycomments;
        private DB db;
        private HashMap<String, String> returnmap;

        public UploadComment(DB db, String comment, Comment[] mycomments) {
            this.db = db;
            this.comment = comment;
            this.mycomments = mycomments;
        }


        protected void onPreExecute() {
            scrollContent.removeView(loadingView);
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url = "http://192.168.253.1/" + LoginState.username + "/comment_insert/" + imageid + "/";
                if (imageid == null) {
                    //图片ID获取错误
                    throw new Exception();
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("comment", comment);
                returnmap = new JsonPost(map, url, "commentinsert", db).getReturnmap();
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
                commentpage = 2;
                editText.setText(null);
                comments.clear();
                String _commentnum = returnmap.get("commentnum");
                int commentnum = Integer.parseInt(_commentnum);
                for (int i = 0; i < commentnum; i++) {
                    mycomments[i].setCommentid(returnmap.get("commentid" + i));
                    mycomments[i].textView1.setText(returnmap.get("commentuser" + i));
                    mycomments[i].textView2.setText(returnmap.get("comment" + i));
                    comments.add(mycomments[i]);
                }
                refreshComments();
                //  getCommentProgress =new GetCommentProgress(db,context);
                // getCommentProgress.execute();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        uploadComment = null;
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
        private String tagnameorid;
        private String imageid;
        private String type;
        private Tag tag;
        private int errorType = 0;

        public UploadTagProgress(String tagnameorid, String imageid, String type, Tag tag) {
            this.tagnameorid = tagnameorid;
            this.imageid = imageid;
            this.type = type;
            this.tag = tag;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url;
                HashMap<String, String> map = new HashMap<String, String>();
                if (type.equals("taginsert")) {//插入TAG
                    if (!CheckValid.isTagUnique(tags, tagnameorid)) {
                        errorType = 1;
                        throw new Exception();
                    }
                    if (!CheckValid.isTagValid(tagnameorid)) {
                        errorType = 2;
                        throw new Exception();
                    }
                    if (tagnameorid.isEmpty()) {
                        errorType = 3;
                        throw new Exception();
                    }
                    url = "http://192.168.253.1/" + LoginState.username + "/tag_insert/";
                    map.put("tag", tagnameorid);
                    map.put("image_id", imageid);
                } else {//删除TAG
                    url = "http://192.168.253.1/" + LoginState.username + "/tag_delete/";
                    map.put("tag_id", tagnameorid);
                }
                if (imageid == null) {
                    //图片ID获取错误
                    throw new Exception();
                }
                new JsonPost(map, url, type, db).getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            if (success) {
                if (type.equals("taginsert")) {
                    String url = "http://192.168.253.1/tag_show/" + imageid + "/";
                    getTagProgress = new GetTagProgress(url, db);
                    getTagProgress.execute();
                } else {
                    db.tagdelete(tagnameorid);
                    tag.tagText.setText(null);
                    tag.changeState(Tag.addable);
                }
                refreshTags();
            } else {
                switch (errorType) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "标签修改失败", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "已经存在相同的标签", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "标签长度不能超过10字节\n并且标签不能有空格", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "标签不能为空", Toast.LENGTH_LONG).show();
                        break;
                }
                uploadTagProgress = null;
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

    class GetOrigin extends AsyncTask<Void, Void, Boolean> {
        private String targeturl;
        HashMap<String, String> returnmap;
        Context context;
        public GetOrigin(Context context) {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String url = "http://192.168.253.1/download_origin/" + imageid + "/";
                returnmap = new JsonGet(url, "origin").getReturnmap();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            getOrigin=null;
            if (success) {
                String baseurl = "http://192.168.253.1/media/";
                String originurl=returnmap.get("url");
                targeturl=baseurl+originurl;
                Origindownload origindownload=new Origindownload(context,targeturl,50,imageid);
                origindownload.execute();
            }
        }
    }
}