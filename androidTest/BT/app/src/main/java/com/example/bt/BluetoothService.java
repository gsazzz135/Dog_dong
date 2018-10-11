package com.example.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class BluetoothService {
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    BluetoothService(MainActivity activity, android.os.Handler mHandler){
        mActivity = activity;
        mHandler = mHandler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean getDeviceState(){
        Log.d(TAG, "Check the Bluetooth support");
        if(btAdapter==null){
            Log.d(TAG, "Bluetooth is not available");
            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    public void setRequestEnableBt(){
        Log.i(TAG, "Check the enable Bluetooth");

        if(btAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth is Enable Now");
        } else {
            Log.d(TAG)
        }
    }
}
