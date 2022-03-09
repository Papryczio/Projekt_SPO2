package com.example.projekt_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    private int numOfTabs;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, int numOfTabs){
        super(fragmentActivity);
        this.numOfTabs = numOfTabs;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new BPMFragment();
            case 1:
                return new SPO2Fragment();
            case 2:
                return new BTLOGFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }

}
