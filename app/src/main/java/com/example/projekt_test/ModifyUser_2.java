package com.example.projekt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ModifyUser_2 extends AppCompatActivity {

    private Button confirmButton;
    private EditText nameText;
    private EditText ageText;
    private EditText heightText;
    private EditText weightText;
    private Spinner sexSpinner;
    private DatabaseHelper myDb;
    private int selectedItem;
    private ModifyUser mu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user2);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            selectedItem = extras.getInt("ID");
        }
        else{
            return;
        }

        String name = "";
        String sex = "";
        int age = -1;
        int height = -1;
        double weight = -1;

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getUserByID(selectedItem);
        while (res.moveToNext()) {
            name = res.getString(1);
            sex = res.getString(2);
            age = Integer.parseInt(res.getString(3));
            height = Integer.parseInt(res.getString(4));
            weight = Double.parseDouble(res.getString(5));
        }
        confirmButton = (Button)findViewById(R.id.button_mod_confirm_add);
        nameText = (EditText)findViewById(R.id.editTextName_mod);
        nameText.setText(name);
        ageText = (EditText)findViewById(R.id.editTextAge_mod);
        ageText.setText(String.valueOf(age));
        heightText = (EditText)findViewById(R.id.editTextHeight_mod);
        heightText.setText(String.valueOf(height));
        weightText = (EditText)findViewById(R.id.editTextWeight_mod);
        weightText.setText(String.valueOf(weight));
        sexSpinner = (Spinner)findViewById(R.id.spinnerSex_mod);
        switch(sex){
            case ("Male"):
                sexSpinner.setSelection(1);
                break;
            case ("Female"):
                sexSpinner.setSelection(2);
                break;
            case("Other"):
                sexSpinner.setSelection(3);
            default:
                sexSpinner.setSelection(0);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                String new_name = null;
                String new_sex = null;
                int new_age = -1;
                int new_height = -1;
                double new_weight = -1;

                if(nameText.getText().toString() != ""){
                    new_name = nameText.getText().toString();
                    nameText.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    nameText.setBackgroundColor(Color.RED);
                    valid = false;
                }
                if(sexSpinner.getSelectedItem() != "Sex"){
                    new_sex = sexSpinner.getSelectedItem().toString();
                    sexSpinner.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    sexSpinner.setBackgroundColor(Color.RED);
                    valid = false;
                }
                if(Integer.parseInt(ageText.getText().toString()) > 0 && Integer.parseInt(ageText.getText().toString()) < 120){
                    new_age = Integer.parseInt(ageText.getText().toString());
                    ageText.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    ageText.setBackgroundColor(Color.RED);
                    valid = false;
                }
                if(Integer.parseInt(heightText.getText().toString()) > 50 && Integer.parseInt(heightText.getText().toString()) < 270){
                    new_height = Integer.parseInt(heightText.getText().toString());
                    heightText.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    heightText.setBackgroundColor(Color.RED);
                    valid = false;
                }
                if(Double.parseDouble(weightText.getText().toString()) > 5 && Double.parseDouble(weightText.getText().toString()) < 350){
                    new_weight = Double.parseDouble(weightText.getText().toString());
                    weightText.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    weightText.setBackgroundColor(Color.RED);
                    valid = false;
                }

                if(valid == true){
                    myDb.updateUser(String.valueOf(selectedItem), new_name, new_sex, new_age, new_height, new_weight, 0);
                    Toast.makeText(getApplicationContext(),"User modified", Toast.LENGTH_LONG);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Incorrect data", Toast.LENGTH_LONG);
                }

            }
        });

    }
}