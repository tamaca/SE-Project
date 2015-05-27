package com.example.team.myapplication.Network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http4.client.methods.CloseableHttpResponse;
import org.apache.http4.impl.client.CloseableHttpClient;
import org.apache.http4.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by coco on 2015/5/8.
 */
public class JsonPost {
    private String url;
    private int type;
    boolean autoLogin;
    boolean rememPassword;
    private DB db;
    private LoginActivity loginActivity;

    //Login
    public JsonPost(HashMap<String, String> map, String url, int type, boolean autoLogin, boolean rememPassword, DB db) {
        this.url = url;
        this.type = type;
        this.autoLogin = autoLogin;
        this.rememPassword = rememPassword;
        this.db = db;
        this.loginActivity = loginActivity;
        try {
            Post post = new Post();
            post.execute(map);
        } catch (Exception e) {
            e.toString();
        }

    }

    //Register
    public JsonPost(HashMap<String, String> map, String url, int type,DB db) {
        this.url = url;
        this.type = type;
        this.db=db;
        try {
            Post post = new Post();
            post.execute(map);
        } catch (Exception e) {
            e.toString();
        }
    }

    private void dbsave(String id, String password) {
        if (rememPassword) {
            boolean usercheck = db.checkuser(id);
            if (!usercheck) {
                db.userinsert(id, password);
            } else {
                db.userupdatepassword(id, password);
            }
            if (autoLogin) {
                if (!usercheck) {
                    db.lastuserinsert(id, password);
                } else {
                    db.lastuserupdateid(id, password);
                }
                ;
            }
        } else {
            if (!db.checkuser(id)) {
                db.userinsert(id);
            } else {
                db.userupdatepassword(id, null);
            }
        }
        if (!autoLogin) {
            db.lastuserdelete();
        }
    }

    private class Post extends AsyncTask<HashMap<String, String>, Void, JSONObject> {
        //  HttpClient client = new DefaultHttpClient();
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        //HttpPost httpPost = new HttpPost(url);
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = new JSONObject();
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        HashMap<String, String> map;

        @Override
        protected JSONObject doInBackground(HashMap<String, String>... params) {
            map = params[0];
            Iterator iter = map.entrySet().iterator();
            try {
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    jsonObject.put(key, val);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePair.add(new BasicNameValuePair("jsonString", jsonObject
                    .toString()));
            CloseableHttpResponse response = null;
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                try {
                    response = client.execute(httpPost);
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
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            switch (type) {
                //login
                case 1: {
                    try {
                        String id = jsonObject.getString("user_id");
                        String password = jsonObject.getString("user_password");
                        Log.v("id", "id=" + id);
                        Log.v("afterpassword", "password" + password);
                        dbsave(this.jsonObject.getString("email"), this.jsonObject.getString("password"));
                        //这里写跳转代码
                        //loginActivity.showProgress(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //register
                case 2: {
                    try {
                        String id = jsonObject.getString("user_id");
                        String password = jsonObject.getString("user_password");
                        String name = jsonObject.getString("user_name");
                        Log.v("id", "id=" + id);
                        Log.v("password", "password" + password);
                        Log.v("name", "name" + name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //comment
                case 3: {
                    try {
                        String username = jsonObject.getString("username");
                        String content= jsonObject.getString("content");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    return;
            }
        }
    }
}
