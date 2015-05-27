package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.AES;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.GeneralActivity;

import java.util.HashMap;


public class ViewPictureActivity extends GeneralActivity {
    ImageView imgview;
    private EditText editText;
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (EditText)findViewById(R.id.comment_text);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComment();
            }
        });

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
    }
}
