package com.example.android_security;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "tblUsers";
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String UID = "uid";
    static final String DB_NAME = "android_security.db";
    static final int DB_VERSION = 1;
    private SQLiteDatabase database;
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,  "+ EMAIL + " TEXT , "+ UID + " TEXT);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData( String email, String uid, Context context){
        ContentValues contentValue = new ContentValues();
        contentValue.put(EMAIL, email);
        contentValue.put(UID, uid);
        SQLiteOpenHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        database.insert(TABLE_NAME, null, contentValue);
    }
    public int updateData(String email, String uid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EMAIL, email);
        contentValues.put(UID, uid);
        int i = database.update(TABLE_NAME, contentValues, ID + " = " + uid, null);
        return i;
    }
    public void deleteData(long id) {
        database.delete(TABLE_NAME, ID + "=" + id, null);
    }


}