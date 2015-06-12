package com.example.team.myapplication;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyToast;
import com.example.team.myapplication.util.UploadPictureView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChoosePictureActivity extends GeneralActivity {

    private MyToast myToast;
    private ArrayList<UploadPictureView> pictures;
    private ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /**
         * 初始化变量
         */
        Button toGalleryButton = (Button) findViewById(R.id.button3);
        Button toCameraButton = (Button) findViewById(R.id.button4);
        Button okButton = (Button) findViewById(R.id.button11);
        myToast = new MyToast(this);
        pictures = new ArrayList<>();
        imagePaths = new ArrayList<>();
        pictures.add((UploadPictureView) findViewById(R.id.choose_picture_view1));
        pictures.add((UploadPictureView) findViewById(R.id.choose_picture_view2));
        pictures.add((UploadPictureView) findViewById(R.id.choose_picture_view3));
        pictures.add((UploadPictureView) findViewById(R.id.choose_picture_view4));
        /**
         * 给每个添加图片控件设置状态，第一个是显示背景的状态
         * 给每个控件添加监听器
         */
        for (int i = 0; i < pictures.size(); i++) {
            pictures.get(i).changeState(UploadPictureView.nothing);
            pictures.get(i).noPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChoosePictureActivity.this).
                                    setMessage("选择图片来源");
                            builder.setPositiveButton("相册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getImageFromAlbum(view);
                                }
                            });
                            builder.setNegativeButton("照相机", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dispatchTakePictureIntent(view);
                                }
                            });
                            builder.create().show();
                        }
                    });
                }
            });
        }
        pictures.get(0).changeState(UploadPictureView.only_background);
        toGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromAlbum(view);
            }
        });
        toCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 上传状态为 have_picture 的组件的picture图片
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_picture, menu);
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

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CODE_PICK_IMAGE = 2;

    /**
     * 获取相册中的一张图片
     *
     * @param view
     */
    protected void getImageFromAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    /**
     * 以下是获取拍摄的照片
     */
    String mCurrentPhotoPath;

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * 从相机中获取拍摄的照片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    for(int i = 0;i<pictures.size();i++){
                        if(pictures.get(i).currentState == UploadPictureView.only_background){
                            setPic(pictures.get(i).picture);
                            pictures.get(i).changeState(UploadPictureView.have_picture);
                            if(i+1<pictures.size()){
                                pictures.get(i+1).changeState(UploadPictureView.only_background);
                            }
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Picture is Taken", Toast.LENGTH_LONG).show();
                    break;
                case REQUEST_CODE_PICK_IMAGE:
                    ContentResolver contentResolver = getContentResolver();
                    Uri uri = data.getData();
                    mCurrentPhotoPath = uri.getPath();
                    if (uri != null) {
                        for(int i = 0;i<pictures.size();i++){
                            if(pictures.get(i).currentState == UploadPictureView.only_background){
                                setPic(pictures.get(i).picture);
                                pictures.get(i).changeState(UploadPictureView.have_picture);
                                if(i+1<pictures.size()){
                                    pictures.get(i+1).changeState(UploadPictureView.only_background);
                                }
                                break;
                            }
                        }

                    }

                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Picture is not Taken", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 把拍摄的图片写入储存卡 没有问题
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 把从路径读到的图片压缩 (可能是这里出了问题，读得了图片，但是不能获得真实图片的宽高)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPic(ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getMaxHeight();
        int targetH = imageView.getMaxWidth();
        /*
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inSampleSize=2;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, targetW);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        imageView.setImageBitmap(bitmap);


    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int requestWidth) {
        int inSampleSize = 1;
        //SD卡中图片的宽
        int outWidth = options.outWidth;
        if (outWidth > requestWidth) {
            inSampleSize = Math.round((float) outWidth / (float) requestWidth);
        }
        return inSampleSize;
    }
}
