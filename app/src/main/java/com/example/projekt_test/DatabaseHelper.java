package com.example.projekt_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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


    public static final String TABLE_NAME_1 = "date_table";
    public static final String COL_11 = "USER_ID";
    public static final String COL_12 = "DATE";


    public static final String TABLE_NAME_2 = "data_table";
    public static final String COL_21 = "ID_DATE";
    public static final String COL_22 = "TIME";
    public static final String COL_23 = "BPM";
    public static final String COL_24 = "SPO2";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " ("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_1+" TEXT,"
                +COL_2+" TEXT,"
                +COL_3+" INT,"
                +COL_4+" INT,"
                +COL_5+" DOUBLE,"
                +COL_6+" INT)"
        );

        db.execSQL("create table " + TABLE_NAME_1 + " ("
                +ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_11+" INT,"
                +COL_12+" DATE, "
                +"FOREIGN KEY (" + COL_11 + ") REFERENCES " + TABLE_NAME + "(" + ID + "))"
        );

        db.execSQL("create table " + TABLE_NAME_2 + " ("
                +ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +COL_21+" INT,"
                +COL_22+" DATE,"
                +COL_23+" INT,"
                +COL_24+" INT,"
                +" FOREIGN KEY (" + COL_21 + ") REFERENCES " + TABLE_NAME_1 + "(" + ID + "))"
        );


        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);
        Log.d(TAG, "onUpgrade");
    }

    // Insert row with data to database at autoincrement ID
    public boolean insertUser(String name, String sex, int age, int height, double weight, int selected){
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
            Log.d(TAG, "Insert user failed");
            return false;
        }else{
            Log.d(TAG, "Insert user succeeded");
            return true;
        }
    }

    public boolean insertDate(String date, String User_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, User_ID);
        contentValues.put(COL_12, date);
        long result = db.insert(TABLE_NAME_1, null, contentValues);
        if(result == -1) {
            Log.d(TAG, "Insert date failed");
            return false;
        } else {
            Log.d(TAG, "Insert date succeeded");
            return true;
        }
    }

    public boolean insertData(String ID_Date, String time, String BPM, String SPO2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_21, ID_Date);
        contentValues.put(COL_22, time);
        contentValues.put(COL_23, BPM);
        contentValues.put(COL_24, SPO2);
        long result = db.insert(TABLE_NAME_2, null, contentValues);
        if(result == - 1) {
            Log.d(TAG, "Insert data failed");
            return false;
        } else {
            Log.d(TAG, "Insert data succeeded");
            return true;
        }
    }

    // Get all rows and columns from database
    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        Log.d(TAG, "Get all users called");
        return res;
    }

    public Cursor getAllDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_1, null);
        Log.d(TAG, "Get all date called");
        return res;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_2, null);
        Log.d(TAG, "Get all data called");
        return res;
    }

    public Cursor getAllDateByUserID(String User_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_1 + " WHERE " + COL_11 + " = " + User_ID, null);
        return res;
    }

    public Cursor getAllData_user_date(String ID_Date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_2 + " WHERE " + COL_21 + " = " + ID_Date, null);
        return res;
    }

    // Get all columns of an user at given ID
    public Cursor getUserByID(int id){
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
    public boolean updateUser(String id, String name, String sex, int age, int height, double weight, int selected){
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
            Log.d(TAG, "Update user failed");
            return false;
        }else{
            Log.d(TAG, "Update user succeeded");
            return true;
        }
    }

    public boolean updateDate(String id, String date, String User_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, User_ID);
        contentValues.put(COL_12, date);
        long result = db.update(TABLE_NAME_1, contentValues, "ID = ?", new String[] {id});
        if(result == -1) {
            Log.d(TAG, "Update date failed");
            return false;
        } else {
            Log.d(TAG, "Update date succeeded");
            return true;
        }
    }

    public boolean updateData(String id,String ID_Date, String time, String BPM, String SPO2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_21, ID_Date);
        contentValues.put(COL_22, time);
        contentValues.put(COL_23, BPM);
        contentValues.put(COL_24, SPO2);
        long result = db.update(TABLE_NAME_2, contentValues, "ID = ?", new String[] {id});
        if(result == -1){
            Log.d(TAG, "Update data failed");
            return false;
        } else {
            Log.d(TAG, "Update data suceeded");
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
    }

    public Cursor checkExistenceOfUserAndDate(String user_id, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_1 + " WHERE " + COL_11 + " = ? AND " + COL_12 + " = ?", new String[] {user_id, date});
        if (res.getCount() > 0){
            return res;
        }
        else {
            return null;
        }
    }

    public String maxDateID(){
        SQLiteDatabase db = this.getWritableDatabase();
        String maxID = "";
        Cursor res = db.rawQuery("SELECT MAX(" + ID + ") FROM " + TABLE_NAME_1, null);
        while(res.moveToNext()){
            maxID = res.getString(0);
        }
        return maxID;
    }


    // Delete row at selected ID from database
    public Integer deleteUser(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Delete user called");
        return db.delete(TABLE_NAME, "ID = ?",  new String[] {id});
    }

    public Integer deleteDate(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Delete date called");
        return db.delete(TABLE_NAME_1, "ID = ?", new String[] {id});
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Delete data called");
        return db.delete(TABLE_NAME_2, "ID = ?", new String[] {id});
    }
}
