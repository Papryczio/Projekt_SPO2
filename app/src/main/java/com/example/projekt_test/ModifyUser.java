package com.example.projekt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ModifyUser extends AppCompatActivity {

    private DatabaseHelper myDb;
    private ListView listView;
    private Button deleteButton;
    private Button modButton;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        deleteButton = (Button)findViewById(R.id.button_delete);
        modButton = (Button)findViewById(R.id.button_modify);
        listView = (ListView) findViewById(R.id.listView_modUser);

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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, user_array);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i+1;
                Log.d("MODUSER", "ID got: " + selectedItem);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = myDb.deleteData(String.valueOf(selectedItem));
                Log.d("MODUSER: delete", "Check of item delete: " + check);
            }
        });

        modButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, user_array);
        listView.setAdapter(arrayAdapter);
    }
}