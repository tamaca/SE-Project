package com.example.team.myapplication.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Gravity;
import android.view.WindowManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by coco on 2015/6/14.
 */
public class Origindownload extends AsyncTask<String, Integer, Bitmap> {
    /**
     * 服务器路径*
     */
    private String url;
    private ProgressDialog progressDialog;
    private int y;
    private Context context;
    private String imageid;
    public Origindownload(Context context, String url, int y,String imageid) {
        this.url = url;
        this.context = context;
        this.y = y;
        this.imageid=imageid;
    }

    @Override
    protected void onPreExecute() {//执行前的初始化

        // TODO Auto-generated method stub

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        progressDialog.show();
        params.y = y;
        progressDialog.getWindow().setAttributes(params);
        progressDialog.setCanceledOnTouchOutside(false);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {//执行任务
        // TODO Auto-generated method stub
        CloseableHttpClient httpclient = HttpClientBuilder.create()
                .useSystemProperties()
                .build();
        HttpGetHC4 httpget = new HttpGetHC4(url);
        byte[] image = new byte[]{};
        try {
            CloseableHttpResponse resp = httpclient.execute(httpget);
            int Status = resp.getStatusLine().getStatusCode();
            final HttpEntity entity = resp.getEntity();
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (entity != null) {
                long file_length = entity.getContentLength();
                long total_length = 0;
                int length = 0;
                byte[] data = new byte[1024];
                inputStream = entity.getContent();
                while (-1 != (length = inputStream.read(data))) {
                    total_length += length;
                    byteArrayOutputStream.write(data, 0, length);
                    int progress = ((int) (total_length / (float) file_length) * 100);
                    publishProgress(progress);
                }
            }
            image = byteArrayOutputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            inputStream.close();
            byteArrayOutputStream.close();
            return bitmap;
        } catch (Exception e) {
            httpget.abort();
            //TODO:下载错误
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null) {
            // TODO Auto-generated method stub
            try {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Pictures" + File.separator + imageid+".jpg";
                File imageFile = new File(filePath);
                if (!imageFile.getParentFile().exists()) {
                    imageFile.getParentFile().mkdirs();
                }
                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                //TODO:图片下载完成
                // imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                //TODO：图片获取错误
                e.printStackTrace();
            }
        }
        else
        {
            //TODO：图片获取错误
        }
        progressDialog.dismiss();

    }
}
