package com.DuckHome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Controlling extends Activity {
    final static String on = "92";//on
    final static String off = "79";//off
    private static final String TAG = "BlueTest5-Controlling";
    private final boolean mIsUserInitiatedDisconnect = false;
    ArrayList<Integer> sensors;
    ArrayList<String> sensor_titles;
    ArrayList<Integer> actuators;
    ArrayList<String> actuator_titles;
    ListView sensorlist;
    ListView actuatorlist;
    SeekBarAdapter sensors_adapt;
    SeekBarAdapter actuators_adapt;
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);
        ActivityHelper.initialize(this);

        sensors = new ArrayList<Integer>(Arrays.asList(10, 20, 30));
        sensor_titles = new ArrayList<>(Arrays.asList("First", "Second", "Third"));
        actuators = new ArrayList<Integer>(Arrays.asList(15, 25, 35));
        actuator_titles = new ArrayList<>(Arrays.asList("Fourth", "Fifth", "Sixth"));

        sensorlist = findViewById(R.id.sensorlist);
        actuatorlist = findViewById(R.id.actuatorlist);
        sensors_adapt = new SeekBarAdapter(this, R.layout.seekbar_list_item, sensors, sensor_titles, this, false);
        sensorlist.setAdapter(sensors_adapt);
        actuators_adapt = new SeekBarAdapter(this, R.layout.seekbar_list_item, actuators, actuator_titles, this, true);
        actuatorlist.setAdapter(actuators_adapt);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        //Log.d(TAG, "Ready");


    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        //Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        //Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        //Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sendBT(String nameString, String messageString) {
        byte[] nameByte = nameString.getBytes();
        byte[] messageByte = messageString.getBytes();

        // Combine two Byte arrays (two fields) into one
        final byte[] allBytes = new byte[nameByte.length + messageByte.length];
        System.arraycopy(nameByte, 0, allBytes, 0, nameByte.length);
        System.arraycopy(messageByte, 0, allBytes, nameByte.length, messageByte.length);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (messageString.length() > 0 && nameString.length() > 0) {
                    //scaledrone.publish("observable-room", message);
                    try {


                        mBTSocket.getOutputStream().write(allBytes); //Send Message over BLE
                        //Log.w("debugggg",messageString + nameString);
                        msg("Message Send");


                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else {
                    msg("Message or name field is empty");

                }
            }
        }, 2000); // Millisecond 1000 = 1 sec
    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning()) ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {

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

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }

    private class ReadInput implements Runnable {

        private final Thread t;
        private boolean bStop = false;

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
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */
                        String[] splited = strInput.split(" ");
                        if (splited.length == 2) {
                            int position = sensor_titles.indexOf(splited[0]);
                            sensors_adapt.data.set(position, Integer.parseInt(splited[1]));
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    sensors_adapt.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }
}
