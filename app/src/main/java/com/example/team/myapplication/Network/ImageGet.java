package com.example.team.myapplication.Network;

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
import org.apache.http4.client.methods.CloseableHttpResponse;
import org.apache.http4.client.methods.HttpGetHC4;
import org.apache.http4.impl.client.CloseableHttpClient;
import org.apache.http4.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Created by coco on 2015/4/25.
 */
//图片获取类
public class ImageGet {
    private LruCacheImageLoader mLruCacheImageLoader;
    private final WeakReference<ImageView> imageViewWeakReference;
    private HashSet<BitmapDownloaderTask> mLoadImageAsyncTaskHashSet;
    private ImageView imageView;
    private DownloadDrawable downloadDrawable;
    private String imageUrl;
    private String imageId;
    private DB db;

    public ImageGet(ImageView imageView, String imageUrl, DB db, String type) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        mLoadImageAsyncTaskHashSet = new HashSet<BitmapDownloaderTask>();
        this.imageUrl = imageUrl;
        this.db = db;
        imageId = Localstorage.getImagesId(imageUrl);
        mLruCacheImageLoader = LruCacheImageLoader.getLruCacheImageLoaderInstance();
        Load(imageUrl, type);
    }

    //三级缓存获取机制
    public void Load(String imageUrl, String type) {
        imageView = imageViewWeakReference.get();
        Bitmap bitmap = mLruCacheImageLoader.getBitmapFromLruCache(imageUrl);//从缓存获取图片
        if (bitmap == null) {
            String filePath = Localstorage.getImageFilePath(imageUrl, type);
            File imageFile = new File(filePath);
            //从手机存储目录获取图片
            if (type.equals("origin") || !imageFile.exists()) {
                //从服务器上下载图片
                if (cancelPotentialDownload(imageUrl, imageView)) {
                    BitmapDownloaderTask bitmapDownloaderTask = new BitmapDownloaderTask(imageView, filePath, type);
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

    //异步操作类    将缓存或内存中的图片显示
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
                //imageView.setContentDescription(imageId);
            }
        }

    }

    //异步操作类  从服务器上获取图片显示
    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url = imageUrl;
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<String> filepathWeakReference;
        private String type;

        public BitmapDownloaderTask(ImageView imageView, String filepath, String type) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
            filepathWeakReference = new WeakReference<String>(filepath);
            this.type = type;
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
                        // imageView.setContentDescription(imageId);
                        mLruCacheImageLoader.addBitmapToLruCache(imageUrl, bitmap);
                       /* long time = System.currentTimeMillis();
                        Timestamp tsTemp = new Timestamp(time);
                        db.imageinsert(imageId, "me", "500", tsTemp);*/
                        if (!type.equals("origin")) {
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

    //下载图片的静态类
    static Bitmap downloadBitmap(String url) {
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .useSystemProperties()
                .build();
        HttpGetHC4 httpget = new HttpGetHC4(url);
        try {
            CloseableHttpResponse resp = httpclient.execute(httpget);
            int Status = resp.getStatusLine().getStatusCode();
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
                    if (resp != null) {
                        resp.close();
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
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //需要记录下载的次序，保证最后一次启动请求的图片被有效地展现出来
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

    //当一个新的下载的时候，停止这个图片对应的所有可能的下载进程
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
