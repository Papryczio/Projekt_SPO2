package com.example.projekt_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SPO2Fragment extends Fragment {

    private TextView textValue;
    int SPO2_value;


    public SPO2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int SPO2_value = 0;
        View rootView = inflater.inflate(R.layout.fragment_s_p_o2, container, false);
        textValue = (TextView) rootView.findViewById(R.id.textView_SPO2_Value);
        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String current_SPO2 = intent.getStringExtra("SPO2");
            if(current_SPO2 != null) {
                SPO2_value = Integer.parseInt(current_SPO2);
                textValue.setText(current_SPO2);
                if (SPO2_value > 96) {
                    textValue.setTextColor(Color.parseColor("#00ff00"));
                } else if (SPO2_value <= 96 && SPO2_value > 93) {
                    textValue.setTextColor(Color.parseColor("#ffff00"));
                } else if (SPO2_value <= 93 && SPO2_value > 89) {
                    textValue.setTextColor(Color.parseColor("#ffad00"));
                } else if (SPO2_value <= 89) {
                    textValue.setTextColor(Color.parseColor("#ff2300"));
                }
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReciever, new IntentFilter(LiveDataService.INTENT_SERVICE_MESSAGE));
        Intent liveData = new Intent(this.getActivity(), LiveDataService.class);
        getActivity().startService(liveData);
    }
}