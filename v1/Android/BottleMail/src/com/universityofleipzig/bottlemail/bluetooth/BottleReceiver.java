package com.universityofleipzig.bottlemail.bluetooth;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ArrayAdapter;

public final class BottleReceiver extends BroadcastReceiver {

	private ArrayAdapter<String> bottleArray;

	private static final String TAG = "BottleReceiver";

	public BottleReceiver(Context context) {
		super();
		this.bottleArray = new ArrayAdapter<String>(context, 
				android.R.layout.simple_list_item_1, new ArrayList<String>());
		Log.e(TAG, "BottleReceiver");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// When discovery finds a device
		Log.e(TAG, "onReceive().actoin:" +action);
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {

			Log.e(TAG, "onReceive().ACTION_FOUND");

			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			// Add the name and address to an array adapter to show in a
			// ListView
			bottleArray.add(device.getName()
					+ "\n"
					+ device.getAddress());			
			
			// TODO copy to BottleDetail
			// BluetoothService service = new BluetoothService(device);
			// service.start();
		}

	}

	public ArrayAdapter<String> getNearestBootles() {
		return this.bottleArray;
	}
	
}
