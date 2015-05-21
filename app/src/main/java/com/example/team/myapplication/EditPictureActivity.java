package com.example.team.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class EditPictureActivity extends ActionBarActivity {


    //TabHost tabHost;

   // public EditPictureActivity() {
   //     tabHost = this.getTabHost();
   // }


    //ImageView imageView = (ImageView)findViewById(R.id.imageView4);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_edit_picture);
        ImageView imgview = (ImageView)findViewById(R.id.edit_imageView);
        Bitmap bitmap = (Bitmap)intent.getExtras().get("picture");
        imgview.setImageBitmap(bitmap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_picture, menu);
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
            case R.id.action_settings:
                return true;


            case android.R.id.home:
                finish();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

    /*public void getPictureFromCamera(){
        Bitmap image = (Bitmap)this.getIntent().getExtras().get("Data");
        imageView.setImageBitmap(image);

    }*/
}
