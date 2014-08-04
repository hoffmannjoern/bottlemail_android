package com.universityofleipzig.bottlemail.bluetooth2;

import java.util.Calendar;

import org.json.JSONException;

import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.data.MessageEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class BluetoothHandler {

	// debugging
	private static final String TAG = "BluetoothHandler";

	private static BluetoothHandler mBluetoothHandler = new BluetoothHandler();

	private BluetoothAdapter mBluetoothAdapter;
	private ConnectThread mConnectThread;
	
	
	private BluetoothHandler(){
		
		Log.e( TAG, "+++ BluetoothHandler +++");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.e( TAG, "The device doesn't support Bluetooth!");	
			return;
		}
		if (!mBluetoothAdapter.isEnabled()){
			//Bluetooth aktivieren
		}
	}
	
	public static BluetoothHandler getInstance() {
		
		Log.e( TAG, "+++ getInstance +++");
		return mBluetoothHandler;
	}
	
	public void startDiscovery() throws Exception {
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.startDiscovery();
		}
		else{
			throw new Exception();
		}
	}
	
	public void cancelDiscovery(){
		if(mBluetoothAdapter != null){
			if(mBluetoothAdapter.isDiscovering())
				mBluetoothAdapter.cancelDiscovery();
		}
	}
	
	private void startConnection(Bottle bottle){
		
		//BluetoothConnection connection = new BluetoothConnection(bottle);
		
		
		
	}
	
	public void asyncGetAllMessages(Bottle bottle, GenericListener<MSGEvent> listener){
		mConnectThread = getConnectThread(bottle, listener);
		
		if(mConnectThread != null)
			mConnectThread.start();
		
		try {
			listener.notify(new MessageEvent(this,new BMail(-1, "Some Text","Tim", Calendar.getInstance(),false)));
		} catch (JSONException e) {
			Log.e("BluetoothHandler", "JSONException:" +e.getMessage());
		}
	}
	
	private ConnectThread getConnectThread(Bottle bottle, GenericListener<MSGEvent> listener){
		
		BluetoothDevice device = null;
		if(bottle.getMac() != null)
			device = mBluetoothAdapter.getRemoteDevice(bottle.getMac());
		if(device == null){
			Log.e("getConnectThread", "Kein BluetoothDevice!!");
			return null;
		}
		
		return new ConnectThread(device, listener);
	}
	
	public void disconnect() {
		if(mConnectThread != null)
			mConnectThread.cancel();
	}
}
