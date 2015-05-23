package com.example.team.myapplication.Network;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Cache.LruCacheImageLoader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Created by coco on 2015/4/25.
 */
public class ImageGet {
    ImageDownloader imageDownloader = new ImageDownloader();
    private LruCacheImageLoader mLruCacheImageLoader;
    private final WeakReference<ImageView> imageViewWeakReference;
    private HashSet<LoadImageAsyncTask> mLoadImageAsyncTaskHashSet;

    public ImageGet(ImageView imageView,String imageUrl) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        mLoadImageAsyncTaskHashSet=new HashSet<LoadImageAsyncTask>();
        mLruCacheImageLoader= LruCacheImageLoader.getLruCacheImageLoaderInstance();
        LoadImageAsyncTask loadImageAsyncTask=new LoadImageAsyncTask();
        loadImageAsyncTask.execute(imageUrl);
    }
    private class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;
        private Bitmap bitmap;
        ImageView imageView;
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            imageView = imageViewWeakReference.get();
            bitmap = mLruCacheImageLoader.getBitmapFromLruCache(imageUrl);
            if (bitmap == null) {
                String filePath = Localstorage.getImageFilePath(imageUrl);
                File imageFile = new File(filePath);
                if (!imageFile.exists()) {
                    Localstorage.getBitmapFromNetWorkAndSaveToSDCard(imageDownloader, imageView, imageUrl, filePath);
                }
                if (filePath != null) {
                    bitmap = Localstorage.getBitmapFromSDCard(filePath, imageView.getWidth());
                    if (bitmap != null) {
                        mLruCacheImageLoader.addBitmapToLruCache(imageUrl, bitmap);
                    }
                }
            } else {
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mLoadImageAsyncTaskHashSet.remove(this);
            if (bitmap!=null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
