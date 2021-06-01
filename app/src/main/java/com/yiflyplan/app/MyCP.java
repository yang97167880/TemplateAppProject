package com.yiflyplan.app;



import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Lin on 2019/3/19.
 */

public class MyCP extends ContentProvider {

    SQLiteDatabase sqLiteDatabase;
    private boolean isDropTable = false;

    public static final String AUTHORITY="yiflyplan";
    public static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, Session.TABLE_NAME, Session.CODE);
        uriMatcher.addURI(AUTHORITY, ChatRecords.TABLE_NAME, ChatRecords.CODE);

    }
    String createSQL="create table session(id INTEGER PRIMARY KEY AUTOINCREMENT,ownId int(255) not null,userId int(255) not null,userAvatar varchar(255) not null, userName varchar(255) DEFAULT '昵称',unreadCount int(255) DEFAULT 0)";


    String createSQL2="create table chatRecords( id INTEGER PRIMARY KEY AUTOINCREMENT,sessionId int(255) not null , content varchar(10000) not null,position int(1) not null, createDate varchar(255) not null)";




    public boolean onCreate() {

//     创建 db
        sqLiteDatabase=getContext().openOrCreateDatabase(AUTHORITY+".db", Context.MODE_PRIVATE,null);




        if(isDropTable){
            sqLiteDatabase.execSQL("drop table session");
            sqLiteDatabase.execSQL("drop table chatRecords");
        }


        if (!exitDataBase(sqLiteDatabase,"session"))
            sqLiteDatabase.execSQL(createSQL);

        if (!exitDataBase(sqLiteDatabase,"chatRecords"))
            sqLiteDatabase.execSQL(createSQL2);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int code = uriMatcher.match(uri);

        if (code == Session.CODE) {
            Cursor result = sqLiteDatabase.query(Session.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
            return result;
        }
        else{
            Cursor result = sqLiteDatabase.query(ChatRecords.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
            return result;
        }

    }





    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code = uriMatcher.match(uri);

        if(code == Session.CODE) {
            sqLiteDatabase.insert(Session.TABLE_NAME, null, values);

        }
        else if(code== ChatRecords.CODE){
            sqLiteDatabase.insert(ChatRecords.TABLE_NAME, null, values);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = uriMatcher.match(uri);

        if(code == Session.CODE) {
            sqLiteDatabase.delete(Session.TABLE_NAME, selection, selectionArgs);
        }
        else if (code== ChatRecords.CODE){
            sqLiteDatabase.delete(ChatRecords.TABLE_NAME,selection,selectionArgs);
        }


        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = uriMatcher.match(uri);

        if(code == Session.CODE) {
            sqLiteDatabase.update(Session.TABLE_NAME, values, selection, selectionArgs);
        }
        else if(code== ChatRecords.CODE){
            sqLiteDatabase.update(ChatRecords.TABLE_NAME,values,selection,selectionArgs);
        }


        return 0;
    }

    public static final class Session implements BaseColumns{

        public static final String TABLE_NAME="session";
        public static final String id="id";
        public static final String ownId="ownId";
        public static final String userId="userId";
        public static final String userAvatar="userAvatar";
        public static final String userName="userName";
        public static final String unreadCount="unreadCount";


        public static final int CODE=1;

        public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);

    }

    public static final class ChatRecords implements BaseColumns{

        public static final String TABLE_NAME="chatRecords";
        public static final String id="id";
        public static final String sessionId="sessionId";
        public static final String content="content";
        public static final String position="position";
        public static final String createDate="createDate";

        public static final int CODE=2;

        public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);

    }



    public boolean exitDataBase(SQLiteDatabase db,String dbTitle) {
        boolean B = false;
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + dbTitle + "';";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                B = true;
            } else {
                B = false;
            }
        }

        cursor.close();
        return B;
    }

}

