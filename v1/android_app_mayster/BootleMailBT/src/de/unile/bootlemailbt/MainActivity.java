package de.unile.bootlemailbt;

import java.util.ArrayList;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	ArrayList<String> mArrayAdapter = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button clsBtn = (Button) this.findViewById(R.id.btnClose);
		clsBtn.setOnClickListener(new BTMBTOnClickListner());
		//mArrayAdapter.add("Dummy Device");
		ListView listview = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mArrayAdapter);
		listview.setAdapter(adapter);
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			Set<BluetoothDevice> pd = mBluetoothAdapter.getBondedDevices();
			if (pd.size() >0) {
				for (BluetoothDevice bd: pd) {
					mArrayAdapter.add(bd.getName() + "\n" + bd.getAddress());
				}
			}
		
		} else {
			mArrayAdapter.add("Device does not support Bluetooth");
			Log.e("APPKILLER", "Device does not support Bluetooth");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("APPDO", "unregister BT");
	}
	
	

}
