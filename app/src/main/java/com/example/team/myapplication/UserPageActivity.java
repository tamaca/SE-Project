package com.example.team.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.team.myapplication.util.GeneralActivity;

public class UserPageActivity extends GeneralActivity {
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_page);
        Intent intent = getIntent();
        String userName = intent.getExtras().get("user_name").toString();
        name = (TextView)findViewById(R.id.name);
        name.setText(userName);
        setTitle(userName+"的个人主页");
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
