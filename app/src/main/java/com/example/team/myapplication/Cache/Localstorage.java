package com.example.team.myapplication.Cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import java.io.File;

/**
 * Created by coco on 2015/4/24.
 */
public class Localstorage {
    public final static String IMAGES_DIR_NAME="XXX"+File.separator;
    public final static String IMAGES_DIR_PATH= Environment.getExternalStorageDirectory()+ File.separator+IMAGES_DIR_NAME;
    /**
     * 判断SD卡是否存在
     */
    public static boolean isExistSDCard() {
        boolean isExist = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            isExist = true;
        }
        return isExist;
    }
    /**
     * 从SD卡中获取图片
     */
    public static Bitmap getBitmapFromSDCard(String filePath,int requestWidth){
        Bitmap bitmap=null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        BitmapFactory.decodeFile(filePath, options);
        options.inJustDecodeBounds=true;
        options.inSampleSize=calculateInSampleSize(options,requestWidth);
        options.inJustDecodeBounds=false;
        bitmap=BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    public static Bitmap fixBitmap(){
        return null;
    }

    /**
     * 计算图片的缩放比例
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int requestWidth){
        int inSampleSize=1;
        //SD卡中图片的宽
        int outWidth=options.outWidth;
        if (outWidth>requestWidth) {
            inSampleSize=Math.round((float) outWidth / (float) requestWidth);
        }
        return inSampleSize;
    }

    /**
     * 依据图片的Url获取其在SDCard的存储路径
     */
    public static String getImageFilePath(String imageUrl){
        File dir=new File(IMAGES_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String imageFilePath=null;
        String imageName=null;
        imageName=getImagesId(imageUrl);
        imageFilePath=IMAGES_DIR_PATH+File.separator+imageName+".jpg";
        return imageFilePath;
    }
    public static String getImagesId(String imageUrl)
    {
        int start=imageUrl.lastIndexOf("/");
        int end=imageUrl.lastIndexOf(".");
        String imageId=imageUrl.substring(start+1, end);
        return imageId;
    }

    /**
     * 从网络获取图片且保存至SD卡
     */
/*
    public static void getBitmapFromNetWorkAndSaveToSDCard(ImageDownloader imageDownloader,ImageView imageView,String imageUrl,String filePath){
        String _filePath=filePath;
        imageDownloader.download(imageUrl,_filePath,imageView);
    }
*/
}
