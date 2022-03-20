package com.example.projekt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChangeUser extends AppCompatActivity {

    private DatabaseHelper myDb;
    private ListView listView;
    private Button changeUser;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        listView = (ListView)findViewById(R.id.listView_changeUser);
        changeUser = (Button)findViewById(R.id.changeUser_button);

        myDb = new DatabaseHelper(this);
        populateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setSelection(i);
                String item = adapterView.getItemAtPosition(i).toString();
                String list[];
                String list_2[];
                list = item.split("\n");
                list_2 = list[0].split(": ");
                selectedItem = Integer.parseInt(list_2[1]);
                Log.d("CHANGE_USER", "ID separated from data: " + selectedItem);
                Log.d("CHANGE_USER", "ID got: " + item);
            }
        });

        changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.updateSelectedUser(String.valueOf(selectedItem));
                finish();
            }
        });
    }

    private void populateList(){
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
        listView = (ListView) findViewById(R.id.listView_changeUser);
        listView.setAdapter(arrayAdapter);
    }
}