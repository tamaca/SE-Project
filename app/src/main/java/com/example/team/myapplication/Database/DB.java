package com.example.team.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.team.myapplication.Cache.Localstorage;
import com.example.team.myapplication.Network.AES;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by coco on 2015/5/15.
 */
public class DB extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "Dolphin.db";
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_PATH = Localstorage.DATABASE_DIR_PATH;
    //
    private final static String M_USER = "m_user";
    private final static String M_USER_ID = "m_user_id";
    private final static String M_USER_PASSWORD = "m_user_password";
    //
    private final static String M_LASTUSER = "m_lastuser";
    private final static String M_LASTUSER_ID = "m_lastuser_id";
    private final static String M_LASTUSER_PASSWORD = "m_lastuser_password";
    //
    private final static String M_IMAGE =   "m_image";
    private final static String M_IMAGE_IMAGEID = "m_image_imageid";
    private final static String M_IMAGE_USERID = "m_image_userid";
    private final static String M_IMAGE_LIKENUMBER = "m_image_likenumber";
    private final static String M_IMAGE_UPDATEDATE = "m_image_updatedate";
    //
    private final static String M_COMMENT = "m_comment";
    private final static String M_COMMENT_COMMENTID = "m_comment_commentid";
    private final static String M_COMMENT_USERID = "m_comment_userid";
    private final static String M_COMMENT_IMAGEID = "m_comment_imageid";
    private final static String M_COMMENT_CONTENT = "m_comment_content";
    private final static String M_COMMENT_COMMETNTDATE = "m_comment_commentdate";
    //
    private final static String M_TAG = "m_tag";
    private final static String M_TAG_ID = "m_tag_id";
    private final static String M_TAG_NAME = "m_tag_name";
    private final static String M_TAG_IMAGEID = "m_tag_imageid";
    //
    private final static String M_LOBBYIMAGE = "m_lobbyimage";
    private final static String M_LOBBYIMAGE_IMAGEID = "m_lobbyimage_imageid";
    private final static String M_LOBBYIMAGE_RANK = "m_lobbyimage_rank";

    public DB(Context context) {
// TODO Auto-generated constructor stub
        super(new CustomPathDatabaseContext(context, getDirPath()), DATABASE_NAME, null, DATABASE_VERSION);
        // super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * 获取db文件在sd卡的路径
     * @return
     */
    private static String getDirPath(){
        //TODO 这里返回存放db的文件夹的绝对路径
        return DATABASE_PATH;
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user = "CREATE TABLE " + M_USER + " (" + M_USER_ID
                + " TEXT primary key , " + M_USER_PASSWORD + " TEXT);";
        String lastuser = "CREATE TABLE " + M_LASTUSER + " (" + M_LASTUSER_ID
                + " TEXT primary key, " + M_LASTUSER_PASSWORD + " TEXT NOT NULL," + "FOREIGN KEY (M_LASTUSER_ID) REFERENCES M_USER(M_USER_ID));";
        String image = "CREATE TABLE " + M_IMAGE + " (" + M_IMAGE_IMAGEID
                + " TEXT primary key, " + M_IMAGE_USERID + " TEXT NOT NULL, "
                + M_IMAGE_LIKENUMBER + " TEXT NOT NULL," + M_IMAGE_UPDATEDATE + " DATETIME NOT NULL," + " FOREIGN KEY (M_IMAGE_USERID) REFERENCES M_USER(M_USER_ID) ON DELETE CASCADE);";
        String comment = "CREATE TABLE " + M_COMMENT + " (" + M_COMMENT_COMMENTID + " TEXT primary key, "
                + M_COMMENT_USERID + " TEXT NOT NULL, " + M_COMMENT_IMAGEID + " TEXT NOT NULL, " + M_COMMENT_CONTENT + " TEXT NOT NULL,"
                + M_COMMENT_COMMETNTDATE + " DATETIME NOT NULL," + "FOREIGN KEY (M_COMMENT_USERID) REFERENCES M_USER(M_USER_ID)  ON DELETE CASCADE, "
                + "FOREIGN KEY (M_COMMENT_IMAGEID) REFERENCES M_IMAGE(M_IMAGE_IMAGEID) ON DELETE CASCADE);";
        String tag = "CREATE TABLE " + M_TAG + " (" + M_TAG_ID
                + " TEXT primary key," + M_TAG_NAME + " TEXT NOT NULL, "+ M_TAG_IMAGEID + " TEXT NOT NULL,"
                + "FOREIGN KEY (M_TAG_IMAGEID) REFERENCES M_IMAGE(M_IMAGE_IMAGEID) ON DELETE CASCADE);";
        String lobbyimage = "CREATE TABLE " + M_LOBBYIMAGE + " (" + M_LOBBYIMAGE_RANK + " TEXT primary key," + M_LOBBYIMAGE_IMAGEID
                + " TEXT NOT NULL," + "FOREIGN KEY (M_LOBBYIMAGE_IMAGEID) REFERENCES M_IMAGE(M_IMAGE_IMAGEID)   ON DELETE CASCADE);";
        db.execSQL(user);
        db.execSQL(image);
        db.execSQL(lastuser);
        db.execSQL(comment);
        db.execSQL(tag);
        db.execSQL(lobbyimage);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + M_USER + M_LASTUSER + M_IMAGE + M_COMMENT + M_TAG + M_LOBBYIMAGE;
        db.execSQL(sql);
        onCreate(db);
    }
    public Cursor userselect() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_USER, null, null, null, null, null, null);
        return cursor;
    }

    public long userinsert(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_USER_ID, id);
        long row = db.insert(M_USER, null, cv);
        return row;
    }
    public long userinsert(String id,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_USER_ID, id);
        cv.put(M_USER_PASSWORD,password);
        long row = db.insert(M_USER, null, cv);
        return row;
    }
    public void userdelete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_USER_ID + " = ?";
        String[] whereValue = {id};
        db.delete(M_USER, where, whereValue);
    }
    public String getmUserPassword(String id)
    {
        String key = "1234567891234567";
        AES aes= new AES(key);
        String encryptid=aes.encrypt(id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_USER where M_USER_ID='" + encryptid.trim() + "'", null);
        if (cur.moveToFirst()) {
            String encryptpassword = cur.getString((cur.getColumnIndex("m_user_password")));
            if(encryptpassword==null)
            {
                return null;
            }
            String password=aes.decrypt(encryptpassword);
            return password;
        }
        return null;
    }
    private boolean usercheck(String id, SQLiteDatabase db) {
        Cursor cur = db.rawQuery("select * from M_USER where M_USER_ID='" + id.trim() + "'", null);
        if (cur.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean checkuser(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_USER where M_USER_ID='" + id.trim() + "'", null);
        if (cur.moveToFirst()) {
            String id1 = cur.getString((cur.getColumnIndex("m_user_id")));
            return true;
        } else {
            return false;
        }
    }
    public void userupdatepassword(String id, String newpassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_USER_ID + " = ?";
        String[] whereValue = {id};
        ContentValues cv = new ContentValues();
        cv.put(M_USER_PASSWORD, newpassword);
        db.update(M_USER, cv, where, whereValue);
    }
    //Lastuser
    public Cursor lastuserselect() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_LASTUSER, null, null, null, null, null, null);
        return cursor;
    }

    public long lastuserinsert(String id,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_LASTUSER_ID, id);
        cv.put(M_LASTUSER_PASSWORD, password);
        long row = db.insert(M_LASTUSER, null, cv);
        return row;
    }

    public void lastuserdelete() {
        Cursor cursor=lastuserselect();
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex("m_lastuser_id"));
            SQLiteDatabase db = this.getWritableDatabase();
            String where = M_LASTUSER_ID + " = ?";
            String[] whereValue = {id};
            db.delete(M_LASTUSER, where, whereValue);
        } else {
            try {
                throw new Exception("出错了");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void lastuserupdateid(String newid,String newpassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_LASTUSER", null);
        if (cur.moveToFirst()) {
            String id = cur.getString((cur.getColumnIndex("m_lastuser_id")));
            String where = M_LASTUSER_ID + " = ?";
            String[] whereValue = {id};
            ContentValues cv = new ContentValues();
            cv.put(M_LASTUSER_ID, newid);
            cv.put(M_LASTUSER_PASSWORD, newpassword);
            db.update(M_LASTUSER, cv, where, whereValue);
        } else {
            Log.e("errorbig", "Can't");
        }
    }
    //Image
    public Cursor imageselect() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_IMAGE, null, null, null, null, null, null);
        return cursor;
    }

    public long imageinsert(String imageid, String userid, String likenumber, Timestamp updatedate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_IMAGE_IMAGEID, imageid);
        cv.put(M_IMAGE_USERID, userid);
        cv.put(M_IMAGE_LIKENUMBER, likenumber);
        cv.put(M_IMAGE_UPDATEDATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updatedate));
        long row = db.insert(M_IMAGE, null, cv);
        return row;
    }

    public void imagedelete(String imageid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_IMAGE_IMAGEID + " = ?";
        String[] whereValue = {imageid};
        db.delete(M_IMAGE, where, whereValue);
    }

    public void imageupdatelikenumber(String imageid, String newlikenumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_IMAGE_IMAGEID + " = ?";
        String[] whereValue = {imageid};
        ContentValues cv = new ContentValues();
        cv.put(M_IMAGE_LIKENUMBER, newlikenumber);
        db.update(M_IMAGE, cv, where, whereValue);
    }
    public Cursor userimageselect(String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_IMAGE where M_IMAGE_USERID='" + userid.trim() + "'", null);
        return cur;
    }

    //Comment
    public Cursor commentselect() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_COMMENT, null, null, null, null, null, null);
        return cursor;
    }

    public long commentinsert(String commentid, String userid, String imageid, String content, Timestamp commentdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_COMMENT_COMMENTID, commentid);
        cv.put(M_COMMENT_USERID, userid);
        cv.put(M_COMMENT_IMAGEID, imageid);
        cv.put(M_COMMENT_CONTENT, content);
        cv.put(M_COMMENT_COMMETNTDATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commentdate));
        long row = db.insert(M_COMMENT, null, cv);
        return row;
    }

    public void commentdelete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_COMMENT_COMMENTID + " = ?";
        String[] whereValue = {id};
        db.delete(M_COMMENT, where, whereValue);
    }

    public void commentupdatecontent(String commentid, String newcontent) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_COMMENT_COMMENTID + " = ?";
        String[] whereValue = {commentid};
        ContentValues cv = new ContentValues();
        cv.put(M_COMMENT_CONTENT, newcontent);
        db.update(M_COMMENT, cv, where, whereValue);
    }

    public Cursor imagecommentselect(String imageid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_COMMENT where M_COMMENT_IMAGEID='" + imageid.trim() + "'", null);
        return cur;
    }

    //Tag
    public Cursor tagselect() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_TAG, null, null, null, null, null, null);
        return cursor;
    }

    public long taginsert(String tagid, String tagname, String imageid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_TAG_ID, tagid);
        cv.put(M_TAG_IMAGEID, imageid);
        cv.put(M_TAG_NAME, tagname);
        long row = db.insert(M_TAG, null, cv);
        return row;
    }

    public void tagdelete(String tagid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_TAG_ID + " = ?";
        String[] whereValue = {tagid};
        db.delete(M_TAG, where, whereValue);
    }

    public void tagupdate(String tagid, String newtagname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_TAG_ID + " = ?";
        String[] whereValue = {tagid};
        ContentValues cv = new ContentValues();
        cv.put(M_TAG_NAME, newtagname);
        db.update(M_TAG, cv, where, whereValue);
    }

    public Cursor tagimageselect(String tagname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from M_TAG where M_TAG_NAME='" + tagname.trim() + "'", null);
        return cur;
    }

    //lobbyimage
    public Cursor lobbyimageselect() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(M_LOBBYIMAGE, null, null, null, null, null, null);
        return cursor;
    }

    public long lobbyimageinsert(String rank, String imageid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(M_LOBBYIMAGE_IMAGEID, imageid);
        cv.put(M_LOBBYIMAGE_RANK, rank);
        long row = db.insert(M_LOBBYIMAGE, null, cv);
        return row;
    }

    public void lobbyimagedelete(String rank) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = M_LOBBYIMAGE_RANK + " = ?";
        String[] whereValue = {rank};
        db.delete(M_LOBBYIMAGE, where, whereValue);
    }

    public void lobbyimageupdate(String rank, String newimageid) {
        lobbyimagedelete(rank);
        lobbyimageinsert(rank, newimageid);
    }

}
