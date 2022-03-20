package com.example.projekt_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "user.db";
    public static final String TABLE_NAME = "user_table";
    public static final String ID = "ID";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "SEX";
    public static final String COL_3 = "AGE";
    public static final String COL_4 = "HEIGHT";
    public static final String COL_5 = "WEIGHT";
    public static final String COL_6 = "SELECTED";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                      +COL_1+" TEXT," + COL_2+ " TEXT,"
                                                      +COL_3+" INT," + COL_4+ " INT,"
                                                      +COL_5+" DOUBLE," + COL_6+ " INT)");
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.d(TAG, "onUpgrade");
    }

    // Insert row with data to database at autoincrement ID
    public boolean insertData(String name, String sex, int age, int height, double weight, int selected){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, sex);
        contentValues.put(COL_3, age);
        contentValues.put(COL_4, height);
        contentValues.put(COL_5, weight);
        contentValues.put(COL_6, selected);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            Log.d(TAG, "Insert data failed");
            return false;
        }else{
            Log.d(TAG, "Insert data succeeded");
            return true;
        }
    }

    // Get all rows and columns from database
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        Log.d(TAG, "Get all data called");
        return res;
    }

    // Get all columns of an user at given ID
    public Cursor getSingleDataByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where ID = " + String.valueOf(id), null);
        Log.d(TAG, "Get data by ID called");
        return res;
    }

    // Get ID and Name of currently selected user
    public Cursor getIDAndNameofSelectedUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID, name from " + TABLE_NAME + " where " + COL_6 + " = " + String.valueOf(1), null);
        Log.d(TAG, "Get ID and name of selected user called");
        return res;
    }


    // Update all columns of a row
    public boolean updateData(String id, String name, String sex, int age, int height, double weight, int selected){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, sex);
        contentValues.put(COL_3, age);
        contentValues.put(COL_4, height);
        contentValues.put(COL_5, weight);
        contentValues.put(COL_6, selected);
        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {id});
        if(result == -1){
            Log.d(TAG, "Update data failed");
            return false;
        }else{
            Log.d(TAG, "Update data succeeded");
            return true;
        }
    }

    // Update currently selected user col_6 with 0 and set 1 at new current user's col_6
    public boolean updateSelectedUser(String new_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, 0);
        long result = db.update(TABLE_NAME, contentValues, COL_6 + " = ?", new String[] {"1"});
        if(result == -1) {
            Log.d(TAG, "Update data of selected user failed (deselecting)");
            return false;
        }
        contentValues.put(COL_6, 1);
        result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {new_ID});
        if(result == -1) {
            Log.d(TAG, "Update data of selected user failed (selecting)");
            return false;
        }
        else {
            Log.d(TAG, "Update data of selected user succeeded (selecting)");
            return true; }
        //db.rawQuery("UPDATE " + TABLE_NAME + " SET " + COL_6 + " = 0 WHERE " + COL_6 + " = 1", null);
        //db.rawQuery("UPDATE " + TABLE_NAME + " SET " + COL_6 + " = 1 WHERE " + ID + " = " + new_ID, null);
    }

    // Delete row at selected ID from database
    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Delete data called");
        return db.delete(TABLE_NAME, "ID = ?",  new String[] {id});
    }
}
