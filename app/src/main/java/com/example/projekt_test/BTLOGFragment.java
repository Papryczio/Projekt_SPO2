package com.example.projekt_test;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.example.projekt_test.databinding.FragmentBTLOGBinding;

public class BTLOGFragment extends Fragment {

    //region elementy
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    private TextView mTxtReceive;
    private TextView BPM_textView;
    private TextView SPO2_textView;
    private Button mBtnClearInput;
    private ScrollView scrollView;
    //endregion

    MonitoringScreen monitoringScreen = new MonitoringScreen();

    public BTLOGFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_t_l_o_g, container, false);

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
/*
            Data BT_data = new Data();

            FragmentBTLOGBinding binding = FragmentBTLOGBinding.inflate(inflater, container, false);
            rootView = binding.getRoot();
            binding.setLifecycleOwner(this);
            binding.setData(BT_data);

        //!kontrola TextView
*/
        return rootView;
    }

}