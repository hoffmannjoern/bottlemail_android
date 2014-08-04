package com.universityofleipzig.bottlemail.bluetooth2;

import java.io.IOException;
import java.util.UUID;

import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectThread extends Thread {

	// debugging
	private static final String TAG = "ConnectThread";
	private final BluetoothSocket mmSocket;

	private ConnectedThread connTh;
	
	private GenericListener<MSGEvent> mGenericListener;

	public ConnectThread(BluetoothDevice device, GenericListener<MSGEvent> listener) {
		Log.d(TAG, "BluetoothService");
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(UUID
					.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		} catch (IOException e) {
			Log.e(TAG, "construct: IOException");
		}
		mmSocket = tmp;
		mGenericListener = listener;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		BluetoothHandler.getInstance().cancelDiscovery();

		try {

			// Connect the device through the socket (pair it). This will block
			// until it succeeds or throws an exception
			Log.e(TAG, "run: mmSocket wants to connect");
			mmSocket.connect();

			Log.e(TAG, "run: mmSocket conntected");

			connTh = new ConnectedThread(mmSocket,mGenericListener);

			connTh.start();
			Log.e(TAG, "run: connTh started");

			try {
				Thread.sleep(5000);
				connTh.getBottleID();
				Log.e(TAG, "run: test write");
			 } catch (InterruptedException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 }
			Log.e(TAG, "run: test write after thread sleep");

		} catch (IOException connectException) {

			// Unable to connect; close the socket and get out
			Log.e(TAG, "run: IOException:" + connectException.getMessage());

			return;
		}

		// Do work to manage the connection (in a separate thread)
		// manageConnectedSocket(mmSocket);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			if (this.connTh != null)
				this.connTh.cancel();
			if (mmSocket != null)
				mmSocket.close();
			Log.d(TAG, "canceled");
		} catch (IOException e) {
			Log.e(TAG, "cancel: IOException");
		}
	}

}
