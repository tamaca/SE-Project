package com.example.team.myapplication.Network;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.LoginState;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by coco on 2015/6/3.
 */
public class JsonGet {
    private String url;
    private DB db;
    private View view;

    //关注或黑名单中的人
    public ArrayList<String> getUserNames() {
        return userNames;
    }

    private ArrayList<String> userNames;

    public HashMap<String, String> getReturnmap() {
        return returnmap;
    }

    private HashMap<String, String> returnmap;
    private String type;

    //图片获取(url)
    public JsonGet(String url, DB db, View view, String type) throws Exception {
        this.url = url;
        this.db = db;
        this.view = view;
        this.type = type;
        Get get = new Get();
        JSONObject jsonObject = get.GetFromServer();
        get.PostExecuteImageUrl(jsonObject);
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
                throw new getException();
            }
        }

        //接收图片(url)
        protected void PostExecuteImageUrl(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    //TODO:不满4张需要处理
                    String baseurl = "http://192.168.253.1/media/";
                    String image_small[] = new String[4];
                    String image_big[] = new String[4];
                    String image_id[] = new String[4];
                    for (int i = 0; i <= 3; i++) {
                        image_small[i] = baseurl + jsonObject.getString("image" + i + "_small");
                        image_big[i] = baseurl + jsonObject.getString("image" + i + "_big");
                        image_id[i] = jsonObject.getString("image" + i + "_id");
                    }
                    if (view != null) {
                        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1);
                        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
                        ImageView imageView3 = (ImageView) view.findViewById(R.id.imageView3);
                        ImageView imageView4 = (ImageView) view.findViewById(R.id.imageView4);
                        ImageGet imageGet1 = new ImageGet(imageView1, image_small[0], image_id[0], db, "small");
                        ImageGet imageGet2 = new ImageGet(imageView2, image_small[1], image_id[1], db, "small");
                        ImageGet imageGet3 = new ImageGet(imageView3, image_small[2], image_id[2], db, "small");
                        ImageGet imageGet4 = new ImageGet(imageView4, image_small[3], image_id[3], db, "small");
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("type", "online");
                        jsonObject1.put("imageid", image_id[0]);
                        jsonObject1.put("imagebigurl", image_big[0]);
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("type", "online");
                        jsonObject2.put("imageid", image_id[1]);
                        jsonObject2.put("imagebigurl", image_big[1]);
                        JSONObject jsonObject3 = new JSONObject();
                        jsonObject3.put("type", "online");
                        jsonObject3.put("imageid", image_id[2]);
                        jsonObject3.put("imagebigurl", image_big[2]);
                        JSONObject jsonObject4 = new JSONObject();
                        jsonObject4.put("type", "online");
                        jsonObject4.put("imageid", image_id[3]);
                        jsonObject4.put("imagebigurl", image_big[3]);
                        imageView1.setContentDescription(jsonObject1.toString());
                        imageView2.setContentDescription(jsonObject2.toString());
                        imageView3.setContentDescription(jsonObject3.toString());
                        imageView4.setContentDescription(jsonObject4.toString());
                    } else {
                        ImageGet imageGet6 = new ImageGet(null, image_small[0], image_id[0], db, "small");
                        ImageGet imageGet7 = new ImageGet(null, image_small[1], image_id[1], db, "small");
                        ImageGet imageGet8 = new ImageGet(null, image_small[2], image_id[2], db, "small");
                        ImageGet imageGet9 = new ImageGet(null, image_small[3], image_id[3], db, "small");
                    }
                    dbimagesave(image_id[0]);
                    dbimagesave(image_id[1]);
                    dbimagesave(image_id[2]);
                    dbimagesave(image_id[3]);
                    if (type.equals("lobby")) {
                        dblobbyimagesave(String.valueOf(LoginState.page * 4 + 1), image_id[0]);
                        dblobbyimagesave(String.valueOf(LoginState.page * 4 + 2), image_id[1]);
                        dblobbyimagesave(String.valueOf(LoginState.page * 4 + 3), image_id[2]);
                        dblobbyimagesave(String.valueOf(LoginState.page * 4 + 4), image_id[3]);
                    } else {
                        //TODO:TA的动态数据库存储
                    }
                } else {
                    throw new executeException();
                    //TODO:接收信息错误
                }
                //这里写跳转代码
                //loginActivity.showProgress(false);
            } else {
                //TODO:接收信息错误
                throw new nullException();
            }

        }

        //黑名单 和 关注的人
        protected ArrayList<String> PostExecuteList(JSONObject jsonObject) throws Exception {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status.equals("normal")) {
                    String _num = jsonObject.getString("num");
                    int num = Integer.parseInt(_num);
                    ArrayList<String> userNames = new ArrayList<String>();
                    for (int i = 0; i < num; i++) {
                        String _username = jsonObject.getString("username" + i);
                        userNames.add(_username);
                    }
                    return userNames;
                } else {
                    throw new executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new nullException();
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
                    throw new executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new nullException();
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
                    throw new executeException();
                }
            } else {
                //TODO:接收信息错误
                throw new nullException();
            }
        }

        //异常类
        class getException extends Exception {
            public String name = "get";
        }

        class nullException extends Exception {
            public String name = "null";
        }

        class executeException extends Exception {
            public String name = "execute";
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

    //关注的人缩略图保存
    private void imagecaredsave(String imageid, String updatedate, String userid) {
        if (!db.checkuserimage(imageid, userid)) {
            Timestamp updatetime = new Timestamp(System.currentTimeMillis());
            updatetime.valueOf(updatedate);
            db.imagecaredinsert(imageid, userid, updatetime);
        }
    }
}
