package com.example.projekt_test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class BT_Service extends Service {
    //log.d tag
    private static final String TAG = "BT_Service";

    //values
    private int BPM = 0;
    private int SPO2 = 0;
    private int BPM_avg[] = new int[10];
    private int BPM_avg_val = -1;
    private int SPO2_avg_val = -1;
    private int SPO2_avg[] = new int[10];
    private String[] temp = new String[4];
    private int BPM_counter = 0;
    private int SPO2_counter = 0;
    private boolean BPM_valid = false;
    private boolean SPO2_valid = false;


    //database related
    private DatabaseHelper db;
    private int User_ID = -1;
    private int Date_ID = -1;

    //bluetooth connection related
    private BluetoothDevice mDevice;
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
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
        //getting extras from mainActivity
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        Log.d(TAG, "Ready");

        //database
        db = new DatabaseHelper(this);
        //checking current user
        Cursor res = db.getIDAndNameOfSelectedUser();
        while (res.moveToNext()) {
            User_ID = Integer.parseInt(res.getString(0));
        }

        //looking for existence of user-date relation -> if exists select / else create
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


        //open bluetooth connection
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new BT_Service.ConnectBT().execute();
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... devices) {
            //Trying to connect to device
            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
            //Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Connection attempt failed", Toast.LENGTH_LONG).show();
            } else {
                mIsBluetoothConnected = true;
                Toast.makeText(getApplicationContext(), "Bluetooth connected", Toast.LENGTH_LONG).show();
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

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private final Thread t;

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
                //for as long as bluetooth is connected
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    //if there is data available
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        //split buffer with new lines
                        temp = strInput.split("\n");
                        if (!(temp[0].trim().equals("0")) && !(temp[0].trim().equals("1"))) {
                            try {
                                Log.d(TAG, "TEMP: SPO2 = " + temp[0] + ", SPO2Valid = " + temp[1] + ", BPM = " + temp[2] + ", BPMValid = " + Integer.parseInt(temp[3].trim()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            //checking if current user is valid
                            Cursor res = db.getIDAndNameOfSelectedUser();
                            int ID = -1;
                            while (res.moveToNext()) {
                                ID = Integer.parseInt(res.getString(0));
                            }
                            if (ID != User_ID) {
                                User_ID = ID;
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
                                BPM_valid = false;
                                SPO2_valid = false;
                                BPM_avg_val = -1;
                                SPO2_avg_val = -1;
                                BPM_counter = 0;
                                SPO2_counter = 0;
                            }

                            //checking if date is valid
                            LocalDate localDate = LocalDate.now();
                            res = db.checkExistenceOfUserAndDate(String.valueOf(User_ID), localDate.toString());
                            if (res == null) {
                                db.insertDate(localDate.toString(), String.valueOf(User_ID));
                                BPM_valid = false;
                                SPO2_valid = false;
                                BPM_avg_val = -1;
                                SPO2_avg_val = -1;
                                BPM_counter = 0;
                                SPO2_counter = 0;
                            }

                            //getting BPM data and averaging 10 valid samples
                            if (temp[3].trim().equals("1")) {
                                try {
                                    BPM = Integer.parseInt(temp[2].trim());
                                    BPM_avg[BPM_counter] = BPM;
                                    if(++BPM_counter == 10){
                                        BPM_valid = true;
                                        int BPM_avg_temp = 0;
                                        for(int j = 0; j < 10; j++){
                                            BPM_avg_temp += BPM_avg[j];
                                        }
                                        BPM_avg_val = BPM_avg_temp/10;
                                        BPM_counter = 0;
                                    }
                                } catch (Exception ex) {
                                    Log.d(TAG, "BPM data invalid");
                                }
                            }
                            //getting SPO2 data and averaging 10 valid samples
                            if (temp[1].trim().equals("1")) {
                                try {
                                    SPO2 = Integer.parseInt(temp[0].trim());
                                    if(SPO2 > 60) {
                                        SPO2_avg[SPO2_counter] = SPO2;
                                        if (++SPO2_counter == 10) {
                                            SPO2_valid = true;
                                            int SPO2_avg_temp = 0;
                                            for (int j = 0; j < 10; j++) {
                                                SPO2_avg_temp += SPO2_avg[j];
                                            }
                                            SPO2_avg_val = SPO2_avg_temp / 10;
                                            SPO2_counter = 0;
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.d(TAG, "SPO2 data invalid");
                                }
                            }

                            //if both BPM and SPO2 averages are valid -> insert it into database
                            if(BPM_valid && SPO2_valid){
                                try {
                                    LocalTime localTime = LocalTime.now();
                                    db.insertData(String.valueOf(Date_ID), localTime.toString(), String.valueOf(BPM_avg_val), String.valueOf(SPO2_avg_val));
                                }
                                catch(Exception ex){
                                    ex.printStackTrace();
                                    Log.d(TAG, "Data insertion into database failed");
                                }
                            }
                        }

                        //starting service that sends live data to fragments
                        Intent liveData = new Intent(getApplicationContext(), LiveDataService.class);
                        liveData.putExtra("BPM", String.valueOf(BPM));
                        liveData.putExtra("SPO2", String.valueOf(SPO2));
                        liveData.putExtra("LOG", strInput);
                        getApplicationContext().startService(liveData);
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


