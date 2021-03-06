package com.example.team.myapplication.Network;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Cache.LruCacheImageLoader;
import com.example.team.myapplication.Database.DB;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.HashSet;

/**
 * Created by coco on 2015/4/25.
 */
public class ImageGet {
    private LruCacheImageLoader mLruCacheImageLoader;
    private final WeakReference<ImageView> imageViewWeakReference;
    private HashSet<BitmapDownloaderTask> mLoadImageAsyncTaskHashSet;
    private ImageView imageView;
    private DownloadDrawable downloadDrawable;
    private String imageUrl;
    private String imageId;
    private DB db;
    public ImageGet(ImageView imageView, String imageUrl,DB db) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        mLoadImageAsyncTaskHashSet = new HashSet<BitmapDownloaderTask>();
        this.imageUrl = imageUrl;
        this.db=db;
        imageId=Localstorage.getImagesId(imageUrl);
        mLruCacheImageLoader = LruCacheImageLoader.getLruCacheImageLoaderInstance();
        Load(imageUrl);
       /* LoadImageAsyncTask loadImageAsyncTask=new LoadImageAsyncTask();
        loadImageAsyncTask.execute(imageUrl);*/
    }

    public void Load(String imageUrl) {
        imageView = imageViewWeakReference.get();
        Bitmap bitmap = mLruCacheImageLoader.getBitmapFromLruCache(imageUrl);
        if (bitmap == null) {
            String filePath = Localstorage.getImageFilePath(imageUrl);
            File imageFile = new File(filePath);
            if (!imageFile.exists()) {
                if (cancelPotentialDownload(imageUrl, imageView)) {
                    BitmapDownloaderTask bitmapDownloaderTask = new BitmapDownloaderTask(imageView, filePath);
                    downloadDrawable = new DownloadDrawable(bitmapDownloaderTask);
                    bitmapDownloaderTask.execute(imageUrl);
                }
            } else if (filePath != null) {
                bitmap = Localstorage.getBitmapFromSDCard(filePath, imageView.getLayoutParams().width);
                if (bitmap != null) {
                    BitmapShowInCache bitmapShowInCache = new BitmapShowInCache();
                    bitmapShowInCache.execute(bitmap);
                    mLruCacheImageLoader.addBitmapToLruCache(imageUrl, bitmap);
                }
            }
        } else {
            BitmapShowInCache bitmapShowInCache = new BitmapShowInCache();
            bitmapShowInCache.execute(bitmap);
        }

    }

    private class BitmapShowInCache extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mLoadImageAsyncTaskHashSet.remove(this);
            if (bitmap != null) {
             /*   long time = System.currentTimeMillis();
                Timestamp tsTemp = new Timestamp(time);
                db.userinsert("me");
                db.imageinsert(imageId, "me", "500", tsTemp);
                Cursor mCursor=db.imageselect();
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    String id= mCursor.getString((mCursor.getColumnIndex("m_image_imageid")));
                    String userid = mCursor.getString((mCursor.getColumnIndex("m_image_userid")));
                    String likenumber = mCursor.getString((mCursor.getColumnIndex("m_image_likenumber")));
                    String updatedate = mCursor.getString((mCursor.getColumnIndex("m_image_updatedate")));
                    String name1 = mCursor.getString((mCursor.getColumnIndex("m_image_imageid")));
                }*/
                imageView.setImageBitmap(bitmap);
            }
        }

    }

    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url = imageUrl;
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<String> filepathWeakReference;

        public BitmapDownloaderTask(ImageView imageView, String filepath) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
            filepathWeakReference = new WeakReference<String>(filepath);
        }

        @Override
        protected void onPreExecute() {
            try {
                imageView.setImageDrawable(downloadDrawable);
            } catch (Exception e) {
                Log.v("error", e.toString());
                Log.v("error", e.toString());
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
                return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap == null) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                String filePath = filepathWeakReference.get();
                if (imageView != null) {
                    BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloader(imageView);
                    if (this == bitmapDownloaderTask) {
                        mLoadImageAsyncTaskHashSet.remove(this);
                        imageView.setImageBitmap(bitmap);
                        mLruCacheImageLoader.addBitmapToLruCache(imageUrl, bitmap);
                       /* long time = System.currentTimeMillis();
                        Timestamp tsTemp = new Timestamp(time);
                        db.imageinsert(imageId, "me", "500", tsTemp);*/
                        try {
                            File imageFile = new File(filePath);
                            if (!imageFile.getParentFile().exists()) {
                                imageFile.getParentFile().mkdirs();
                            }
                            if (!imageFile.exists()) {
                                imageFile.createNewFile();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            // imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //   progress.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
       /* @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mLoadImageAsyncTaskHashSet.remove(this);
            if (bitmap!=null) {
                imageView.setImageBitmap(bitmap);
            }
        }*/
    }

    static Bitmap downloadBitmap(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse resp = httpclient.execute(httpget);
            final int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error" + statusCode + "while retrieving bitmap from" + url);
                return null;
            }
            final HttpEntity entity = resp.getEntity();
            if (entity != null) {
                InputStream in = null;
                try {
                    in = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    return bitmap;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            httpget.abort();
            e.printStackTrace();
            Log.w("ImageDownloader", "Error while retrieving bitmap from" + url + e.toString());
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        return null;
    }

    static class DownloadDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskWeakReference;

        public DownloadDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskWeakReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskWeakReference.get();
        }
    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloader(imageView);
        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl != null) || (!bitmapDownloaderTask.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloader(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadDrawable) {
                DownloadDrawable downloadDrawable = (DownloadDrawable) drawable;
                return downloadDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }
}
