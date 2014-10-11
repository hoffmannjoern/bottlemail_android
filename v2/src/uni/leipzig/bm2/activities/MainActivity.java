package uni.leipzig.bm2.activities;

import java.util.Calendar;

import uni.leipzig.bm2.adapter.ScannedBottlesListAdapter;
import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
import uni.leipzig.bottlemail2.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ListActivity {
	
	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
    private final static String TAG = MainActivity.class.getSimpleName();
    
    private static MenuItem scanItem = null;

    // Memory-Handling
    //TODO: BottleRack is not saved on device... save to hashed or passphrased file (because of clarified geoloations) in memory
    // - Tutorial for android security has an example for hashing with non-readable passphrase (or sth. like this)
    // - Watch for saving files / logging of bottles, for example in xml-file
    private static BottleRack mBottleRack = BottleRack.getInstance();
    
    // Bluetooth-Handling
	private static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 666;
	private static Handler mHandler;

	private static ScannedBottlesListAdapter mScannedBottlesListAdapter;
	
	// Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD_BLE = 10000;
    
    // Location-Handling
    private static LocationManager mLocationManager;
    private static LocationListener mLocationListener;
    private static final long SCAN_PERIOD_LOC = 30000;
    private static final long SCAN_RANGE_LOC = 50;
    
    
	public static final String SHOW_BOTTLE_DETAILS = 
			"uni.leipzig.bm2.activities.SHOW_BOTTLE_DETAILS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.e(TAG, "+++ onCreate +++");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);
        
		getActionBar().setTitle(R.string.app_name);

        mHandler = new Handler();
        // Initializes list view adapter.
        mScannedBottlesListAdapter = new ScannedBottlesListAdapter(this, getResources(), mBottleRack);
        setListAdapter(mScannedBottlesListAdapter);
//		setContentView(R.layout.activity_main);
        
        // different behaviour since API 18 
 		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
 			initializeBluetoothAdapterWithManager();
 		else
 	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 				
 		testBluetoothSupportOfDevice();

		// Aquire a reference to the system Location Manager
		initializeLocationManagerandListener();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(DEBUG) Log.e(TAG, "+++ onResume +++");
		
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, R.string.toast_bluetooth_not_enabled, 
					Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice(true);
		
        //TODO: Decide: Do we want a GPS location, or fits a network only location
		// What do we expect?
		// To register for both just request location update types both (see location strategies tutorial)
		// For both we need the ACCESS_FINE_LOCATION permission, for network we only need ACCESS_COARSE_LOCATION
		// Register the listener with the Location Manager to receive location updates
		// Register for Network-Provider Updates
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				SCAN_PERIOD_LOC, 
				SCAN_RANGE_LOC, 
				mLocationListener);
		// Register for GPS-Provider Updates
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
//				SCAN_PERIOD_LOC, SCAN_RANGE_LOC, mLocationListener);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(DEBUG) Log.e(TAG, "+++ onActivityResult +++");
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

/*	@Override
	protected void onStart(){
		super.onStart();
		if(DEBUG) Log.e(TAG, "+++ onStart +++");
	}*/

    @Override
    protected void onPause() {
        super.onPause();
		if(DEBUG) Log.e(TAG, "+++ onPause +++");
		
        scanLeDevice(false);
        mScannedBottlesListAdapter.clear();
        mLocationManager.removeUpdates(mLocationListener);
    }

/*	@Override
	protected void onStop(){
		super.onStop();
		if(DEBUG) Log.e(TAG, "+++ onStop +++");
	}*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(DEBUG) Log.e(TAG, "+++ onCreateOptionsMenu +++");
		
        getMenuInflater().inflate(R.menu.main, menu);
        scanItem = menu.findItem(R.id.menu_scan);
        menu.findItem(R.id.menu_stop).setVisible(true);
        
        if(DEBUG) {
        	menu.findItem(R.id.menu_test).setVisible(true);
        	menu.findItem(R.id.menu_createBottle).setVisible(true);
        } else {
        	menu.findItem(R.id.menu_test).setVisible(false);
        	menu.findItem(R.id.menu_createBottle).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(DEBUG) Log.e(TAG, "+++ onOptionsItemSelected +++");

        switch (item.getItemId()) {
            case R.id.menu_scan:
            	item.setVisible(false);
                mScannedBottlesListAdapter.clear();
                scanLeDevice(true);
        		mLocationManager.requestLocationUpdates(
        				LocationManager.NETWORK_PROVIDER, 
        				SCAN_PERIOD_LOC, 
        				SCAN_RANGE_LOC, 
        				mLocationListener);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                // TODO: stop or remove indeterminate_progress icon
                //menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
                mLocationManager.removeUpdates(mLocationListener);
                break;
            case R.id.menu_test:
        		if(DEBUG) Log.e(TAG, "+++ GenerateTestBottle+++");
            	mScannedBottlesListAdapter.addTestBottleToScannedList("ca:fe:ca:fe:ca:fe");
       			mScannedBottlesListAdapter.notifyDataSetChanged();
            	break;
        }
        return true;
    }

	private void initializeLocationManagerandListener() {
		if(DEBUG) Log.e(TAG, "+++ initializeLocationManagerandListener +++");
		
		// TODO: Defining model for best performance (see tutorial)
		mLocationManager = (LocationManager) 
				this.getSystemService(Context.LOCATION_SERVICE);
		
		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if(DEBUG) Log.e(TAG, "+++ onLocationChanged +++");
				// TODO Auto-generated method stub	
				// Called when a new location is found by provider at speicified time and range
				// makeUseOfNewLocation(location)
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}
		};
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void initializeBluetoothAdapterWithManager () {
		if(DEBUG) Log.e(TAG, "+++ initializeBluetoothAdapterWithManager +++");
		
		// TODO: do it this way, when supporting at least API 18, do we?
		final BluetoothManager bluetoothManager = 
				(BluetoothManager) 
				getSystemService(Context.BLUETOOTH_SERVICE);	
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	private void testBluetoothSupportOfDevice () {
		if(DEBUG) Log.e(TAG, "+++ testBluetoothSupportOfDevice +++");
		
		if(mBluetoothAdapter == null) {
			// Device does not support bluetooth
			// TODO: put in a dialog window here!
			Toast.makeText(this, R.string.toast_bluetooth_not_supported, 
					Toast.LENGTH_LONG).show();
			finish();
		} else if(!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			// BLE is not supported by device
			// TODO: put in a dialog window here!
			Toast.makeText(this, R.string.toast_ble_not_supported, 
					Toast.LENGTH_LONG).show();
			finish();	
		} 
	}

    private void scanLeDevice(final boolean enable) {

		if(DEBUG) Log.e(TAG, "+++ scanLeDevice +++");
		
        if (enable) {
			Toast.makeText(this, R.string.toast_scanning_ble, 
					Toast.LENGTH_SHORT).show();
			
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    setLoadIconByHidingScanButtonIfTrue(false);
                }
            }, SCAN_PERIOD_BLE);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
            setLoadIconByHidingScanButtonIfTrue(true);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            setLoadIconByHidingScanButtonIfTrue(false);
        }
    }
    
    private void setLoadIconByHidingScanButtonIfTrue(boolean setLoad) {
    	if (scanItem != null)
    		scanItem.setVisible(!setLoad);
    	setProgressBarIndeterminateVisibility(setLoad);
    }
    
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                byte[] scanRecord) {
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
           			if(DEBUG) Log.e(TAG, "+++ DeviceFoundInLeScanCallback +++");
        		
           			//TODO what works? what is better?
           			//send to external data structure 
           			mScannedBottlesListAdapter.addDeviceToScannedListIfIsBottle(device);
           			mScannedBottlesListAdapter.notifyDataSetChanged();
               }
            });

//			List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);
//			if (records.size() == 0) {
//			    Log.i(TAG, "Scan Record Empty");
//			} else {
//			    Log.i(TAG, "Scan Record: "
//			            + TextUtils.join(",", records));
//			}
       }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		if(DEBUG) Log.e(TAG, "+++ onListItemClick +++");

		mBluetoothAdapter.stopLeScan(mLeScanCallback);
        
		final Bottle bottle = mScannedBottlesListAdapter.getBottle(position);
		if (bottle == null) return;

		//TODO: set text color to "bottle_known", if bottle is clicked, because was connected minimal one time
        // - actual the getView 
        bottle.setColor(getResources().getColor(R.color.green));
		mScannedBottlesListAdapter.notifyDataSetChanged();
        
        // Set location to Bottleobject
        // TODO: To use both: find the best choice of both, here we need more brain, than it should take.. 
        // maybe decide for only one provider
        if (mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null) {
        	bottle.setGeoLocation(mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        } else { 
        	Log.e(TAG, "Network Provider knows nothing!");
        }
//        if (mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null)
//        	bottle.setGeoLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//        else 
//        	Log.e(TAG, "GPS Provider knows nothing!");
        
        // add bottle to bottlerack, if it is not there 
        if( (mBottleRack.addBottleToRack(bottle)) == true) {
        	if(DEBUG) Log.d("Bottle added to \"Bottle Rack\"", bottle.getMac());
    		// Set found timestamp to NOW -> the moment of clicking first
    		bottle.setFoundDate(Calendar.getInstance().toString());
    		//TODO: set text color to "bottle_known", if bottle is clicked, because was connected minimal one time
            // - actual the getView 
            bottle.setColor(getResources().getColor(R.color.green));
    		mScannedBottlesListAdapter.notifyDataSetChanged();
        } else {
        	if(DEBUG) Log.d("Bottle already in \"Bottle Rack\"", bottle.getMac());
        }        
        
		// aus string zweite zeile in Integer wandeln           	
		Intent intent = new Intent(this, BottleDetails.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(SHOW_BOTTLE_DETAILS, bottle);
		intent.putExtras(bundle);

        startActivity(intent);
    }

}
