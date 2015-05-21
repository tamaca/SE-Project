package com.example.team.myapplication.Network;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by coco on 2015/5/8.
 */
public class JsonPost {
    private String url;

    public JsonPost(String url) {
        this.url = url;
    }

    public JSONObject Post(String name[], String data[], int number) {
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = new JSONObject();
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        try {
            for (int i = 1; i <= number; i++) {
                jsonObject.put(name[i - 1], data[i - 1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nameValuePair.add(new BasicNameValuePair("jsonString", jsonObject
                .toString()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            try {
                HttpResponse response = client.execute(httpPost);
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
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
