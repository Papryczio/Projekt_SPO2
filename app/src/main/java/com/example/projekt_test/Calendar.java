package com.example.projekt_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;

public class Calendar extends AppCompatActivity {

    //Log.d TAG
    private static final String TAG = "Calendar_activity";

    //GUI
    private CalendarView calendarView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private PagerAdapterCalendar pagerAdapterCalendar;
    private String[] tabTitles = {"BPM", "SPO2"};

    //database related
    private DatabaseHelper myDb;
    private String ID = "";
    private String date_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //GUI
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_calendar);
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager_calendar);
        pagerAdapterCalendar = new PagerAdapterCalendar(this, tabLayout.getTabCount(), date_ID, ID);
        viewPager2.setAdapter(pagerAdapterCalendar);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabTitles[position])).attach();

        //database
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getIDAndNameOfSelectedUser();
        while (res.moveToNext()) {
            ID = res.getString(0);
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                LocalDate localDate = LocalDate.of(i,i1+1,i2);
                Log.d(TAG, "Chosen date is: " + localDate);
                Cursor res = myDb.getIDAndNameOfSelectedUser();
                while (res.moveToNext()) {
                    ID = res.getString(0);
                }
                res = myDb.checkExistenceOfUserAndDate(String.valueOf(ID), localDate.toString());
                if (res != null) {
                    while (res.moveToNext()) {
                        date_ID = res.getString(0);
                    }
                    pagerAdapterCalendar = new PagerAdapterCalendar(Calendar.this, tabLayout.getTabCount(), date_ID, ID);
                    viewPager2.setAdapter(pagerAdapterCalendar);
                } else {
                    Toast.makeText(getApplicationContext(),"No data available", Toast.LENGTH_LONG).show();
                    pagerAdapterCalendar = new PagerAdapterCalendar(Calendar.this, tabLayout.getTabCount(), "", "");
                    viewPager2.setAdapter(pagerAdapterCalendar);
                }
            }
        });
    }
}