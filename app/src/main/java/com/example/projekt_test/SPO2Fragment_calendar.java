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

import org.w3c.dom.Text;

import java.util.ArrayList;


public class SPO2Fragment_calendar extends Fragment {

    //Log.d TAG
    private static final String TAG = "SPO2Fragment_calendar";

    //GUI
    private LineChart SPO2_chart;
    private PieChart SPO2_pie;

    //database connected
    private DatabaseHelper myDb;
    private String date_ID = "";
    private String user_ID = "";

    //chart connected
    private ArrayList<Entry> dataVals;
    private LineDataSet lineDataSet;
    private ArrayList<ILineDataSet> dataSets;
    private LineData data;

    //pie connected
    private ArrayList<PieEntry> pieEntries;
    private PieData pieData;

    public SPO2Fragment_calendar(String date_ID, String user_ID) {
        this.date_ID = date_ID;
        this.user_ID = user_ID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_s_p_o2_calendar, container, false);

        //GUI
        SPO2_chart = (LineChart) rootView.findViewById(R.id.SPO2_graph_calendar);
        SPO2_pie = (PieChart) rootView.findViewById(R.id.SPO2_pie_calendar);

        //database
        myDb = new DatabaseHelper(getActivity());
        if(!date_ID.equals("")) {
            drawChart(date_ID);
            drawPie(date_ID);
        }

        return rootView;
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