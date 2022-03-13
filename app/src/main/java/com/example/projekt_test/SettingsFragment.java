package com.example.projekt_test;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    private Button newButton;
    private Button modButton;
    private Button changeButton;
    private TextView currentUser;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

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

            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }
}