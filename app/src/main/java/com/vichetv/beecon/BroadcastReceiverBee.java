package com.vichetv.beecon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class BroadcastReceiverBee extends BroadcastReceiver {
    protected  MainActivity mainActivity;
    ArrayList<BluetoothDevice> devices= new ArrayList<>();
    public BroadcastReceiverBee(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println(action);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try{

                devices.add(device);
                mainActivity.handleNewBluetoothDevice(this.devices);
                Log.i("In action_found", device.getName());
            }
            catch(Exception e){}
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Log.i("In discovery started", "Discovery started");
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.i("In discovery finished", "Discovery finished");
        }

    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }
}
