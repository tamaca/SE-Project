package com.example.team.myapplication.Network;

import android.app.ProgressDialog;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by coco on 2015/6/13.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;

public class ImagePost extends AsyncTask<String, Integer, String> {
    /**
     * 服务器路径*
     */
    private String url;
    /**
     * 上传的参数*
     */
    private Map<String, String> paramMap;
    /**
     * 要上传的文件*
     */
    private File file;
    private long totalSize;
    private Context context;
    private ProgressDialog progressDialog;
    private int y;

    public ImagePost(Context context, String url, Map<String, String> paramMap, File file, int y) {
        this.context = context;
        this.url = url;
        this.paramMap = paramMap;
        this.file = file;
        this.y = y;
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
    protected String doInBackground(String... params) {//执行任务
        // TODO Auto-generated method stub
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName(HTTP.UTF_8));//设置请求的编码格式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式
        int count = 0;
        builder.addBinaryBody("file" + count, file, ContentType.DEFAULT_BINARY, String.valueOf(count) + "." + paramMap.get("fileTypes"));
        builder.addTextBody("fileTypes", paramMap.get("fileTypes"));//设置请求参数
        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
        totalSize = entity.getContentLength();//获取上传文件的大小
        ProgressOutHttpEntity progressHttpEntity = new ProgressOutHttpEntity(
                entity, new ProgressOutHttpEntity.ProgressListener() {
            @Override
            public void transferred(long transferedBytes) {
                publishProgress((int) (100 * transferedBytes / totalSize));//更新进度
            }
        });
        return uploadFile(url, progressHttpEntity);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {//执行进度
        // TODO Auto-generated method stub
        Log.i("info", "values:" + values[0]);
        progressDialog.setProgress((int) values[0]);//更新进度条
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {//执行结果
        // TODO Auto-generated method stub
        Log.i("info", result);
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        super.onPostExecute(result);
    }

    /**
     * 向服务器上传文件
     *
     * @param url
     * @param entity
     * @return
     */
    public String uploadFile(String url, ProgressOutHttpEntity entity) {
        HttpClient httpClient = new DefaultHttpClient();// 开启一个客户端 HTTP 请求
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 设置连接超时时间
        HttpPost httpPost = new HttpPost(url);//创建 HTTP POST 请求
        httpPost.setEntity(entity);
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            String status = jsonObject.getString("status");
            if (status.equals("normal")) {
                return "图片上传成功";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return "图片上传失败";
    }
}