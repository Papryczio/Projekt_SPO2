package com.example.projekt_test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SPO2Fragment extends Fragment {

    private Data data;
    private TextView textValue;


    public SPO2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_s_p_o2, container, false);
        textValue = (TextView)rootView.findViewById(R.id.textView_SPO2_Value);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = new ViewModelProvider(requireActivity()).get(Data.class);
        data.getSPO2().observe(getViewLifecycleOwner(), new Observer<String>() {

            @Override
            public void onChanged(String s) {
                Log.d("BPM_fragment", "DATA UPDATE");
                textValue.setText(data.getSPO2().getValue());
            }
        });
    }


}