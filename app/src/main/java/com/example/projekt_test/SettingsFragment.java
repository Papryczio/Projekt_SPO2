package com.example.projekt_test;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private Button newButton;
    private Button modButton;
    private Button changeButton;
    private TextView currentUser;

    private DatabaseHelper myDb;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        myDb = new DatabaseHelper(getActivity());

        newButton = (Button)rootView.findViewById(R.id.addButton);
        modButton = (Button)rootView.findViewById(R.id.modButton);
        changeButton = (Button)rootView.findViewById(R.id.changeButton);
        currentUser = (TextView)rootView.findViewById(R.id.UserIDTextView);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getActivity(), addNewUser.class);
                startActivity(newIntent);
            }
        });

        modButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = myDb.getAllData();
                if(res.getCount() == 0){
                    Toast.makeText(getContext(), "There are no users added yet", Toast.LENGTH_LONG).show();
                }else{
                    Intent newIntent = new Intent(getActivity(), ModifyUser.class);
                    startActivity(newIntent);
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = myDb.getAllData();
                if(res.getCount() == 0){
                    Toast.makeText(getContext(), "There are no users added yet", Toast.LENGTH_LONG).show();
                }else{
                    Intent newIntent = new Intent(getActivity(), ChangeUser.class);
                    startActivity(newIntent);
                }
            }
        });

        return rootView;
    }
}