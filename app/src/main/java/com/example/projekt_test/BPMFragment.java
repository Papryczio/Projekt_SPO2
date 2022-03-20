package com.example.projekt_test;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;


public class BPMFragment extends Fragment {

    private Data data;
    private TextView textValue;
    private TextView desc;

    private DatabaseHelper myDb;

    private BPMTarget BPM_check;

    public BPMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_p_m, container, false);

        textValue = (TextView)rootView.findViewById(R.id.textView_BPM_Value);
        desc = (TextView)rootView.findViewById(R.id.textView_desc);

        myDb = new DatabaseHelper(getActivity());

        BPM_check = new BPMTarget();

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = new ViewModelProvider(requireActivity()).get(Data.class);
        data.getBPM().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("BPM_fragment", "DATA UPDATE");
                String current_BPM = data.getBPM().getValue();
                current_BPM = current_BPM.replace("\r", "");
                textValue.setText(current_BPM);

                Cursor res = myDb.getIDAndNameofSelectedUser();
                String ID = "null";
                while(res.moveToNext()) {
                    ID = res.getString(0);
                }

                res = myDb.getSingleDataByID(Integer.parseInt(ID));
                int age = -1;
                while(res.moveToNext()) {
                    age = Integer.parseInt(res.getString(3));
                }

                String range = BPM_check.BPM_check(Integer.parseInt(current_BPM), age);
                if(range == "Rest"){
                    textValue.setTextColor(Color.rgb(170, 255, 156));
                    desc.setText(range);
                }
                else if(range == "Low Intensity"){
                    textValue.setTextColor(Color.rgb(0, 202, 209));
                    desc.setText(range);
                }
                else if(range == "Moderate"){
                    textValue.setTextColor(Color.rgb(56, 196, 0));
                    desc.setText(range);
                }
                else if(range == "Aerobic"){
                    textValue.setTextColor(Color.rgb(255, 208, 0));
                    desc.setText(range);
                }
                else if(range == "Vigorous"){
                    textValue.setTextColor(Color.rgb(255, 115, 0));
                    desc.setText(range);
                }
                else if(range == "Max Effort"){
                    textValue.setTextColor(Color.rgb(255, 96, 71));
                    desc.setText(range);
                }
                else if(range == "Over Limit"){
                    textValue.setTextColor(Color.rgb(189, 26, 0));
                    desc.setText(range);
                }
                else{
                    textValue.setTextColor(Color.BLACK);
                    desc.setText("");
                }
            }
        });
    }
}