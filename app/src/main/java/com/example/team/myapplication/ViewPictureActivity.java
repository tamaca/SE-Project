package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
        comments = new ArrayList<>();//评论的ArrayList 数组，把获得的

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
    public void submitComment(View view){
        String comment = editText.getText().toString();


    }

    public void getComments(){
        //测试显示评论.
        comments.add(new Comment(getApplicationContext(),
                "sxy" + ": " , "评论在这里（5毛一条，括号里不要复制）"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy"+": ","23333"));
        comments.add(new Comment(getApplicationContext(), "sxy" + ": ", "23333"));
        for(int i = 0;i<comments.size();i++){
            commentView.addView(comments.get(i));
        }
    }
    class myThread implements Runnable{


        @Override
        public void run() {
            try{
                Thread.sleep(3000);
                comments.add(new Comment(getApplicationContext(), "sxy" + ": ", "23333"));
                commentView.postInvalidate();

            }
            catch (InterruptedException e){
                //
            }
        }
    }

}
