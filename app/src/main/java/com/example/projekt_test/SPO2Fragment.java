package com.example.projekt_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class SPO2Fragment extends Fragment {

    private static final String TAG = "SPO2Fragment";

    private TextView textValue;
    private TextView AVGspo2;
    private CircularProgressBar SPO2bar;


    private int SPO2_value;
    private int AVGcounter;

    private DatabaseHelper myDb;

    private LineChart SPO2_chart;
    private ArrayList<Entry> dataVals;
    private LineDataSet lineDataSet;
    private ArrayList <ILineDataSet> dataSets;
    private LineData data;



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
        AVGspo2 = (TextView)rootView.findViewById(R.id.textView_AVGSPO2);
        SPO2bar = (CircularProgressBar)rootView.findViewById(R.id.progressBar_SPO2);
        SPO2_chart = (LineChart)rootView.findViewById(R.id.SPO2_graph);
        myDb = new DatabaseHelper(getActivity());
        SPO2bar.setProgressMax(100);

        dataVals = new ArrayList<Entry>();
        String maxID = myDb.maxDateID();
        Cursor res2 = myDb.getAllData_user_date(maxID);
        while (res2.moveToNext()) {
            try {
                dataVals.add(new Entry(Float.valueOf(res2.getString(0)), Float.valueOf(res2.getString(4))));
            }
            catch (Exception ex) {
                Log.d(TAG, "GRAPH exception");
                ex.printStackTrace();
            }
        }
        lineDataSet = new LineDataSet(dataVals, "SPO2");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);

        Description description = new Description();
        description.setText("SPO2");
        SPO2_chart.setDescription(description);

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        data = new LineData(dataSets);
        SPO2_chart.setData(data);
        SPO2_chart.invalidate();

        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String current_SPO2 = intent.getStringExtra("SPO2");
            if(current_SPO2 != null) {
                SPO2_value = Integer.parseInt(current_SPO2);

                if(++AVGcounter == 5){
                    Log.d(TAG, "counter tik tok");
                    AVGcounter = 0;
                    int AVGSPO2 = 0;
                    int dataCounter = 0;
                    int AVGSPO2sum = 0;
                    String maxID = myDb.maxDateID();
                    Cursor res2 = myDb.getAllData_user_date(maxID);
                    while(res2.moveToNext()){
                        try {
                            AVGSPO2sum += Integer.parseInt(res2.getString(4));
                            Log.d(TAG, "Got data: " + res2.getString(4));
                            dataCounter++;
                        }
                        catch(Exception ex){
                            Log.d(TAG, "Data exception");
                            ex.printStackTrace();
                        }
                    }
                    try {
                        AVGSPO2 = AVGSPO2sum/dataCounter;
                        AVGspo2.setText(String.valueOf(AVGSPO2));
                    }
                    catch(Exception ex){
                        Log.d(TAG, "none data to average");
                        ex.printStackTrace();
                    }
                }

                String max_date_ID = myDb.maxDateID();
                String max_data_ID = myDb.maxDataIDatDate(max_date_ID);
                Cursor res3 = myDb.getDataatID(max_data_ID);
                while (res3.moveToNext()) {
                    data.addEntry(new Entry(Float.valueOf(res3.getString(0)), Float.valueOf(res3.getString(4))),0);
                }
                SPO2_chart.notifyDataSetChanged();
                SPO2_chart.moveViewToX(data.getEntryCount());


                if(SPO2_value > 60) {
                    textValue.setText(current_SPO2);
                    SPO2bar.setProgress(SPO2_value);

                    if (SPO2_value > 96) {
                        textValue.setTextColor(Color.parseColor("#00ff00"));
                        SPO2bar.setProgressBarColor(Color.parseColor("#00ff00"));
                    } else if (SPO2_value <= 96 && SPO2_value > 93) {
                        textValue.setTextColor(Color.parseColor("#ffff00"));
                        SPO2bar.setProgressBarColor(Color.parseColor("#ffff00"));
                    } else if (SPO2_value <= 93 && SPO2_value > 89) {
                        textValue.setTextColor(Color.parseColor("#ffad00"));
                        SPO2bar.setProgressBarColor(Color.parseColor("#ffad00"));
                    } else if (SPO2_value <= 89) {
                        textValue.setTextColor(Color.parseColor("#ff2300"));
                        SPO2bar.setProgressBarColor(Color.parseColor("#ff2300"));
                    }
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