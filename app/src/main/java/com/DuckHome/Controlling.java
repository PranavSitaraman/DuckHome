package com.DuckHome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Controlling extends Activity {
    final static String on = "92";//on
    final static String off = "79";//off
    private static final String TAG = "BlueTest5-Controlling";
    TextView name;
    TextView message;
    Button btnSubmit;
    SeekBar sensor;
    TextView sensorvalue;
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            sensorvalue.setText("" + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
    private final int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private final ReadInput mReadThread = null;
    private final boolean mIsUserInitiatedDisconnect = false;
    private final boolean mIsBluetoothConnected = false;
    // Global Variables for the Message and Name
    private EditText editText;
    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);
        ActivityHelper.initialize(this);

        ArrayList<Integer> sensors = new ArrayList<Integer>(Arrays.asList(10, 20, 30));
        ArrayList<Integer> actuators = new ArrayList<Integer>(Arrays.asList(15, 25, 35));

        ListView sensorlist = findViewById(R.id.sensorlist);
        ListView actuatorlist = findViewById(R.id.actuatorlist);
        sensorlist.setAdapter(new SeekBarAdapter(this, R.layout.seekbar_list_item, sensors));
        actuatorlist.setAdapter(new SeekBarAdapter(this, R.layout.seekbar_list_item, actuators));

        /*
        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        sensor.setOnSeekBarChangeListener(seekBarChangeListener);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        //Log.d(TAG, "Ready");




        btnSubmit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {

                // Get input fields from view
                 name = (TextView) findViewById(R.id.nameInput);
                message = (TextView) findViewById(R.id.messageInput);
                // Pull Strings from fields
                final String nameString = name.getText().toString();
                final String messageString = message.getText().toString();

                // Strings to Bytes
                byte[] nameByte = nameString.getBytes();
                byte[] messageByte = messageString.getBytes();


                // Combine two Byte arrays (two fields) into one
                final byte[] allBytes = new byte[nameByte.length + messageByte.length];
                System.arraycopy(nameByte, 0, allBytes, 0, nameByte.length);
                System.arraycopy(messageByte, 0, allBytes, nameByte.length, messageByte.length);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messageString.length() > 0 && nameString.length() >0) {
                            //scaledrone.publish("observable-room", message);
                            try {


                                mBTSocket.getOutputStream().write(allBytes); //Send Message over BLE
                                //Log.w("debugggg",messageString + nameString);
                                msg("Message Send");


                                // Clear Text Fields
                                name.setText(null);
                                message.setText(null);



                            } catch (IOException e) {
                                e.printStackTrace();

                            };
                        } else{
                                    msg("Message or name field is empty");

                        }
                    }
                }, 2000); // Millisecond 1000 = 1 sec





            }
        });
*/


    }

    /*

        private class DisConnectBT extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... params) {//cant inderstand these dotss

                if (mReadThread != null) {
                    mReadThread.stop();
                    while (mReadThread.isRunning())
                        ; // Wait until it stops
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

     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
