package com.example.team.myapplication.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.LoginActivity;
import com.example.team.myapplication.R;
import com.example.team.myapplication.ViewPictureActivity;
import com.example.team.myapplication.util.Comment;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http4.client.entity.UrlEncodedFormEntityHC4;
import org.apache.http4.client.methods.CloseableHttpResponse;
import org.apache.http4.client.methods.HttpPostHC4;
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
    private ViewPictureActivity view;

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
    public JsonPost(HashMap<String, String> map, String url, int type, DB db) {
        this.url = url;
        this.type = type;
        this.db = db;
        try {
            Post post = new Post();
            post.execute(map);
        } catch (Exception e) {
            e.toString();
        }
    }

    //图片信息获取
    public JsonPost(HashMap<String, String> map, String url, int type, DB db, ViewPictureActivity view) {
        this.url = url;
        this.type = type;
        this.db = db;
        this.view = view;
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

    public void getImageInformation(JSONObject info) {
        try {
            String originImageurl = info.getString("origin");
            String _author = info.getString("author");
            String _like = info.getString("like");
            String _isLike = info.getString("islike");
            String _updateTime = info.getString("updatetime");
            String _comment = info.getString("comment");
            JSONObject commentJson = new JSONObject(_comment);
            String commenter1 = commentJson.getString("name1");
            String comment1 = commentJson.getString("comment1");
            String commenter2 = commentJson.getString("name2");
            String comment2 = commentJson.getString("comment2");
            //原图位置
            view.getImgview().setContentDescription(originImageurl);
            //TODO 获取上传者
            //String _author = "The Hammer";
            view.getAuthor().setText(_author);
            //TODO 获取赞的数量和该用户是否已经赞
            Boolean isLike = (_isLike.equals("true"));//测试用, false 代表没有赞过
            view.getLike().setText(isLike ? "取消赞\n" : "赞\n" + "(" + _like + ")");
            //TODO 获取该图片的上传时间
            view.getUploadTime().setText(_updateTime);
            //TODO 获取评论
            //String commenter1 = "sxy";
            //String comment1 = "评论在这里（5毛一条，括号里不要复制）";
            view.getComments().add(new Comment(view.getApplicationContext(), commenter1, comment1));
            view.getComments().add(new Comment(view.getApplicationContext(), commenter2, comment2));
            for (int i = 0; i < view.getComments().size(); i++) {
                view.getCommentView().addView(view.getComments().get(i));
                view.getComments().get(i).textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.toUserPageActivity(v);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Post extends AsyncTask<HashMap<String, String>, Void, JSONObject> {
        //  HttpClient client = new DefaultHttpClient();
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        //HttpPost httpPost = new HttpPost(url);
        HttpPostHC4 httpPost = new HttpPostHC4(url);
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
                //  httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                //  httpPost.setHeader("Accept", "application/json");
                //  httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(new UrlEncodedFormEntityHC4(nameValuePair, "UTF-8"));
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
                    client.close();
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
                        String username = jsonObject.getString("user_name");
                        String content = jsonObject.getString("user_content");
                        Log.v("content", "content" + content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //获取图片信息
                case 4: {
                    //直接获取原图的URL
                    //图片的评论以JSON格式收取
                    getImageInformation(jsonObject);
                    break;
                }
                default:
                    return;
            }
        }
    }
}
