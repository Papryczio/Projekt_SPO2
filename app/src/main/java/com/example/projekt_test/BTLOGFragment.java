package com.example.projekt_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BTLOGFragment extends Fragment {

    //region elementy_graficzne
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


        //region GUI
        chkScroll = (CheckBox) rootView.findViewById(R.id.chkScroll);
        mTxtReceive = (TextView)rootView.findViewById(R.id.txtReceive);
        mBtnClearInput = (Button) rootView.findViewById(R.id.btnClearInput);
        BPM_textView = (TextView) rootView.findViewById(R.id.BPM_display);
        SPO2_textView = (TextView) rootView.findViewById(R.id.SPO2_display);
        scrollView = (ScrollView) rootView.findViewById(R.id.viewScroll);
        //endregion

        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());
        mBtnClearInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    mTxtReceive.setText("");
                }
        });
        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String BPM = intent.getStringExtra("BPM");
            if(BPM != null) {
                BPM_textView.setText(BPM);
            }
            String SPO2 = intent.getStringExtra("SPO2");
            if(SPO2 != null) {
                SPO2_textView.setText(SPO2);
            }
            String LOG = intent.getStringExtra("LOG");
            if(LOG != null) {
                Date now = new Date();
                long timestamp = now.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String dateStr = sdf.format(timestamp);
                mTxtReceive.append(dateStr + "\n");
                mTxtReceive.append(LOG);
                if (chkScroll.isChecked()) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        }
    };

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReciever, new IntentFilter(LiveDataService.INTENT_SERVICE_MESSAGE));
        Intent liveData = new Intent(this.getActivity(), LiveDataService.class);
        getActivity().startService(liveData);


    }

}