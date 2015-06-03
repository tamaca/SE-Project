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

    public JsonGet(String url, DB db, View view) {
        this.url = url;
        this.db = db;
        this.view = view;
        try {
            Get get = new Get();
            get.execute();
        } catch (Exception e) {
            e.toString();
        }
    }

    private class Get extends AsyncTask<Void, Void, JSONObject> {
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        HttpGetHC4 httpget = new HttpGetHC4(url);

        @Override
        protected JSONObject doInBackground(Void... params) {
            CloseableHttpResponse response = null;
            httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");
            try {
                response = client.execute(httpget);
                int a = response.getStatusLine().getStatusCode();
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    builder.append(s);
                }
                try {
                    JSONObject jsonObject1 = new JSONObject(builder.toString());
                    return jsonObject1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String url = "http://192.168.253.1/media/";
                    ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1);
                    ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
                    ImageView imageView3 = (ImageView) view.findViewById(R.id.imageView3);
                    ImageView imageView4 = (ImageView) view.findViewById(R.id.imageView4);
                    String image1_small = url + jsonObject.getString("image0_small");
                    String image2_small = url + jsonObject.getString("image1_small");
                    String image3_small = url + jsonObject.getString("image2_small");
                    String image4_small = url + jsonObject.getString("image3_small");
                    String image1_big = url + jsonObject.getString("image0_big");
                    String image2_big = url + jsonObject.getString("image1_big");
                    String image3_big = url + jsonObject.getString("image2_big");
                    String image4_big = url + jsonObject.getString("image3_big");
                    ImageGet imageGet1 = new ImageGet(imageView1, image1_small, db,"small");
                    ImageGet imageGet2 = new ImageGet(imageView2, image2_small, db,"small");
                    ImageGet imageGet3 = new ImageGet(imageView3, image3_small, db,"small");
                    ImageGet imageGet4 = new ImageGet(imageView4, image4_small, db,"small");
                    imageView1.setContentDescription(image1_big);
                    imageView2.setContentDescription(image2_big);
                    imageView3.setContentDescription(image3_big);
                    imageView4.setContentDescription(image4_big);
                    //TODO 此处需要加入本地数据库
                }
                //这里写跳转代码
                //loginActivity.showProgress(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
