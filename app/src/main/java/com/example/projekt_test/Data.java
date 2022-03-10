package com.example.projekt_test;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class Data extends ViewModel {
    private MutableLiveData<String> BPM = new MutableLiveData<>();
    private MutableLiveData<String> SPO2 = new MutableLiveData<>();
    private MutableLiveData<String> BTLOG = new MutableLiveData<>();

    public MutableLiveData<String> getBPM() {
        if(BPM == null){
            BPM = new MutableLiveData<>();
        }
        return BPM;
    }

    public MutableLiveData<String> getSPO2() {
        if(SPO2 == null){
            SPO2 = new MutableLiveData<>();
        }
        return SPO2;
    }

    public MutableLiveData<String> getBTLOG() {
        if(BTLOG == null){
            SPO2 = new MutableLiveData<>();
        }
        return BTLOG;
    }

}
