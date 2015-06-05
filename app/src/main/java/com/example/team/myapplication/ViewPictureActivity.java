package com.example.team.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.ImageGet;
import com.example.team.myapplication.Network.NetworkState;
import com.example.team.myapplication.util.Comment;
import com.example.team.myapplication.util.GeneralActivity;

import java.util.ArrayList;
import java.util.List;


public class ViewPictureActivity extends GeneralActivity {
    ImageView imgview;
    private EditText editText;
    private LinearLayout commentView;
    private ProgressBar progressBar;
    private List<Comment> comments;
    private UploadComment uploadComment = null;
    private TextView author;
    private TextView uploadTime;
    private DB db = new DB(this);
    private int likeNumber;
    private ImageButton like;


    private TextView likeText;
    private boolean isLike = false;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    //
    public ImageView getImgview() {
        return imgview;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public LinearLayout getCommentView() {
        return commentView;
    }

    public TextView getAuthor() {
        return author;
    }

    public TextView getUploadTime() {
        return uploadTime;
    }

    public ImageButton getLike() {
        return like;
    }

    public TextView getLikeText() {
        return likeText;
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查看图片");
        Intent intent = getIntent();

        setContentView(R.layout.activity_view_picture);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //变量初始化
        imgview = (ImageView) findViewById(R.id.imageView8);
        editText = (EditText) findViewById(R.id.comment_text);
        commentView = (LinearLayout) findViewById(R.id.comment_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        author = (TextView) findViewById(R.id.author);
        like = (ImageButton) findViewById(R.id.like_button);
        uploadTime = (TextView) findViewById(R.id.upload_time);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout4);
        likeText = (TextView) findViewById(R.id.textView5);
        ////////

        comments = new ArrayList<>();
        author.setOnClickListener(new ToUserPageListener());
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLike(view);
            }
        });

        String bigurl = (String) intent.getExtras().get("bigurl");
        new ImageGet(imgview, bigurl, db, "big");
        // getImageInformation(imageid);
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

    public void addComment(String comment) {
        comments.add(new Comment(getApplicationContext(), LoginState.username, comment));
        commentView.addView(comments.get(comments.size() - 1));
        comments.get(comments.size() - 1).textView1.setOnClickListener(new ToUserPageListener());
        linearLayout.postInvalidate();
        Thread refresh = new Thread(new Refresh());
        refresh.start();
    }

    /*public void getImageInformation(String imageid) {
        String url = "http://192.168.137.1/php23/index.php";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("imageid", imageid);
        JsonPost post = new JsonPost(map, url, 4, db, this);
    }*/

    /*  public void getImageInformation() {

          String _author = "The Hammer";
          author.setText(_author);


          isLike = false;//测试用, false 代表没有赞过
          likeNumber = 999;//测试用
          like.setText(isLike ? "取消赞\n" : "赞\n" + "(" + likeNumber + ")");


          String _uploadTime = "2月1日";
          uploadTime.setText(_uploadTime);


          String commenter = "sxy";
          String comment = "评论在这里（5毛一条，括号里不要复制）";
          comments.add(new Comment(getApplicationContext(),
                  commenter, comment));

          for (int i = 0; i < comments.size(); i++) {
              commentView.addView(comments.get(i));
              comments.get(i).textView1.setOnClickListener(new ToUserPageListener());
          }
      }
  */
    public void changeLike(View view) {
        onLikeChange(isLike);
    }

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

    public void toUserPageActivity(View view) {
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("user_name", ((TextView) view).getText());
        //TODO 加入查看个人主页时传入的其他参数
        startActivity(intent);
    }

    public void toPictureActivity(View view) {
        if (NetworkState.isNetworkConnected(getApplicationContext())) {
            if (!NetworkState.isWifiEnable(getApplicationContext())) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewPictureActivity.this).setMessage("将会消耗流量下载，继续？");
                builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                        //TODO 在这里添加想传入查看原图页面的信息，比如图片主人的名字，图片ID 啥的。

                        startActivity(intent);*/
                    }
                });
                builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            } else {
                /*Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                //TODO 在这里添加想传入查看原图页面的信息，比如图片主人的名字，图片ID 啥的。

                startActivity(intent);*/
            }
        } else {
            /*Intent intent = new Intent(this,PictureActivity.class);
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            intent.putExtra("pic",bitmap);
            startActivity(intent);*/
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
                Toast.makeText(getApplicationContext(), "评论失败", Toast.LENGTH_SHORT).show();
                uploadComment = null;
            }
        }

    }

    public class ToUserPageListener implements TextView.OnClickListener {

        @Override
        public void onClick(View view) {
            toUserPageActivity(view);
        }
    }

}