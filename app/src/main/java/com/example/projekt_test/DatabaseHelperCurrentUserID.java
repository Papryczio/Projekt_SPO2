package com.example.projekt_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelperCurrentUserID extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "currentUser.db";
    public static final String TABLE_NAME = "currentUser_table";
    public static final String ID = "ID";
    public static final String USER_ID = "USER_ID";

    public DatabaseHelperCurrentUserID(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                      + USER_ID + " INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean updateData(String id, int userID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userID);
        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {id});
        if(result == -1){
            return false;
        } else {
            return true;
        }
    }
}
