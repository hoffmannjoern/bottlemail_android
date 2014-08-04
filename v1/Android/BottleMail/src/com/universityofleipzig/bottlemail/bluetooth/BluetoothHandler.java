package com.universityofleipzig.bottlemail.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.BottleRack;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class BluetoothHandler {

    // debugging
    private static final String TAG = "BluetoothHandler";
    
	private static BluetoothHandler instance = null;
	BluetoothAdapter mBluetoothAdapter;

	private BluetoothHandler() {
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
//			 Toast.makeText(this, String.valueOf(getString(R.string.toast_noBluetooth)), 
//		        		Toast.LENGTH_SHORT).show();
			Log.e(TAG, "No Bluetooth!");
			System.exit(-1);
		}
	}

	public static BluetoothHandler getInstance() {
		if (instance == null) {
			instance = new BluetoothHandler();
		}
		return instance;
	}
	
	public void discovering() {
		if ( mBluetoothAdapter.isEnabled() ) {
			if ( !mBluetoothAdapter.isDiscovering() )  
				mBluetoothAdapter.startDiscovery();
		}
	}
	
	public boolean isDiscovering () {
		return mBluetoothAdapter.isDiscovering();
	}
	
	public void cancelDiscovering() {
		mBluetoothAdapter.cancelDiscovery();
	}
	
	public ArrayList<String> getNearestBottles() {
		
		ArrayList<String> nearBot = new ArrayList<String>();
		
		/**
		 * TODO Suche nahe Flaschen und adde zu den schon gepaarten, nahen
		 */

		nearBot.addAll(getBoundedBottles());
		
		return nearBot;
	}

	public ArrayList<String> getBoundedBottles() {
		
		Log.d(TAG,"BluetoothHandler.getBoundedBottles");
		
		ArrayList<String> bondBot = new ArrayList<String>();
		Set<BluetoothDevice> pd = mBluetoothAdapter.getBondedDevices();
		if (pd.size() > 0) {

			for (BluetoothDevice bd : pd) {

				// neue Flasche machen
				//id ist doch auf der Flasche hinterlegt, oder?
				//diese sollte als bottleID gespeichert werden
				Bottle newBottle = new Bottle(
						this.convertAddressToInteger(bd.getAddress()),
						bd.getName());
				// flasche hinzufuegen
								
				// sollte vielleicht nicht direkt zu bekannten Flaschen
				// hinzugefuegt werden ?
				//sollst du ja auch gar nicht machen
				//gib die Flasche bitte an den Controller zurueck
				BottleRack.getInstance().addBottleToRack(newBottle);
				// anzeigen
				bondBot.add(bd.getName() + "\n"
						+ bd.getAddress());
			}
		}

		return bondBot;
	}

	public BluetoothDevice getDeviceByAdress(String address) {
		// gets device from address
		return mBluetoothAdapter.getRemoteDevice(address);
	}
	
	@Deprecated
	public int convertAddressToInteger(String address) {
		// da die Addresse als MAC angegeben ist
		// folgende festlegung, ID = Adresse ohne ':'
		try {
			return Integer.valueOf(address.replace(":", ""));
		} catch (NumberFormatException e) {
			Log.e("com.universityofleipzig.bottlemail.bluetooth",
					"BluetoothHandler.convertAddressToInteger:"
							+ e.getMessage());
			return -1;
		}
	}
}
