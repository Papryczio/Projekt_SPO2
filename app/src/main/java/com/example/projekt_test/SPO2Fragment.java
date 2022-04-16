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
import android.widget.Button;
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

public class SPO2Fragment extends Fragment {
    //Log.d TAG
    private static final String TAG = "SPO2Fragment";

    //GUI elements
    private TextView textValue;
    private TextView SPO2_avg;
    private CircularProgressBar SPO2_bar;
    private LineChart SPO2_chart;
    private PieChart SPO2_pie;
    private Button calendar;

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
    private int SPO2_value = 0;
    private int counter_avg = 0;
    private int counter_pie = 0;
    private String ID = "";
    private String newestID = "";

    public SPO2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_s_p_o2, container, false);

        //GUI elements
        textValue = (TextView) rootView.findViewById(R.id.textView_SPO2_Value);
        SPO2_avg = (TextView) rootView.findViewById(R.id.textView_AVGSPO2);
        SPO2_bar = (CircularProgressBar) rootView.findViewById(R.id.progressBar_SPO2);
        SPO2_chart = (LineChart) rootView.findViewById(R.id.SPO2_graph_calendar);
        SPO2_pie = (PieChart) rootView.findViewById(R.id.SPO2_pie_calendar);
        calendar = (Button) rootView.findViewById(R.id.button_calendar_2);
        SPO2_bar.setProgressMax(100);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Calendar.class);
                startActivity(intent);
            }
        });

        //database connected
        myDb = new DatabaseHelper(getActivity());
        Cursor res = myDb.getIDAndNameOfSelectedUser();
        while (res.moveToNext()) {
            ID = res.getString(0);
        }
        newestID = myDb.getNewestDateForSelectedUser(ID);

        //chart connected
        drawChart(newestID);

        //pie connected
        drawPie(newestID);

        //other functions and variables

        return rootView;
    }

    private BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String current_SPO2 = intent.getStringExtra("SPO2");
            Log.d(TAG, "Data received");

            //if received data is not null proceed on calculations and GUI updates
            if (current_SPO2 != null) {
                SPO2_value = Integer.parseInt(current_SPO2);
                textValue.setText(current_SPO2);
                SPO2_bar.setProgress(SPO2_value);

                //checking currently chosen user
                Cursor res = myDb.getIDAndNameOfSelectedUser();
                while(res.moveToNext()){
                    String newID = res.getString(0);
                    if(!(ID.equals(newID))){
                        ID = res.getString(0);
                        newestID = myDb.getNewestDateForSelectedUser(ID);
                        drawChart(newestID);
                        drawPie(newestID);
                    }
                }
                newestID = myDb.getNewestDateForSelectedUser(ID);

                //calculating daily average of SPO2 every 5 new data entries
                if (++counter_avg == 5) {
                    counter_avg = 0;
                    int AVGSPO2 = 0;
                    int dataCounter = 0;
                    int AVGSPO2sum = 0;
                    Cursor res2 = myDb.getAllData_user_date(newestID);
                    while (res2.moveToNext()) {
                        try {
                            AVGSPO2sum += Integer.parseInt(res2.getString(4));
                            Log.d(TAG, "Got data: " + res2.getString(4));
                            dataCounter++;
                        } catch (Exception ex) {
                            Log.d(TAG, "Data exception");
                            ex.printStackTrace();
                        }
                    }
                    try {
                        AVGSPO2 = AVGSPO2sum / dataCounter;
                        SPO2_avg.setText(String.valueOf(AVGSPO2));
                    } catch (Exception ex) {
                        Log.d(TAG, "none data to average");
                        ex.printStackTrace();
                    }
                }

                if(++counter_pie == 20) {
                    counter_pie = 0;
                    drawPie(newestID);
                }


                //appending chart with newly received data
                String max_data_ID = myDb.maxDataIDatDate(newestID);
                Cursor res3 = myDb.getDataatID(max_data_ID);
                while (res3.moveToNext()) {
                    data.addEntry(new Entry(Float.valueOf(res3.getString(0)), Float.valueOf(res3.getString(4))), 0);
                }
                SPO2_chart.notifyDataSetChanged();
                SPO2_chart.moveViewToX(data.getEntryCount());

                //checking for SPO2 range and updating GUI
                if (SPO2_value > 96) {
                    textValue.setTextColor(Color.parseColor("#00ff00"));
                    SPO2_bar.setProgressBarColor(Color.parseColor("#00ff00"));
                } else if (SPO2_value <= 96 && SPO2_value > 93) {
                    textValue.setTextColor(Color.parseColor("#ffff00"));
                    SPO2_bar.setProgressBarColor(Color.parseColor("#ffff00"));
                } else if (SPO2_value <= 93 && SPO2_value > 89) {
                    textValue.setTextColor(Color.parseColor("#ffad00"));
                    SPO2_bar.setProgressBarColor(Color.parseColor("#ffad00"));
                } else if (SPO2_value <= 89) {
                    textValue.setTextColor(Color.parseColor("#ff2300"));
                    SPO2_bar.setProgressBarColor(Color.parseColor("#ff2300"));
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
        Cursor res2 = myDb.getAllData_user_date(newID);
        while (res2.moveToNext()) {
            try {
                dataVals.add(new Entry(Float.valueOf(res2.getString(0)), Float.valueOf(res2.getString(4))));
            } catch (Exception ex) {
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
    }

    private void drawPie(String newID){
        pieEntries = new ArrayList<>();
        SPO2_pie.setUsePercentValues(true);
        SPO2_pie.setDrawEntryLabels(false);
        SPO2_pie.setDrawRoundedSlices(true);
        SPO2_pie.setHoleColor(Color.parseColor("#00000000"));
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#00ff00"));
        colors.add(Color.parseColor("#ffff00"));
        colors.add(Color.parseColor("#ffad00"));
        colors.add(Color.parseColor("#ff2300"));

        Cursor res = myDb.getAllData_user_date(newID);
        int greenRange = 0, yellowRange = 0, orangeRange = 0, redRange = 0;
        while (res.moveToNext()) {
            int SPO2 = Integer.parseInt(res.getString(4));
            if (SPO2 > 96) {
                greenRange++;
            } else if (SPO2 <= 96 && SPO2 > 93) {
                yellowRange++;
            } else if (SPO2 <= 93 && SPO2 > 89) {
                orangeRange++;
            } else if (SPO2 <= 89) {
                redRange++;
            }
        }
        pieEntries.add(new PieEntry(greenRange, "Excellent"));
        pieEntries.add(new PieEntry(yellowRange, "Good"));
        pieEntries.add(new PieEntry(orangeRange, "Bad"));
        pieEntries.add(new PieEntry(redRange, "Horrible"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"SPO2");
        pieDataSet.setColors(colors);
        pieData = new PieData(pieDataSet);
        SPO2_pie.setData(pieData);
        SPO2_pie.invalidate();
    }
}