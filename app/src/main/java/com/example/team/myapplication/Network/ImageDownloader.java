package com.example.team.myapplication.Network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

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

public class ImageDownloader {
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

    public void download(String url, String filepath, ImageView imageview) {
        if (cancelPotentialDownload(url, imageview)) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageview, filepath);
            DownloadDrawable downloadDrawable = new DownloadDrawable(task);
            imageview.setImageDrawable(downloadDrawable);
            task.execute(url);
        }
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<String> filepathWeakReference;

        public BitmapDownloaderTask(ImageView imageView, String filepath) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
            filepathWeakReference = new WeakReference<String>(filepath);
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
            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                String filePath = filepathWeakReference.get();
                if (imageView != null) {
                    BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloader(imageView);
                    if (this == bitmapDownloaderTask) {
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
