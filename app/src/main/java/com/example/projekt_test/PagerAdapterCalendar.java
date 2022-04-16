package com.example.projekt_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapterCalendar extends FragmentStateAdapter {

    private int numOfTabs;
    private String date_ID;
    private String user_ID;

    public PagerAdapterCalendar(@NonNull FragmentActivity fragmentActivity, int numOfTabs, String date_ID, String user_ID) {
        super(fragmentActivity);
        this.numOfTabs = numOfTabs;
        this.date_ID = date_ID;
        this.user_ID = user_ID;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BPMFragment_calendar(date_ID, user_ID);
            case 1:
                return new SPO2Fragment_calendar(date_ID, user_ID);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
