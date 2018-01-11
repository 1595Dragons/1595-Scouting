package com.example.stephenogden.a1595scoutingapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Stephen Ogden on 5/29/17.
 * FTC 6128 | 7935
 * FRC 1595
 */


public class bluetoothTransmiter extends AppCompatActivity {

    TextView field;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your server's MAC address
    private static String address = Settings.MACADDR;

    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transmitter);

        field = (TextView) findViewById(R.id.Progress);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null) {
            AlertBox("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                field.append("\n...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        ProgressBar progress = (ProgressBar) findViewById(R.id.loadingBar);
        field = (TextView) findViewById(R.id.Progress);
        field.setText("Gathering data (teamNumber)");
        int teamNumber = MainActivity.number;
        progress.setProgress(0);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (hasAuto)");
        boolean hasAuto = scouting.hasAuto;
        progress.setProgress(10);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (autoSwitch)");
        boolean autoSwitch = scouting.autoSwitch;
        progress.setProgress(20);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (autoScale)");
        boolean autoScale = scouting.autoScale;
        progress.setProgress(30);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (teleSwitch)");
        boolean teleSwitch = scouting.teleSwitch;
        progress.setProgress(40);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (teleScale)");
        boolean teleScale = scouting.teleScale;
        progress.setProgress(50);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (cubeNumber)");
        int cubeNumber = scouting.cubeNumber;
        progress.setProgress(60);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (endClimb)");
        boolean endClimb = scouting.endClimb;
        progress.setProgress(70);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        field.setText("Gathering data (endClimbAssist)");
        boolean endClimbAssist = scouting.endClimbAssist;
        progress.setProgress(80);

        field.setText("Connecting to PC...");
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Set up a pointer to the remote node using it's address.
        // Todo: not valid address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            AlertBox("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        try {
            btSocket.connect();
            field.append("\n...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                AlertBox("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }
        progress.setProgress(90);

        field.setText("Compiling data...");
        // Create a data stream so we can talk to server.
        String message = "Hello from Android.\n";
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            AlertBox("Fatal Error", "Output stream creation failed:" + e.getMessage() + ".");
        }
        progress.setProgress(95);

        field.setText("Sending data...");
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) { //Todo: error here
            String msg = "An exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00")) {
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            }
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            AlertBox("Fatal Error", msg);
        }
        progress.setProgress(100);

        // Close the window, and report success


        Button back_btn = (Button) findViewById(R.id.cancel);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bluetoothTransmiter.this, MainActivity.class));
                Toast.makeText(bluetoothTransmiter.this, "Scouting canceled.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    public void AlertBox(String title, String message ){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message + " Press OK to exit." ).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).show();
    }

}
