package com.example.projekt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.icu.util.Output;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class addNewUser extends AppCompatActivity {

    private Button confirmButton;
    private EditText nameText;
    private String name;
    private EditText ageText;
    private int age;
    private EditText heightText;
    private int height;
    private EditText weightText;
    private double weight;
    private Spinner sexSpinner;
    private String sex;
    private String data;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        myDb = new DatabaseHelper(this);

        confirmButton = (Button)findViewById(R.id.button_confirm_add);
        nameText = (EditText)findViewById(R.id.editTextName);
        ageText = (EditText)findViewById(R.id.editTextAge);
        heightText = (EditText)findViewById(R.id.editTextHeight);
        weightText = (EditText)findViewById(R.id.editTextWeight);
        sexSpinner = (Spinner)findViewById(R.id.spinnerSex);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                name = nameText.getText().toString();
                age = Integer.parseInt(ageText.getText().toString());
                height = Integer.parseInt(heightText.getText().toString());
                weight = Double.parseDouble(weightText.getText().toString());
                sex = sexSpinner.getSelectedItem().toString();

                Log.d("ADD_NEW_USER", "User: " + name + " " + age + "y.o " + sex + " " + height + "cm " + weight + "kg ");

                if(!(age < 120 && age > 0)){
                    valid = false;
                    ageText.setBackgroundColor(Color.RED);
                }else{
                    ageText.setBackgroundColor(Color.TRANSPARENT);
                }

                if(!(height > 50 && height < 270)){
                    valid = false;
                    heightText.setBackgroundColor(Color.RED);
                }else{
                    heightText.setBackgroundColor(Color.TRANSPARENT);
                }

                if(!(weight > 5 && weight < 350)){
                    valid = false;
                    weightText.setBackgroundColor(Color.RED);
                }else{
                    weightText.setBackgroundColor(Color.TRANSPARENT);
                }

                if(sex == "Sex"){
                    valid = false;
                    sexSpinner.setBackgroundColor(Color.RED);
                }else{
                    sexSpinner.setBackgroundColor(Color.TRANSPARENT);
                }

                if(valid == true) {
                    Log.d("ADD_NEW_USER", "Data added");
                    boolean result = myDb.insertData(name, sex, age, height, weight);
                    if(result == true){
                        Toast.makeText(getApplicationContext(), "User added", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }else{
                        Toast.makeText(getApplicationContext(), "Database error", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Log.d("ADD_NEW_USER", "Invalid data");
                    Toast.makeText(getApplicationContext(), "Incorrect data", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}