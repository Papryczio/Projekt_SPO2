package com.example.projekt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ChangeUser extends AppCompatActivity {

    private DatabaseHelper myDb;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        populateList();
    }

    private void populateList(){
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        ArrayList<StringBuffer> user_array = new ArrayList<>();
        while(res.moveToNext()){
            StringBuffer buffer = new StringBuffer();
            buffer.append("Id: " + res.getString(0) +"\n");
            buffer.append("Name: " + res.getString(1)+ "\n");
            buffer.append("Sex: " + res.getString(2) +"\n");
            buffer.append("Age: " + res.getString(3) +"\n");
            buffer.append("Height: " + res.getString(4) +"\n");
            buffer.append("Weight: " + res.getString(5) +"\n");
            user_array.add(buffer);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, user_array);
        listView = (ListView) findViewById(R.id.listView_users);
        listView.setAdapter(arrayAdapter);
    }
}