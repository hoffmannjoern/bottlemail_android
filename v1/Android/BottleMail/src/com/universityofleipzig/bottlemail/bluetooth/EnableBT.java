package com.universityofleipzig.bottlemail.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

public class EnableBT {
	private static final boolean debug = true;

	private static final int  REQUEST_ENABLE_BT = 3;
	
	ArrayList<String> mArrayAdapter = new ArrayList<String>();
	

	/**
	 * Look for bluetooth-ability of the device and enable the bluetooth-service if needed
	 * Querying paired devices
	 */
	public void enableBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// entry-point for all bluetooth interaction
		if ( mBluetoothAdapter != null ) {

			// look for enabling-status of bluetooth, if not: start it automatically with user-request
			if ( !mBluetoothAdapter.isEnabled() ) {

				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}

			//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			// look, if the wanted device is already paired
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if ( pairedDevices.size() > 0 ) {

				// Loop through paired devices
				for ( BluetoothDevice device: pairedDevices ) {

					// Add the name and address to an array adapter to show in a ListView
					mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			}
		} else {

			// Device does not support Bluetooth
			if (debug) System.out.println("Das Gerät unterstützt kein Bluetooth.");	
			mArrayAdapter.add("Device does not support Bluetooth");
			Log.e("APPKILLER", "Device does not support Bluetooth");
		}
	}


	private void startActivityForResult(Intent enableBtIntent,
			int requestEnableBt) {
		// TODO Auto-generated method stub
		
	}
}