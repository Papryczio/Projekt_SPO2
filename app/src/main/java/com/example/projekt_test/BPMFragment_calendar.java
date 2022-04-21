package com.example.projekt_test;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.sql.Array;
import java.util.ArrayList;

public class BPMFragment_calendar extends Fragment {

    //Log.d TAG
    private static final String TAG = "BPMFragment_calendar";

    //GUI
    private LineChart BPM_chart;
    private PieChart BPM_pie;


    //database connected
    private DatabaseHelper myDb;
    private String date_ID = "";
    private String user_ID = "";
    private int age;

    //chart connected
    private ArrayList<Entry> dataVals;
    private LineDataSet lineDataSet;
    private ArrayList<ILineDataSet> dataSets;
    private LineData data;

    //pie connected
    private ArrayList<PieEntry> pieEntries;
    private PieData pieData;

    //other
    BPMTarget BPM_check;

    public BPMFragment_calendar(String date_ID, String user_ID) {
        this.date_ID = date_ID;
        this.user_ID = user_ID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b_p_m_calendar, container, false);

        //GUI
        BPM_chart = (LineChart) rootView.findViewById(R.id.BPM_graph_calendar);
        BPM_pie = (PieChart) rootView.findViewById(R.id.BPM_pie_calendar);

        //other
        BPM_check = new BPMTarget();

        //database
        myDb = new DatabaseHelper(getActivity());
        if(!user_ID.equals("")) {
            Cursor res = myDb.getUserByID(Integer.parseInt(user_ID));
            while (res.moveToNext()) {
                age = Integer.parseInt(res.getString(3));
            }
            BPM_chart.setVisibility(View.VISIBLE);
            BPM_pie.setVisibility(View.VISIBLE);
            drawChart(date_ID);
            drawPie(date_ID);
        }
        else{
            BPM_chart.setVisibility(View.INVISIBLE);
            BPM_pie.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    //redrawing chart using all data
    private void drawChart(String newID) {
        dataVals = new ArrayList<Entry>();
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

    private void drawEmptyChart(){
        dataVals = new ArrayList<Entry>();
        lineDataSet = new LineDataSet(dataVals, "BPM");
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

    private void drawEmptyPie(){
        pieEntries = new ArrayList<>();
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "BPM");
        pieData = new PieData(pieDataSet);
        BPM_pie.setHoleColor(Color.parseColor("#00000000"));
        BPM_pie.setData(pieData);
        BPM_pie.invalidate();
    }
}