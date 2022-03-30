package com.example.projekt_test;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LiveDataService extends IntentService {

    public static final String INTENT_SERVICE_MESSAGE = "LIVE_DATA";
    public static final String TAG = "LiveDataService_";

    public LiveDataService() {
        super("LiveDataService");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Intent Service created");
    }

    protected void onHandleIntent(@Nullable Intent intent) {
        String BPM = null, SPO2 = null, LOG = null;
        try {
            BPM = intent.getStringExtra("BPM");
            Log.d(TAG, "BPM: " + BPM);
        } catch(Exception ex) {}
        try {
            SPO2 = intent.getStringExtra("SPO2");
            Log.d(TAG, "SPO2: " + SPO2);
        } catch (Exception ex){}
        try{
            LOG = intent.getStringExtra("LOG");
            Log.d(TAG, "LOG: " + LOG);
        } catch (Exception ex) {}
        sendDataToActivity(BPM, SPO2, LOG);
    }

    private void sendDataToActivity(String BPM, String SPO2, String LOG)
    {
        Intent sendData = new Intent(INTENT_SERVICE_MESSAGE);
        if(BPM != null) {
            sendData.putExtra("BPM", BPM);
        }
        if(SPO2 != null) {
            sendData.putExtra("SPO2", SPO2);
        }
        if(LOG != null) {
            sendData.putExtra("LOG", LOG);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendData);
        Log.d(TAG, "LiveData sent");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: IntentService");

    }


}