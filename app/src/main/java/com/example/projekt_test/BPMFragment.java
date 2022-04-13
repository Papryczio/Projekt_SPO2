package com.example.projekt_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.sql.Array;
import java.util.ArrayList;


public class BPMFragment extends Fragment {

    private static final String TAG = "BPMFragment";

    private TextView textValue;
    private TextView desc;
    private TextView AVGbpm;
    private CircularProgressBar BPMbar;
    private DatabaseHelper myDb;
    private BPMTarget BPM_check;


    private LineChart BPM_chart;
    private ArrayList<Entry> dataVals;
    private LineDataSet lineDataSet;
    private ArrayList <ILineDataSet> dataSets;
    private LineData data;

    private int AVGcounter = 0;

    public BPMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_p_m, container, false);

        textValue = (TextView)rootView.findViewById(R.id.textView_BPM_Value);
        desc = (TextView)rootView.findViewById(R.id.textView_desc);
        BPMbar = (CircularProgressBar)rootView.findViewById(R.id.progressBar_BPM);
        AVGbpm = (TextView)rootView.findViewById(R.id.textView_dailyAVG);
        BPM_chart = (LineChart)rootView.findViewById(R.id.BPM_graph);

        myDb = new DatabaseHelper(getActivity());

        BPM_check = new BPMTarget();


        dataVals = new ArrayList<Entry>();
        String maxID = myDb.maxDateID();
        Cursor res2 = myDb.getAllData_user_date(maxID);
        while (res2.moveToNext()) {
            try {
                dataVals.add(new Entry(Float.valueOf(res2.getString(0)), Float.valueOf(res2.getString(3))));
            }
            catch (Exception ex) {
                Log.d(TAG, "GRAPH exception");
                ex.printStackTrace();
            }
        }
        lineDataSet = new LineDataSet(dataVals, "BPM");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);

        Description description = new Description();
        description.setText("BPM");
        BPM_chart.setDescription(description);

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        data = new LineData(dataSets);
        BPM_chart.setData(data);
        BPM_chart.invalidate();




        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String current_BPM = intent.getStringExtra("BPM");

            Log.d(TAG, "DATA UPDATE");
            if(current_BPM != null) {
                textValue.setText(current_BPM);
                BPMbar.setProgress(Integer.parseInt(current_BPM));

                Cursor res = myDb.getIDAndNameofSelectedUser();
                String ID = "null";
                while (res.moveToNext()) {
                    ID = res.getString(0);
                }

                res = myDb.getUserByID(Integer.parseInt(ID));
                int age = -1;
                while (res.moveToNext()) {
                    age = Integer.parseInt(res.getString(3));
                }

                int BPMmax = BPM_check.BPMlimit(age);
                BPMbar.setProgressMax(BPMmax);

                if(++AVGcounter == 5){
                    Log.d(TAG, "counter tik tok");
                    AVGcounter = 0;
                    int AVGBPM = 0;
                    int dataCounter = 0;
                    int AVGBPMsum = 0;
                    String maxID = myDb.maxDateID();
                    Cursor res2 = myDb.getAllData_user_date(maxID);
                    while(res2.moveToNext()){
                        try {
                            AVGBPMsum += Integer.parseInt(res2.getString(3));
                            Log.d(TAG, "Got data: " + res2.getString(3));
                            dataCounter++;
                        }
                        catch(Exception ex){
                            Log.d(TAG, "Data exception");
                            ex.printStackTrace();
                        }
                    }
                    try {
                        AVGBPM = AVGBPMsum/dataCounter;
                        AVGbpm.setText(String.valueOf(AVGBPM));
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
                    data.addEntry(new Entry(Float.valueOf(res3.getString(0)), Float.valueOf(res3.getString(3))),0);
                }
                BPM_chart.notifyDataSetChanged();
                BPM_chart.moveViewToX(data.getEntryCount());






                String range = BPM_check.BPM_check(Integer.parseInt(current_BPM), age);
                if (range == "Rest") {
                    textValue.setTextColor(Color.rgb(170, 255, 156));
                    BPMbar.setProgressBarColor(Color.rgb(170, 255, 156));
                    desc.setText(range);
                } else if (range == "Low Intensity") {
                    textValue.setTextColor(Color.rgb(0, 202, 209));
                    BPMbar.setProgressBarColor(Color.rgb(0, 202, 209));
                    desc.setText(range);
                } else if (range == "Moderate") {
                    textValue.setTextColor(Color.rgb(56, 196, 0));
                    BPMbar.setProgressBarColor(Color.rgb(56, 196, 0));
                    desc.setText(range);
                } else if (range == "Aerobic") {
                    textValue.setTextColor(Color.rgb(255, 208, 0));
                    BPMbar.setProgressBarColor(Color.rgb(255, 208, 0));
                    desc.setText(range);
                } else if (range == "Vigorous") {
                    textValue.setTextColor(Color.rgb(255, 115, 0));
                    BPMbar.setProgressBarColor(Color.rgb(255, 115, 0));
                    desc.setText(range);
                } else if (range == "Max Effort") {
                    textValue.setTextColor(Color.rgb(255, 96, 71));
                    BPMbar.setProgressBarColor(Color.rgb(255, 96, 71));
                    desc.setText(range);
                } else if (range == "Over Limit") {
                    textValue.setTextColor(Color.rgb(189, 26, 0));
                    BPMbar.setProgressBarColor(Color.rgb(189, 26, 0));
                    desc.setText(range);
                } else {
                    textValue.setTextColor(Color.BLACK);
                    BPMbar.setProgressBarColor(Color.BLACK);
                    desc.setText("");
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