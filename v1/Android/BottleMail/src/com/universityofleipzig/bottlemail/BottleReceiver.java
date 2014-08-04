package com.universityofleipzig.bottlemail;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BottleReceiver extends BroadcastReceiver {
	
	public BottleReceiver(MainFragment mainFragment){
		mMainFragment = mainFragment;
	}
	
	private MainFragment mMainFragment; 
	
	public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView

            Log.d("Receive", device.getAddress());
            mMainFragment.checkBluetoothDevice(device);
        }
    }
}
