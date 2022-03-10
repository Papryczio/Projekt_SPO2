package com.example.projekt_test;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class BTLOGFragment extends Fragment {

    //region elementy_graficzne
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    private TextView mTxtReceive;
    private TextView BPM_textView;
    private TextView SPO2_textView;
    private Button mBtnClearInput;
    private ScrollView scrollView;
    //endregion_graficz

    MonitoringScreen monitoringScreen = new MonitoringScreen();
    private Data data;

    public BTLOGFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_t_l_o_g, container, false);
        data = new Data();
        //kontrola checkBoxów
            chkScroll = (CheckBox) rootView.findViewById(R.id.chkScroll);
            chkReceiveText = (CheckBox) rootView.findViewById(R.id.chkReceiveText);

            chkReceiveText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        monitoringScreen.setChkReceiveText_checked(isChecked);
                }
            });

            chkScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        monitoringScreen.setChkScroll_checked(isChecked);
                }
            });
        //!kontrola checkBoxów

        //kontrola TextView
            mTxtReceive = (TextView)rootView.findViewById(R.id.txtReceive);
            mBtnClearInput = (Button) rootView.findViewById(R.id.btnClearInput);
            BPM_textView = (TextView) rootView.findViewById(R.id.BPM_display);
            SPO2_textView = (TextView) rootView.findViewById(R.id.SPO2_display);

            mTxtReceive.setMovementMethod(new ScrollingMovementMethod());
            mBtnClearInput.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mTxtReceive.setText("");
                }
            });

        data = new ViewModelProvider(requireActivity()).get(Data.class);
        data.getBPM().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("BTLOG_fragment", "BPM DATA UPDATE");
                BPM_textView.setText(data.getBPM().getValue());
            }
        });

        data.getSPO2().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("BTLOG_fragment", "SPO2 DATA UPDATE");
                SPO2_textView.setText(data.getSPO2().getValue());
            }
        });

        return rootView;
    }
}