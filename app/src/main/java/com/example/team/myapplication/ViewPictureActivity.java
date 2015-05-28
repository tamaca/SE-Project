package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

<<<<<<< HEAD
import com.example.team.myapplication.util.Comment;
import com.example.team.myapplication.util.GeneralActivity;

import java.util.ArrayList;
import java.util.List;
=======
import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.AES;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.GeneralActivity;

import java.util.HashMap;
>>>>>>> origin/develop


public class ViewPictureActivity extends GeneralActivity {
    ImageView imgview;
    private EditText editText;
<<<<<<< HEAD
    private LinearLayout commentView;
    private ProgressBar progressBar;
    private List<Comment> comments;
    private View scrollView;
=======
    private DB db = new DB(this);
>>>>>>> origin/develop
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查看图片");
        Intent intent = getIntent();
        setContentView(R.layout.activity_view_picture);
        imgview = (ImageView)findViewById(R.id.imageView8);
        Bitmap bitmap = (Bitmap)intent.getExtras().get("pic");
        imgview.setImageBitmap(bitmap);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (EditText)findViewById(R.id.comment_text);
        commentView = (LinearLayout)findViewById(R.id.comment_view);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        comments = new ArrayList<>();//评论的ArrayList 数组，把获得的评论放在这里
        scrollView = findViewById(R.id.scrollView);
        getComments();




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

    //在此上传评论
<<<<<<< HEAD
    public void submitComment(View view){
        String comment = editText.getText().toString();//获取输入的评论
        if(comment.isEmpty()){

            return;
        }
        addComment(comment);//在用户界面能看到评论更新
        editText.setText(null);
=======
    public void submitComment(){
        String comment = editText.getText().toString();
        String key = "1234567891234567";
        String username= LoginState.username;
        AES aesEncrypt = new AES(key);
        String encryptUsername=aesEncrypt.encrypt(username);
        String url = "http://192.168.137.1/php2/index.php";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", encryptUsername);
        map.put("content", comment);
        JsonPost post = new JsonPost(map, url,3,db);
>>>>>>> origin/develop
    }

    public void addComment(String comment){
        comments.add(new Comment(getApplicationContext(), LoginState.username + ": ", comment));
        commentView.addView(comments.get(comments.size()-1));
        Thread refresh = new Thread(new RefreshThread());
        refresh.start();
        //接下来把评论传到数据库

    }

    public void getComments(){
        //获取评论函数


        //测试显示评论.
        comments.add(new Comment(getApplicationContext(),
                "sxy" + ": " , "评论在这里（5毛一条，括号里不要复制）"));


        for(int i = 0;i<comments.size();i++){
            commentView.addView(comments.get(i));
        }
    }


    class RefreshThread implements Runnable {

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    Thread.sleep(100);

                }

                catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                }
                // 使用postInvalidate可以直接在线程中更新界面

                scrollView.postInvalidate();
                Log.d("Comment------------>","Refreshing!!");
                Thread.currentThread().interrupt();

            }

        }

    }




}
