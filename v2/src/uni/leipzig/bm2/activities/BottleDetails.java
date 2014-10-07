package uni.leipzig.bm2.activities;

import uni.leipzig.bm2.ble.BluetoothLeService;
import uni.leipzig.bm2.ble.SampleGattAttributes;
import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bottlemail2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class BottleDetails extends Activity {

	private static final boolean DEBUG = BottleMailConfig.BOTTLE_DETAILS_DEBUG;	
    private final static String TAG = BottleDetails.class.getSimpleName();
    
    private ExpandableListView mMessagesList;
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private int mDeviceID;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private boolean isBound = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            if(DEBUG) Log.e(TAG,"+++ ServiceConnection.onServiceConnected +++");
    		
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(DEBUG) Log.e(TAG,"+++ ServiceConnection.onServiceDisconnected +++");
    		
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG) Log.e(TAG,"+++ BroadcastReceiver.onReceive +++");
    		
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

	// If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if(DEBUG) Log.e(TAG,"+++ ExpandableListView.onChildClick +++");
            		
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        Log.d(TAG, characteristic.getService() 
                        		+" | "+ characteristic.getProperties() 
                                +" | "+ characteristic.getUuid() 
                                +" | "+ characteristic.getWriteType());
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };  
    
//	private Context mContext;
//	private LayoutInflater mInflater;

	private boolean isInternetPresent;
	
	private static Bottle mBottle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.e(TAG,"+++ OnCreate +++");
		
		setContentView(R.layout.activity_bottle_details);

		//Get Bottle of intent
		Bundle extras = getIntent().getExtras();
		mBottle = extras.getParcelable(MainActivity.SHOW_BOTTLE_DETAILS);

		mDeviceName = mBottle.getBottleName();
		mDeviceID = mBottle.getBottleID();
		mDeviceAddress = mBottle.getMac();

		// set actionbar titles
		getActionBar().setTitle(mDeviceName);
		getActionBar().setSubtitle(R.string.action_subtitle_compose);
		
		Toast.makeText(this, 
				mDeviceName, Toast.LENGTH_SHORT).show();

		if(mDeviceID >= 0 ) {
			((TextView) findViewById(R.id.tv_bottle_id_value))
			.setText(Integer.valueOf(mDeviceID).toString());
		}
		if(!mDeviceAddress.isEmpty()) {
			((TextView) findViewById(R.id.tv_mac_value))
			.setText(mDeviceAddress);
		}

		assureInternetConnection();

		if( !mDeviceAddress.equals("ca:fe:ca:fe:ca:fe") ) {
			mMessagesList = (ExpandableListView) findViewById(R.id.list_messages);
			mMessagesList.setOnChildClickListener(servicesListClickListner);
	        mConnectionState = (TextView) findViewById(R.id.conn_state);
	        mDataField = (TextView) findViewById(R.id.data_value);

	        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
	        isBound = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		}
		
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(DEBUG) Log.e(TAG,"+++ onResume +++");
		
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(DEBUG) Log.e(TAG,"+++ onPause +++");
		
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(DEBUG) Log.e(TAG,"+++ onDestroy +++");
		
        if (isBound) {
        	unbindService(mServiceConnection);
        	isBound = false;
        }
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(DEBUG) Log.e(TAG,"+++ onCreateOptionsMenu +++");
		
        getMenuInflater().inflate(R.menu.bottle_details, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(DEBUG) Log.e(TAG,"+++ onOptionsItemSelected +++");
		
        switch(item.getItemId()) {
            case R.id.menu_connect:
        		if( !mDeviceAddress.equals("ca:fe:ca:fe:ca:fe") ) {
        			// TODO: that works, but is slow.. why?
        			mBluetoothLeService.connect(mDeviceAddress);
        		}
            	return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearUI() {
        if(DEBUG) Log.e(TAG,"+++ clearUI +++");
		
    	mMessagesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    private void updateConnectionState(final int resourceId) {
        if(DEBUG) Log.e(TAG,"+++ updateConnectionState +++");
		
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if(DEBUG) Log.e(TAG,"+++ displayData +++");
		
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if(DEBUG) Log.e(TAG,"+++ displayGattServices +++");
		
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mMessagesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        if(DEBUG) Log.e(TAG,"+++ makeGattUpdateIntentFilter +++");
		
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
	public void sendMessage(View view) {
        if(DEBUG) Log.e(TAG,"+++ sendMessage +++");
		
		String message = mBottle.getBottleName();
		//TODO
		//connect to bluetooth device
		//send message
		//if sending was okay
			//send message to webservice
		
		//TODO send only to webservice? depends on bottle, that appears on screen
		//need new detail values in Bottle class, 
		//when receiving from bottle (in first communication)
	}
	
	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment 
	public void assureInternetConnection(){
        if(DEBUG) Log.e(TAG,"+++ assureInternetConnection +++");
		
		
		Log.d(TAG, "+++ check internet status +++");
		// get Internet status
		isInternetPresent = isOnline();
		
		if( isInternetPresent ) {
//			mWebserviceHandler = WebserviceHandler.getInstance();
					
		} else {
            // Ask user to connect to Internet
            showAlertDialog(this, 
            		getString(R.string.alert_title_no_internet_conn),
                    getString(R.string.alert_body_no_internet_conn), 
                    false);
		}
	}
	
	/**
	 * check if device is online
	 * @return online status
	 */
	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment 
	public boolean isOnline() {
        if(DEBUG) Log.e(TAG,"+++ isOnline +++");
		
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
    
    /**
     * shows a alert dialog with a title, body, close and accept button
     * @param context
     * @param title
     * @param message
     * @param status
     */
	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment
	public void showAlertDialog(Context context, String title, 
			String message, Boolean status) {
        if(DEBUG) Log.e(TAG,"+++ showAlertDialog +++");
		

		if ( status == true ) {
			
			new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(getString(R.string.alert_btn_okay), 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		} else {
			
			new AlertDialog.Builder(context).setTitle(title)
			.setMessage(message).setPositiveButton(
					getString(R.string.alert_btn_accept), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.startActivity(
							new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton(getString(R.string.alert_btn_close), 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.finish();
				}
			})
			.show();
		}
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        if(DEBUG) Log.e(TAG,"+++ onCreateOptionsMenu +++");
		
		// Inflate the menu; this adds items to the action bar 
		// if it is present.
		getMenuInflater().inflate(R.menu.bottle_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if(DEBUG) Log.e(TAG,"+++ onOptionsItemSelected +++");
		
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
