package com.example.team.myapplication.Network;

import android.util.Log;
import android.view.View;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.ViewPictureActivity;
import com.example.team.myapplication.util.Comment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http4.client.entity.UrlEncodedFormEntityHC4;
import org.apache.http4.client.methods.CloseableHttpResponse;
import org.apache.http4.client.methods.HttpPostHC4;
import org.apache.http4.impl.client.CloseableHttpClient;
import org.apache.http4.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    boolean autoLogin;
    boolean rememPassword;
    private DB db;
    private ViewPictureActivity view;
    private JSONObject returnjsonObject = null;

    public HashMap<String, String> getReturnmap() {
        return returnmap;
    }

    private HashMap<String, String> returnmap;

    //多种处理方式
    //登录
    public JsonPost(HashMap<String, String> map, String url, boolean autoLogin, boolean rememPassword, DB db) throws Exception {
        this.url = url;
        this.autoLogin = autoLogin;
        this.rememPassword = rememPassword;
        this.db = db;
        Post post = new Post(map);
        returnjsonObject = post.PostToServer();
        post.PostExecuteLogin(returnjsonObject);
    }

    //注册、修改密码 图片信息获取 上传评论
    public JsonPost(HashMap<String, String> map, String url, String type, DB db) throws Exception {
        this.url = url;
        this.db = db;
        Post post = new Post(map);
        returnjsonObject = post.PostToServer();
        if (type.equals("register")) {
            post.PostExecute(returnjsonObject);
        } else if (type.equals("pwdchange")) {
            post.PostExecuteChangePassword(returnjsonObject);
        } else if (type.equals("imageinfo")) {
            post.PostExecuteImageInformation(returnjsonObject);
        } else if (type.equals("commentinsert")) {
            post.PostExecuteCommentInsert(returnjsonObject);
        } else if (type.equals("taginsert")) {
            returnmap = post.PostExecuteTag(returnjsonObject);
        } else if (type.equals("tagdelete")) {
            post.PostExecute(returnjsonObject);
        } else {
            throw new packageException();
        }
    }


    // 获取关注和黑名单信息//添加关注 黑名单
    public JsonPost(HashMap<String, String> map, String url, String type) throws Exception {
        this.url = url;
        Post post = new Post(map);
        returnjsonObject = post.PostToServer();
        if (type.equals("relation")) {
            post.PostExecuteRelation(returnjsonObject);
        } else if (type.equals("concern")) {
            post.PostExecuteIODRelation(returnjsonObject);
        } else {
            post.PostExecute(returnjsonObject);
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

    //数据库存储图片
    private void dbimagesave(HashMap<String, String> image) {
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        updatetime.valueOf(image.get("updatetime"));
        db.imageinsert(image.get("imageid"), image.get("userid"), image.get("islike"), image.get("likenumber"), updatetime);
    }

    //数据库存储评论
    private void dbcommentsave(HashMap<String, String> commentmap) {
        String _commentnum = commentmap.get("commentnum");
        int commentnum = Integer.parseInt(_commentnum);
        String imageid = commentmap.get("imageid");
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        for (int i = 1; i <= commentnum; i++) {
            updatetime.valueOf(commentmap.get("updatedate" + String.valueOf(i - 1)));
            db.commentinsert(commentmap.get("commentid" + String.valueOf(i - 1)), commentmap.get("userid" + String.valueOf(i - 1)), imageid, commentmap.get("comment" + String.valueOf(i - 1)), updatetime);
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

    //UI处理图片信息
    private HashMap<String, String> getImageInformation(JSONObject info) throws Exception {
        String originImageurl = info.getString("origin");
        String imageId = info.getString("image_id");
        String _author = info.getString("author");
        String _like = info.getString("like");
        String _isLike = info.getString("is_like");
        String _updateTime = info.getString("update_time");
        HashMap<String, String> image = new HashMap<String, String>();
        image.put("origin", originImageurl);
        image.put("imageid", imageId);
        image.put("userid", _author);
        image.put("islike", _isLike);
        image.put("likenumber", _like);
        image.put("updatetime", _updateTime);
        dbimagesave(image);
        return image;
    }

    private HashMap<String, String> getTag(JSONObject info) throws Exception {
        String tag_status = info.getString("tag_status");
        int tagnum;
        if (tag_status.equals("normal")) {
            tagnum = 5;
        } else {
            String _tagnum = info.getString("count");
            tagnum = Integer.parseInt(_tagnum);
        }
        HashMap<String, String> tag = new HashMap<String, String>();
        tag.put("tagnum", String.valueOf(tagnum));
        tag.put("imageid", info.getString("image_id"));
        for (int i = 0; i < tagnum; i++) {
            tag.put("tagname" + i, info.getString("tag" + String.valueOf(i)));
            tag.put("tagid" + i, info.getString("tagid" + String.valueOf(i)));
        }
        dbtagsave(tag);
        //TODO:数据库
        return tag;
    }

    private class Post {
        //  HttpClient client = new DefaultHttpClient();
        CloseableHttpClient client = HttpClients.custom().useSystemProperties().build();
        //HttpPost httpPost = new HttpPost(url);
        HttpPostHC4 httpPost = new HttpPostHC4(url);
        JSONObject jsonObject = new JSONObject();
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        HashMap<String, String> map;

        Post(HashMap<String, String> map) {
            this.map = map;
        }

        protected JSONObject PostToServer() throws Exception {
            try {
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    jsonObject.put(key, val);
                }
                nameValuePair.add(new BasicNameValuePair("jsonString", jsonObject
                        .toString()));
            } catch (Exception e) {
                throw new packageException();
            }
            try {
                CloseableHttpResponse response;
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(new UrlEncodedFormEntityHC4(nameValuePair, "UTF-8"));
                response = client.execute(httpPost);
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
                throw new postException();
            }
        }

        protected void PostExecuteLogin(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String _username = jsonObject.getString("username");
                    dbsaveuser(_username, this.jsonObject.getString("password"));
                } else {
                    throw new executeException();
                }
            }
        }

        protected void PostExecuteComment(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String username = jsonObject.getString("user_name");
                    String content = jsonObject.getString("user_content");
                } else {
                    throw new executeException();
                }
            }
        }

        private HashMap<String, String> PostExecuteTag(JSONObject info) throws Exception {
            String status = info.getString("status");
            HashMap<String, String> tag = new HashMap<String, String>();
            tag.put("tagnum", "1");
            tag.put("imageid", info.getString("image_id"));
            tag.put("tagname" + 0, info.getString("tag" + 0));
            tag.put("tagid" + 0, info.getString("tagid" + 0));
            dbtagsave(tag);
            return tag;
        }

        protected void PostExecuteImageInformation(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    returnmap = new HashMap<String, String>();
                    returnmap.putAll(getImageInformation(jsonObject));
                    returnmap.putAll(getTag(jsonObject));
                } else {
                    throw new executeException();
                }
            }
        }

        protected void PostExecuteChangePassword(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    //  String email=jsonObject.getString("email");
                    // String oldpassword=jsonObject.getString("oldpassword");
                    //String newpassword=jsonObject.getString("newpassword");
                        /*  if(db.checklastuserpassword())//有可能出错
                          {
                              db.lastuserupdatepassword(map.get("password"));
                          }*/
                } else {
                    throw new executeException();
                }
            }
        }

        protected void PostExecuteRelation(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String _concern = jsonObject.getString("concern");
                    String _blacklist = jsonObject.getString("blacklist");
                    String picturecount = jsonObject.getString("count");
                    returnmap = new HashMap<String, String>();
                    returnmap.put("concern", _concern);
                    returnmap.put("blacklist", _blacklist);
                    returnmap.put("picturecount", picturecount);
                } else {
                    throw new executeException();
                }
            }
        }

        protected void PostExecuteIODRelation(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            } else {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String picturecount = jsonObject.getString("count");
                    returnmap = new HashMap<String, String>();
                    returnmap.put("picturecount", picturecount);
                } else {
                    throw new executeException();
                }
            }
        }

        protected void PostExecuteCommentInsert(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            }
            String status = jsonObject.getString("status");
            if (status.equals("normal")) {
                String comment_status = jsonObject.getString("comment_status");
                int num;
                if (comment_status.equals("normal")) {
                    num = 5;
                } else {
                    String _num = jsonObject.getString("count");
                    num = Integer.parseInt(_num);
                }
                returnmap.put("commentnum", String.valueOf(num));
                returnmap.put("imageid", jsonObject.getString("image_id"));
                for (int i = 0; i < num; i++) {
                    returnmap.put("comment" + i, jsonObject.getString("commment" + String.valueOf(i)));
                    returnmap.put("commentid" + i, jsonObject.getString("commentid" + String.valueOf(i)));
                    returnmap.put("userid" + i, jsonObject.getString("userid" + String.valueOf(i)));
                    returnmap.put("updatedate" + i, jsonObject.getString("updatedate" + String.valueOf(i)));
                }
                dbcommentsave(returnmap);
                //TODO:数据库
            } else {
                throw new executeException();
            }
        }

        protected void PostExecute(JSONObject jsonObject) throws Exception {
            if (jsonObject == null) {
                throw new nullException();
                //TODO:网络通信错误
            }
            String status = jsonObject.getString("status");
            if (!status.equals("normal")) {
                throw new executeException();
            }
        }
    }

    //异常类
    class packageException extends Exception {
        public String name = "package";
    }

    class postException extends Exception {
        public String name = "post";
    }

    class nullException extends Exception {
        public String name = "null";
    }

    class executeException extends Exception {
        public String name = "execute";
    }
}
