package uni.leipzig.bm2.bluetoothLE;

import java.util.Vector;

import android.bluetooth.BluetoothDevice;

public class LeDeviceListAdapter {

	private static Vector<BluetoothDevice> bleDevices = new Vector<BluetoothDevice>();
	
	public LeDeviceListAdapter() {
	}
	
	public void notifyDataSetChanged() {
		
	}

	public void addDevice(BluetoothDevice device) {
		bleDevices.add(device);
	}
	
	public Vector<BluetoothDevice> getList () {
		return bleDevices;
	}
}
