package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.team.myapplication.Database.DB;
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
    private View scrollView;

    private DB db = new DB(this);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("查看图片");
        Intent intent = getIntent();
        setContentView(R.layout.activity_view_picture);
        imgview = (ImageView)findViewById(R.id.imageView8);
        Bitmap bitmap = (Bitmap)intent.getExtras().get("pic");
        imgview.setImageBitmap(bitmap);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (EditText)findViewById(R.id.comment_text);
        commentView = (LinearLayout)findViewById(R.id.comment_view);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        comments = new ArrayList<>();//评论的ArrayList 数组，把获得的评论放在这里

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
    public void submitComment(View view) {
        String comment = editText.getText().toString();//获取输入的评论
        if (comment.isEmpty()) {
            return;
        }
        else if(comment.charAt(0)==' '){
            editText.setError("首字为空格");
            return;
        }
        else if(comment.length()>120){
            editText.setError("评论不能超过120字");
            return;
        }
        if(uploadComment==null) {
            showProgress(true);
            uploadComment = new UploadComment(comment);
            uploadComment.execute((Void) null);
        }
        else{
            Toast.makeText(getApplicationContext(),"5秒内只能上传一次评论",Toast.LENGTH_SHORT).show();
        }


    }

    public void addComment(String comment){
        comments.add(new Comment(getApplicationContext(), LoginState.username , comment));
        commentView.addView(comments.get(comments.size() - 1));
        Thread refresh = new Thread(new Refresh());
        refresh.start();

    }

    public void getComments(){
        //测试显示评论.
        //获取评论
        comments.add(new Comment(getApplicationContext(),
                "sxy", "评论在这里（5毛一条，括号里不要复制）"));

        for(int i = 0;i<comments.size();i++){
            commentView.addView(comments.get(i));
        }
    }

    public void showProgress(boolean show){

        progressBar.setVisibility(show?View.VISIBLE:View.GONE);
    }

    class Refresh implements Runnable{
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                commentView.postInvalidate();
                Log.d("comment--->>", "refreshing");
                Thread.currentThread().interrupt();
            }
        }
    }

    class UploadComment extends AsyncTask<Void,Void,Boolean>{
        private String comment;
        public UploadComment(String comment){
            this.comment = comment;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
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
            }
            catch (Exception e){
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success){

            showProgress(false);
            if(success){
                Toast.makeText(getApplicationContext(),"评论成功",Toast.LENGTH_SHORT).show();
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

            }
            else{
                Toast.makeText(getApplicationContext(),"评论失败",Toast.LENGTH_SHORT).show();
                uploadComment = null;
            }
        }

    }


}
