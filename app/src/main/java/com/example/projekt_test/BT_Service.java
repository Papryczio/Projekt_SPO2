package com.example.projekt_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class BT_Service extends Service {

    private int BPM = 0;
    private int SPO2 = 0;
    private String[] temp = new String[4];

    private Data data;
    private DatabaseHelper db;

    private int User_ID = -1;
    private int Date_ID = -1;

    private BluetoothDevice mDevice;
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private static final String TAG = "BT_Service";

    private boolean mIsBluetoothConnected = false;
    private ReadInput mReadThread = null;


    public BT_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // create
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        Log.d(TAG, "Ready");

        //data = new ViewModelProvider((ViewModelStoreOwner) intent).get(Data.class);
        db = new DatabaseHelper(this);

        Cursor res = db.getIDAndNameofSelectedUser();
        while (res.moveToNext()) {
            User_ID = Integer.parseInt(res.getString(0));
        }
        LocalDate localDate = LocalDate.now();
        res = db.checkExistenceOfUserAndDate(String.valueOf(User_ID), localDate.toString());
        if (res != null) {
            while (res.moveToNext()) {
                Date_ID = Integer.parseInt(res.getString(0));
            }
        } else {
            db.insertDate(localDate.toString(), String.valueOf(User_ID));
            Date_ID = Integer.parseInt(db.maxDateID());
        }


        // connect


        if (mBTSocket == null || !mIsBluetoothConnected) {
            new BT_Service.ConnectBT().execute();
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }




    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
            } else {
                mIsBluetoothConnected = true;
                Toast.makeText(getApplicationContext(), "Bluetooth connected", Toast.LENGTH_LONG);
                mReadThread = new ReadInput();
            }

        }
    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            Toast.makeText(getApplicationContext(), "Bluetooth disconnected", Toast.LENGTH_LONG);
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");

            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;
            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;

                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        temp = strInput.split("\n");
                        if(!(temp[0].trim().equals("0")) && !(temp[0].trim().equals("1"))) {
                            Log.d(TAG, "TEMP: SPO2 = " + temp[0] + ", SPO2Valid = " + temp[1] + ", BPM = " + temp[2] + ", BPMValid = " + Integer.parseInt(temp[3].trim()));

                            // odczytanie czy ID użytkownika się nie zmieniło
                            Cursor res = db.getIDAndNameofSelectedUser();
                            int ID = -1;
                            while (res.moveToNext()) {
                                ID = Integer.parseInt(res.getString(0));
                            }
                            if(ID != User_ID){
                                User_ID = ID;
                                LocalDate localDate = LocalDate.now();
                                res = db.checkExistenceOfUserAndDate(String.valueOf(User_ID), localDate.toString());
                                if(res != null){
                                    while (res.moveToNext()){
                                        Date_ID = Integer.parseInt(res.getString(0));
                                    }
                                }
                                else{
                                    db.insertDate(localDate.toString(), String.valueOf(User_ID));
                                    Date_ID = Integer.parseInt(db.maxDateID());
                                }
                            }
                            //wpisywanie danych do bazy
                            if (temp[3].trim().equals("1")) {
                                try {
                                    //data.getBPM().postValue(temp[2]);
                                    BPM = Integer.parseInt(temp[2].trim());
                                    Log.d(TAG, "BPM_DISP: " + data.getBPM().getValue());
                                } catch (Exception ex){
                                    Log.d(TAG, "BPM data invalid");
                                }
                            }

                            if (temp[1].trim().equals("1")) {
                                try {
                                    //data.getSPO2().postValue(temp[0]);
                                    SPO2 = Integer.parseInt(temp[0].trim());
                                    Log.d(TAG, "SPO2_DISP" + data.getSPO2().getValue());
                                } catch (Exception ex){
                                    Log.d(TAG, "SPO2 data invalid");
                                }
                            }
                            try{
                                LocalTime localTime = LocalTime.now();
                                db.insertData(String.valueOf(Date_ID), localTime.toString(), String.valueOf(BPM), String.valueOf(SPO2));
                            } catch (Exception ex){
                                Log.d(TAG, "Data insertion failed");
                            }
                        }
                        //data.getBTLOG().postValue(strInput);
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }
}


