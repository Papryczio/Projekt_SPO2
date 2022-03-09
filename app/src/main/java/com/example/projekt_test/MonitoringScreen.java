package com.example.projekt_test;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projekt_test.databinding.FragmentBTLOGBinding;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MonitoringScreen extends FragmentActivity {

    private static final String TAG = "BlueTest5-MainActivity";

    //region BLUETOOTH
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private static final String BT_TAG = "BT_Monitoring";
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;
    private ProgressDialog progressDialog;
    //endregion

    //region page_BT_LOG
    private boolean chkScroll_checked;
    private boolean chkReceiveText_checked;
    Data BT_data = new Data();
    public void setChkScroll_checked(boolean chkScroll_checked) {
        this.chkScroll_checked = chkScroll_checked;
    }

    public void setChkReceiveText_checked(boolean chkReceiveText_checked) {
        this.chkReceiveText_checked = chkReceiveText_checked;
    }
    //endregion

    private FragmentBTLOGBinding binding;

    private ScrollView scrollView;
    private TextView mTxtReceive;
    private List<Number> BPM = new ArrayList<Number>();
    private List<Number> SPO2 = new ArrayList<Number>();
    String[] temp = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_screen);
        ActivityHelper.initialize(this);





        //pages
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem BPM_tab = findViewById(R.id.BPM_tab);
        TabItem SPO2_tab = findViewById(R.id.SPO2_tab);
        TabItem BTLOG_tab = findViewById(R.id.BTLOG_tab);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(this, tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        //!pages

        //region page_BT_LOG
        chkScroll_checked = true;
        chkReceiveText_checked = true;
        //odczytBPM = 0;
        //odczytSPO2 = 0;
        //endregion

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");

        //mTxtReceive.setMovementMethod(new ScrollingMovementMethod());
        mTxtReceive = (TextView)findViewById(R.id.txtReceive);
        scrollView = (ScrollView) findViewById((R.id.viewScroll));

        /*mBtnClearInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });
*/
        Data BT_data = new Data();

       // binding = DataBindingUtil.setContentView(this, R.layout.fragment_b_t_l_o_g);
       // binding.setData(BT_data);
    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        BTLOGFragment btlogFragment = new BTLOGFragment();

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
                        // dzielenie wiadomości na SPO2 i BPM

                        temp = strInput.split("\n");
                        if(!(temp[0].trim().equals("0")) && !(temp[0].trim().equals("1"))) {
                            Log.d(BT_TAG, "TEMP: SPO2 = " + temp[0] + ", SPO2Valid = " + temp[1] + ", BPM = " + temp[2] + ", BPMValid = " + Integer.parseInt(temp[3].trim()));
                            if (temp[3].trim().equals("1")) {
                                BPM.add(Integer.parseInt(temp[2].trim()));
                                BT_data.BPM.set(temp[2].trim());
                                Log.d(BT_TAG, "BPM_DISP: " + temp[2]);
                            }

                            if (temp[1].trim().equals("1")) {
                                SPO2.add(Integer.parseInt(temp[0].trim()));
                                BT_data.SPO2.set(temp[0].trim());
                                Log.d(BT_TAG, "SPO2_DISP" + temp[0]);
                            }
                        }
/*
                        if (chkReceiveText_checked == true) {
                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {

                                    Date now = new Date();
                                    long timestamp = now.getTime();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                    String dateStr = sdf.format(timestamp);
                                    mTxtReceive.append(dateStr + "\n");
                                    mTxtReceive.append(strInput);

                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if(txtLength > mMaxChars){
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }

                                    if (chkScroll_checked == true) { // Scroll only if this is checked
                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                }
                            });
                        }
*/
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
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MonitoringScreen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
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
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }



}