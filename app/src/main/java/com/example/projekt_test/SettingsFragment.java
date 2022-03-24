package com.example.projekt_test;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private Button newButton;
    private Button modButton;
    private Button changeButton;
    private TextView currentUserID;
    private TextView currentUserName;

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
        currentUserID = (TextView)rootView.findViewById(R.id.TextView_userID);
        currentUserName = (TextView)rootView.findViewById(R.id.textView_userName);

        try {
            Cursor res = myDb.getIDAndNameofSelectedUser();
            String ID = "null";
            String name = "no user selected";
            while(res.moveToNext()) {
                ID = res.getString(0);
                name = res.getString(1);
            }
            currentUserID.setText(ID);
            currentUserName.setText(name);
            Log.d(TAG, "Current user data updated");
        } catch (Exception ex){
            currentUserID.setText("---");
            currentUserName.setText("No user selected");
            Log.d(TAG, "Current user data update failed (cannot get current user");
        }

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
                Cursor res = myDb.getAllUsers();
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
                Cursor res = myDb.getAllUsers();
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

    public void onResume() {
        try {
            Cursor res = myDb.getIDAndNameofSelectedUser();
            String ID = "null";
            String name = "no user selected";
            while(res.moveToNext()) {
                ID = res.getString(0);
                name = res.getString(1);
            }
            currentUserID.setText(ID);
            currentUserName.setText(name);
            Log.d(TAG, " -- on resume -- Current user data updated");
        } catch (Exception ex){
            currentUserID.setText("---");
            currentUserName.setText("No user selected");
            Log.d(TAG, " -- on resume -- Current user data update failed (cannot get current user)");
        }
        super.onResume();
    }

}