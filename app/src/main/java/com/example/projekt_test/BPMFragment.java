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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class BPMFragment extends Fragment {
    //Log.d TAG
    private static final String TAG = "BPMFragment";

    //GUI elements
    private TextView textValue;
    private TextView desc;
    private TextView AVGbpm;
    private CircularProgressBar BPMbar;
    private BPMTarget BPM_check;
    private LineChart BPM_chart;
    private PieChart BPM_pie;

    //database connected
    private DatabaseHelper myDb;

    //chart connected
    private ArrayList<Entry> dataVals;
    private LineDataSet lineDataSet;
    private ArrayList<ILineDataSet> dataSets;
    private LineData data;

    //pie connected
    private ArrayList<PieEntry> pieEntries;
    private PieData pieData;

    //other functions and variables
    private int AVGcounter = 0;
    private int pieCounter = 0;
    private String ID = "";
    private String newestID = "";
    private int age = -1;

    public BPMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_p_m, container, false);

        //GUI elements
        textValue = (TextView) rootView.findViewById(R.id.textView_BPM_Value);
        desc = (TextView) rootView.findViewById(R.id.textView_desc);
        BPMbar = (CircularProgressBar) rootView.findViewById(R.id.progressBar_BPM);
        AVGbpm = (TextView) rootView.findViewById(R.id.textView_dailyAVG);
        BPM_chart = (LineChart) rootView.findViewById(R.id.BPM_graph);
        BPM_pie = (PieChart)rootView.findViewById(R.id.BPM_pie);

        //database connected
        myDb = new DatabaseHelper(getActivity());
        Cursor res = myDb.getIDAndNameOfSelectedUser();
        while (res.moveToNext()) {
            ID = res.getString(0);
        }
        newestID = myDb.getNewestDateForSelectedUser(ID);
        res = myDb.getUserByID(Integer.parseInt(ID));
        while (res.moveToNext()) {
            age = Integer.parseInt(res.getString(3));
        }

        //other functions and variables
        BPM_check = new BPMTarget();

        //chart connected
        drawChart(newestID);

        //pie connected
        drawPie(newestID);



        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String current_BPM = intent.getStringExtra("BPM");
            Log.d(TAG, "Data received");

            //if received data is not null proceed on calculations and GUI updates
            if (current_BPM != null) {
                textValue.setText(current_BPM);
                BPMbar.setProgress(Integer.parseInt(current_BPM));

                //checking currently chosen user
                Cursor res = myDb.getIDAndNameOfSelectedUser();
                while (res.moveToNext()) {
                    String newID = res.getString(0);
                    if(!(ID.equals(newID))){
                        ID = res.getString(0);
                        newestID = myDb.getNewestDateForSelectedUser(ID);
                        drawChart(newestID);
                        drawPie(newestID);
                    }
                }
                //getting age of selected user
                res = myDb.getUserByID(Integer.parseInt(ID));
                while (res.moveToNext()) {
                    age = Integer.parseInt(res.getString(3));
                }

                //checking max BPM for the user and updating progressBar
                int BPMmax = BPM_check.BPMlimit(age);
                BPMbar.setProgressMax(BPMmax);

                //calculating daily average of BPM every 5 new data entries
                if (++AVGcounter == 5) {
                    AVGcounter = 0;
                    int AVGBPM = 0;
                    int dataCounter = 0;
                    int AVGBPMsum = 0;
                    Cursor res2 = myDb.getAllData_user_date(newestID);
                    while (res2.moveToNext()) {
                        try {
                            AVGBPMsum += Integer.parseInt(res2.getString(3));
                            Log.d(TAG, "Got data: " + res2.getString(3));
                            dataCounter++;
                        } catch (Exception ex) {
                            Log.d(TAG, "Data exception");
                            ex.printStackTrace();
                        }
                    }
                    try {
                        AVGBPM = AVGBPMsum / dataCounter;
                        AVGbpm.setText(String.valueOf(AVGBPM));
                        Log.d(TAG, "Average BPM updated");
                    } catch (Exception ex) {
                        Log.d(TAG, "no data to average");
                        ex.printStackTrace();
                    }
                }

                if(++pieCounter == 20){
                    pieCounter = 0;
                    drawPie(newestID);
                }

                //appending chart with newly received data
                String max_data_ID = myDb.maxDataIDatDate(newestID);
                Cursor res3 = myDb.getDataatID(max_data_ID);
                while (res3.moveToNext()) {
                    data.addEntry(new Entry(Float.valueOf(res3.getString(0)), Float.valueOf(res3.getString(3))), 0);
                }
                BPM_chart.notifyDataSetChanged();
                BPM_chart.moveViewToX(data.getEntryCount());

                //checking for BPM range and updating GUI
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

    //liveData handler
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReciever, new IntentFilter(LiveDataService.INTENT_SERVICE_MESSAGE));
        Intent liveData = new Intent(this.getActivity(), LiveDataService.class);
        getActivity().startService(liveData);

    }

    //redrawing chart using all data
    private void drawChart(String newID) {
        dataVals = new ArrayList<Entry>();
        //String maxID = myDb.maxDateID();
        Cursor res2 = myDb.getAllData_user_date(newID);
        while (res2.moveToNext()) {
            try {
                dataVals.add(new Entry(Float.valueOf(res2.getString(0)), Float.valueOf(res2.getString(3))));
            } catch (Exception ex) {
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
    }

    private void drawPie(String newID){
        pieEntries = new ArrayList<>();
        BPM_pie.setUsePercentValues(true);
        BPM_pie.setDrawRoundedSlices(true);
        BPM_pie.setDrawEntryLabels(false);
        BPM_pie.setHoleColor(Color.parseColor("#00000000"));
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(170, 255, 156));
        colors.add(Color.rgb(0, 202, 209));
        colors.add(Color.rgb(56, 196, 0));
        colors.add(Color.rgb(255, 208, 0));
        colors.add(Color.rgb(255, 115, 0));
        colors.add(Color.rgb(255, 96, 71));
        colors.add(Color.rgb(189, 26, 0));

        Cursor res = myDb.getAllData_user_date(newID);
        int rest = 0, low = 0, mod = 0, aero = 0, vig = 0, max = 0, over = 0;
        while(res.moveToNext()){
            int BPM = Integer.parseInt(res.getString(3));
            String range = BPM_check.BPM_check(BPM, age);
            if (range == "Rest") {
                rest++;
            } else if (range == "Low Intensity") {
                low++;
            } else if (range == "Moderate") {
                mod++;
            } else if (range == "Aerobic") {
                aero++;
            } else if (range == "Vigorous") {
                vig++;
            } else if (range == "Max Effort") {
                max++;
            } else if (range == "Over Limit") {
                over++;
            }
        }
        pieEntries.add(new PieEntry(rest, "Rest"));
        pieEntries.add(new PieEntry(low, "Low Intensity"));
        pieEntries.add(new PieEntry(mod, "Moderate"));
        pieEntries.add(new PieEntry(aero, "Aerobic"));
        pieEntries.add(new PieEntry(vig, "Vigorous"));
        pieEntries.add(new PieEntry(max, "Max Effort"));
        pieEntries.add(new PieEntry(over, "Over Limit"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "BPM");
        pieDataSet.setColors(colors);
        pieData = new PieData(pieDataSet);
        BPM_pie.setData(pieData);
        BPM_pie.invalidate();
    }
}