package com.example.team.myapplication.Database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by coco on 2015/5/23.
 */
//重载数据库建立函数  使数据库文件保存在应用程序目录中
class CustomPathDatabaseContext extends ContextWrapper {

    private String mDirPath;

    public CustomPathDatabaseContext(Context base, String dirPath) {
        super(base);
        this.mDirPath = dirPath;
    }

    @Override
    public File getDatabasePath(String name) {
        File result = new File(mDirPath  + name);

        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }

        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
    }
}