package com.example.team.myapplication.Network;

import android.view.View;
import android.widget.ImageView;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.LoginState;
import com.example.team.myapplication.util.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by coco on 2015/6/3.
 */
public class JsonGet {
    private String url;
    private DB db;
    private GalleryItem[] galleryItems;

    //关注或黑名单中的人
    public ArrayList<String> getUserNames() {
        return userNames;
    }

    private ArrayList<String> userNames;

    public HashMap<String, String> getReturnmap() {
        return returnmap;
    }

    private HashMap<String, String> returnmap;


    //图片获取(url)
    public JsonGet(String url, DB db, GalleryItem[] galleryItems, String type) throws Exception {
        this.url = url;
        this.db = db;
        this.galleryItems = galleryItems;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecuteImageUrl(jsonObject, type);
    }

    //图片获取(id)
    public JsonGet(String url, String key) throws Exception {
        this.url = url;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecuteId(jsonObject, key);
    }

    //关注的人获取 或 黑名单获取
    public JsonGet(String url) throws Exception {
        this.url = url;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        userNames = get.PostExecuteList(jsonObject);
    }

    //赞或取消赞
    public JsonGet(String url, DB db) throws Exception {
        this.url = url;
        this.db = db;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecuteLike(jsonObject);
    }

    //获得标签
    public JsonGet(String url, DB db, String type) throws Exception {
        this.url = url;
        this.db = db;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        if (type.equals("gettag")) {
            get.PostExecuteTag(jsonObject);
        } else if (type.equals("getcomment")) {
            get.PostExecuteComment(jsonObject);
        }
    }

    private class Get {
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        HttpGetHC4 httpget = new HttpGetHC4(url);

        protected JSONObject GetFromServer() throws Exception {
            try {
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
            } catch (Exception e) {
                throw new MyException.getException();
            }
        }

        //接收图片(url) 大厅
        protected void PostExecuteImageUrl(JSONObject jsonObject, String type) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                String baseurl = "http://192.168.253.1/media/";
                String image_small[] = new String[8];
                String image_big[] = new String[8];
                String image_id[] = new String[8];
                String image_time[] = new String[8];
                ImageView imageView[] = new ImageView[8];
                int count;
                if (status.equals("normal")) {
                    count = 8;
                } else if (status.equals("no_more_image")) {
                    String _count = jsonObject.getString("count");
                    count = Integer.valueOf(_count);
                    if (count == 0) {
                        throw new MyException.zeroException();
                    }
                } else {
                    throw new MyException.executeException();
                    //TODO:接收信息错误
                }
                for (int i = 0; i < count; i++) {
                    image_small[i] = baseurl + jsonObject.getString("image" + i + "_small");
                    image_big[i] = baseurl + jsonObject.getString("image" + i + "_big");
                    image_id[i] = jsonObject.getString("image" + i + "_id");
                }
                if (galleryItems != null) {
                    for (int i = 0; i < count; i++) {
                        imageView[i] = galleryItems[i].imageView;
                    }
                    for (int i = 0; i < count; i++) {
                        new ImageGet(imageView[i], image_small[i], image_id[i], db, "small");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("type", "online");
                        jsonObject1.put("imageid", image_id[i]);
                        jsonObject1.put("imagebigurl", image_big[i]);
                        imageView[i].setContentDescription(jsonObject1.toString());
                    }
                } else {
                    for (int i = 0; i < count; i++) {
                        new ImageGet(null, image_small[i], image_id[i], db, "small");
                    }
                }
                for (int i = 0; i < count; i++) {
                    dbimagesave(image_id[i]);
                }
                if (type.equals("lobby")) {
                    for (int i = 0; i < count; i++) {
                        dblobbyimagesave(String.valueOf(LoginState.getPage() * 8 + i + 1), image_id[i]);
                    }
                } else if (galleryItems != null) {
                    for (int i = 0; i < count; i++) {
                        image_time[i] = jsonObject.getString("image" + i + "_time");
                        galleryItems[i].textView.setText(image_time[i]);
                        galleryItems[i].textView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }


        //黑名单 和 关注的人
        protected ArrayList<String> PostExecuteList(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String _num = jsonObject.getString("now");
                    int num = Integer.parseInt(_num);
                    ArrayList<String> userNames = new ArrayList<String>();
                    for (int i = 0; i < num; i++) {
                        String _username = jsonObject.getString("target" + i);
                        userNames.add(_username);
                    }
                    return userNames;
                } else {
                    throw new MyException.executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }

        //赞或取消赞
        protected void PostExecuteLike(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    returnmap = new HashMap<String, String>();
                    // HashMap<String, String> returnmap = new HashMap<String, String>();
                    String islike = jsonObject.getString("is_like");
                    String likenumber = jsonObject.getString("like_number");
                    String imageid = jsonObject.getString("image_id");
                    dbimagelikesave(imageid, islike, likenumber);
                    returnmap.put("islike", islike);
                    returnmap.put("likenumber", likenumber);
                    //return returnmap;
                } else {
                    throw new MyException.executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }

        protected void PostExecuteTag(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                int tagnum;
                String status = jsonObject.getString("status");
                returnmap = new HashMap<String, String>();
                if (status.equals("normal")) {
                    tagnum = 5;
                } else {
                    String _tagnum = jsonObject.getString("count");
                    returnmap.put("tagnum", String.valueOf(_tagnum));
                    tagnum = Integer.parseInt(_tagnum);
                }
                returnmap.put("imageid", jsonObject.getString("image_id"));
                for (int i = 0; i < tagnum; i++) {
                    returnmap.put("tagname" + i, jsonObject.getString("tag" + String.valueOf(i)));
                    returnmap.put("tagid" + i, jsonObject.getString("tag" + String.valueOf(i) + "_id"));
                }
                dbtagsave(returnmap);
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }

        protected void PostExecuteComment(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                int commentnum;
                String status = jsonObject.getString("status");
                returnmap = new HashMap<String, String>();
                if (status.equals("normal")) {
                    commentnum = 8;
                    returnmap.put("commentnum", "8");
                } else {
                    String _commentnum = jsonObject.getString("now");
                    returnmap.put("commentnum", String.valueOf(_commentnum));
                    commentnum = Integer.parseInt(_commentnum);
                    if(commentnum==0)
                    {
                        throw new MyException.zeroException();
                    }
                }
                returnmap.put("imageid", jsonObject.getString("image_id"));
                for (int i = 0; i < commentnum; i++) {
                    returnmap.put("comment" + i, jsonObject.getString("comment" + String.valueOf(i) + "_text"));
                    returnmap.put("commentid" + i, jsonObject.getString("comment" + String.valueOf(i) + "_id"));
                    returnmap.put("commentdate" + i, jsonObject.getString("comment" + String.valueOf(i) + "_date"));
                    returnmap.put("commentuser" + i, jsonObject.getString("comment" + String.valueOf(i) + "_username"));
                }
                dbcommentsave(returnmap);
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }

        protected void PostExecuteId(JSONObject jsonObject, String key) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    returnmap = new HashMap<String, String>();
                    returnmap.put(key, jsonObject.getString(key));
                    //return returnmap;
                } else {
                    throw new MyException.executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new MyException.nullException();
            }
        }

    }

    //数据库
    //缩略图数据库保存
    private void dbimagesave(String imageid) {
        db.imageinsert(imageid);
    }

    //大厅缩略图保存
    private void dblobbyimagesave(String rank, String imageid) {
        db.lobbyimageinsert(rank, imageid);
    }

    private void dbimagelikesave(String imageid, String islike, String likenumber) {
        db.imageupdateislike(imageid, islike);
        db.imageupdatelikenumber(imageid, likenumber);
    }

    private void dbcommentsave(HashMap<String, String> comment) {
        String _commentnum = comment.get("commentnum");
        int commentnum = Integer.parseInt(_commentnum);
        String imageid = comment.get("imageid");
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < commentnum; i++) {
            updatetime.valueOf(comment.get("commentdate"+i));
            db.commentinsert(comment.get("commentid" + i), comment.get("commentuser" + i), imageid, comment.get("comment"), updatetime);
        }
    }

    //关注的人缩略图保存
    private void imagecaredsave(String imageid, String updatedate, String userid) {
        if (!db.checkuserimage(imageid, userid)) {
            Timestamp updatetime = new Timestamp(System.currentTimeMillis());
            updatetime.valueOf(updatedate);
            db.imagecaredinsert(imageid, userid, updatetime);
        }
    }

    //数据库存储标签
    private void dbtagsave(HashMap<String, String> tag) {
        String _tagnum = tag.get("tagnum");
        int tagnum = Integer.parseInt(_tagnum);
        for (int i = 0; i < tagnum; i++) {
            db.taginsert(tag.get("tagid" + i), tag.get("tagname" + i), tag.get("imageid"));
        }
    }
}
