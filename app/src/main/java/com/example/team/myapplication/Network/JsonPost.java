package com.example.team.myapplication.Network;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.LoginActivity;
import com.example.team.myapplication.ViewPictureActivity;
import com.example.team.myapplication.util.Comment;

import org.apache.http.NameValuePair;
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
import java.sql.Timestamp;
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

    //多种处理方式
    //登录
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

    //注册、修改密码
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

    //数据库储存用户
    private void dbsaveuser(String id, String password) {
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

    private void dbimagesave(HashMap<String, String> image) {
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        updatetime.valueOf(image.get("updatetime"));
        db.imageinsert(image.get("imageid"), image.get("userid"), image.get("islike"), image.get("likenumber"), updatetime);
    }

    private void dbcommentsave(HashMap<String, String> commentmap) {
        String _commentnum = commentmap.get("commentnum");
        int commentnum = Integer.parseInt(_commentnum);
        String imageid = commentmap.get("imageid");
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        for (int i = 1; i <= commentnum; i++) {
            updatetime.valueOf(commentmap.get("updatedate" + String.valueOf(i - 1)));
            db.commentinsert(commentmap.get("commentid" + String.valueOf(i - 1)), commentmap.get("userid" + String.valueOf(i - 1)), imageid, commentmap.get("commentid" + String.valueOf(i - 1)), updatetime);
        }
    }

    //UI处理图片信息
    public void getImageInformation(JSONObject info) {
        try {
            String imageId = info.getString("imageid");
            String originImageurl = info.getString("origin");
            String _author = info.getString("author");
            String _like = info.getString("like");
            String _isLike = info.getString("islike");
            String _updateTime = info.getString("updatetime");
            String _commentnum = info.getString("commentnum");
            String _comment = info.getString("comment");
            int commentnum = Integer.parseInt(_commentnum);
            JSONObject commentJson = new JSONObject(_comment);
            String commenter[] = new String[6];
            String comment[] = new String[6];
            String commentid[] = new String[6];
            String updatedate[] = new String[6];
            for (int i = 1; i <= commentnum; i++) {
                commenter[i - 1] = commentJson.getString("name" + String.valueOf(i));
                comment[i - 1] = commentJson.getString("comment" + String.valueOf(i));
                commentid[i - 1] = commentJson.getString("commentid" + String.valueOf(i));
                updatedate[i - 1] = commentJson.getString("updatedate" + String.valueOf(i));
            }
            //原图位置
            view.getImgview().setContentDescription(originImageurl);
            //String _author = "The Hammer";
            view.getAuthor().setText(_author);
            Boolean isLike = (_isLike.equals("true"));//测试用, false 代表没有赞过
            view.getLike().setText(isLike ? "取消赞\n" : "赞\n" + "(" + _like + ")");
            view.getCommentView().postInvalidate();
            view.getUploadTime().setText(_updateTime);
            //String commenter1 = "sxy";
            //String comment1 = "评论在这里（5毛一条，括号里不要复制）";
            for (int i = 1; i <= commentnum; i++) {
                view.getComments().add(new Comment(view.getApplicationContext(), commenter[i - 1], comment[i - 1]));
            }
            for (int i = 0; i < view.getComments().size(); i++) {
                view.getCommentView().addView(view.getComments().get(i));
                view.getComments().get(i).textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.toUserPageActivity(v);
                    }
                });
            }
            HashMap<String, String> image = new HashMap<String, String>();
            image.put("imageid", imageId);
            image.put("userid", _author);
            image.put("islike", _isLike);
            image.put("likenumber", _like);
            image.put("updatetime", _updateTime);
            dbimagesave(image);
            HashMap<String, String> commentmap = new HashMap<String, String>();
            commentmap.put("imageid", imageId);
            commentmap.put("commentnum", _commentnum);
            for (int i = 1; i <= commentnum; i++) {
                commentmap.put("commentid" + String.valueOf(i), commentid[i - 1]);
                commentmap.put("userid" + String.valueOf(i), commenter[i - 1]);
                commentmap.put("context" + String.valueOf(i), commentid[i - 1]);
                commentmap.put("updatedate" + String.valueOf(i), updatedate[i - 1]);
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
                  httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                  httpPost.setHeader("Accept", "application/json");
                  httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(new UrlEncodedFormEntityHC4(nameValuePair, "UTF-8"));
                try {
                    response = client.execute(httpPost);
                    int a=response.getStatusLine().getStatusCode();
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
                //多种处理方式
                //登录
                case 1: {
                    try {
                        String status = jsonObject.getString("status");
                        if (status == "normal") {
                            String _username = jsonObject.getString("username");
                            dbsaveuser(_username, this.jsonObject.getString("password"));
                        }
                        //这里写跳转代码
                        //loginActivity.showProgress(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //注册
                case 2: {
                    try {
                        String status = jsonObject.getString("status");
                        Log.v("status", "status=" + status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //提交评论
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
                //修改密码
                case 5: {
                    try {
                        String status = jsonObject.getString("status");
                      //  String email=jsonObject.getString("email");
                       // String oldpassword=jsonObject.getString("oldpassword");
                        //String newpassword=jsonObject.getString("newpassword");
                        /*if(status=="normal")
                        {
                          if(db.checklastuserpassword())//有可能出错
                          {
                              db.lastuserupdatepassword(map.get("password"));
                          }
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                default:
                    return;
            }
        }
    }
}
