package com.example.team.myapplication.Network;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.R;
import com.example.team.myapplication.ViewPictureActivity;

import org.apache.http4.client.methods.CloseableHttpResponse;
import org.apache.http4.client.methods.HttpGetHC4;
import org.apache.http4.impl.client.CloseableHttpClient;
import org.apache.http4.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by coco on 2015/6/3.
 */
public class JsonGet {
    private String url;
    DB db;
    private View view;

    public JsonGet(String url, DB db, View view)throws Exception{
        this.url = url;
        this.db = db;
        this.view = view;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecute(jsonObject);
    }

    public JsonGet(String url, DB db) throws Exception{
        this.url = url;
        this.db = db;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecute(jsonObject);
    }

    private class Get {
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        HttpGetHC4 httpget = new HttpGetHC4(url);

        protected JSONObject GetFromServer() throws Exception {
            CloseableHttpResponse response = null;
            httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");
            response = client.execute(httpget);
            int a = response.getStatusLine().getStatusCode();
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            JSONObject jsonObject1 = new JSONObject(builder.toString());
            if (response != null) {
                response.close();
            }
            client.close();
            return jsonObject1;
        }

        protected void PostExecute(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String url = "http://192.168.253.1/media/";
                    String image_small[] = new String[4];
                    String image_big[] = new String[4];
                    for (int i = 0; i <= 3; i++) {
                        image_small[i] = url + jsonObject.getString("image" + i + "_small");
                        image_big[i] = url + jsonObject.getString("image" + i + "_big");
                    }
                    if (view != null) {
                        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1);
                        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
                        ImageView imageView3 = (ImageView) view.findViewById(R.id.imageView3);
                        ImageView imageView4 = (ImageView) view.findViewById(R.id.imageView4);
                        ImageGet imageGet1 = new ImageGet(imageView1, image_small[0], db, "small");
                        ImageGet imageGet2 = new ImageGet(imageView2, image_small[1], db, "small");
                        ImageGet imageGet3 = new ImageGet(imageView3, image_small[2], db, "small");
                        ImageGet imageGet4 = new ImageGet(imageView4, image_small[3], db, "small");
                        imageView1.setContentDescription(image_big[0]);
                        imageView2.setContentDescription(image_big[1]);
                        imageView3.setContentDescription(image_big[2]);
                        imageView4.setContentDescription(image_big[3]);
                    } else {
                        ImageGet imageGet6 = new ImageGet(null, image_small[0], db, "small");
                        ImageGet imageGet7 = new ImageGet(null, image_small[1], db, "small");
                        ImageGet imageGet8 = new ImageGet(null, image_small[2], db, "small");
                        ImageGet imageGet9 = new ImageGet(null, image_small[3], db, "small");
                    }
                    //TODO 此处需要加入本地数据库
                } else {
                    throw new Exception();
                    //TODO:接收信息错误
                }
                //这里写跳转代码
                //loginActivity.showProgress(false);
            } else {
                //TODO:接收信息错误
                throw new Exception();
            }
        }
    }
}
