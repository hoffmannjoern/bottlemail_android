package com.universityofleipzig.bottlemail.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * Aus dem Blutooth Chat Example Der Service sollte eigentlich ein Geraet
 * uebergeben bekommen und dann ueber die Sockets Verbindung aufnehmen. Er wirft
 * aber immer direkt beim Verbindungsaufbau eine Exception.
 * 
 * @author schueller
 * 
 */
public class BluetoothService extends Thread {

	// debugging
	private static final String TAG = "BluetoothService";
	private final BluetoothSocket mmSocket;

	private BluethoothConectedThread connTh;

	public BluetoothService(BluetoothDevice device) {
		Log.d(TAG, "BluetoothService");
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(UUID
					.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//			tmp = device.createRfcommSocketToServiceRecord(device.getUuids()[0]
//					.getUuid());
		} catch (IOException e) {
			Log.e(TAG, "construct: IOException");
		}
		mmSocket = tmp;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		while (BluetoothHandler.getInstance().isDiscovering()) {
			BluetoothHandler.getInstance().cancelDiscovering();
			Log.e(TAG, "run: stop discovering");
		}

		try {

			// Connect the device through the socket (pair it). This will block
			// until it succeeds or throws an exception
			Log.e(TAG, "run: mmSocket wants to connect");
			mmSocket.connect();

			Log.e(TAG, "run: mmSocket conntected");

			connTh = new BluethoothConectedThread(mmSocket);

			connTh.start();
			Log.e(TAG, "run: connTh started");

			try {
				Thread.sleep(5000);
				connTh.testwrite();
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
