package uni.leipzig.bm2.activities;

import uni.leipzig.bm2.adapter.ScannedBottlesListAdapter;
import uni.leipzig.bm2.config.AppUtilities;
import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
import uni.leipzig.bm2.data.DataTransformer;
import uni.leipzig.bm2.webservice.CreateNewBottleAsync;
import uni.leipzig.bottlemail2.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) 
public class MainActivity extends ListActivity {
	
	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
    private final static String TAG = MainActivity.class.getSimpleName();
    
    // used to invert the visibility of scan icon always, when progress intermediate is toggled
    private static MenuItem scanItem = null;
    
    // permanent memory holder of app data like preferences
    private SharedPreferences mSPreferences;

    // Memory-Handling
    //TODO: BottleRack is not saved on device... save to hashed or passphrased file (because of clarified geoloations) in memory
    // - Tutorial for android security has an example for hashing with non-readable passphrase (or sth. like this)
    // - Watch for saving files / logging of bottles, for example in xml-file
    private static BottleRack mBottleRack = BottleRack.getInstance();

    // Network-Service Handling
    private static ConnectivityManager mConnManager;
    private static final int REQUEST_ENABLE_NW = 10;
    
    // Bluetooth-Handling
	private static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 11;
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

		mSPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();

        // initialize connectivity manager for handling network
		mConnManager = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		
		setupActivityGuiOnCreate();
        
		initializeListAdapterWithScannedBottlesOnCreate();
        
 		initializeBluetoothAdapterOnCreate();
 		testBluetoothSupportOfDeviceOnCreate();

		// Aquire a reference to the system Location Manager
		initializeLocationManagerAndListenerOnCreate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(DEBUG) Log.e(TAG, "+++ onResume +++");

		//TODO: test if any combination of not-availabilities of ble and network harm app performance
		// Ensures network connection is enabled on the device.
		initializeInternetConnection();
		
		ensureBluetoothIsEnabledOnResume();
        scanLeDevice(true);
		
        requestLocationUpdatesOnResume();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if(DEBUG) Log.e(TAG, "+++ onActivityResult +++");
		
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if(requestCode == REQUEST_ENABLE_NW && resultCode == Activity.RESULT_CANCELED) {
        	finish();
        	return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
		if(DEBUG) Log.e(TAG, "+++ onPause +++");
		
        scanLeDevice(false);
        mScannedBottlesListAdapter.clear();
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(DEBUG) Log.e(TAG, "+++ onCreateOptionsMenu +++");
        getMenuInflater().inflate(R.menu.main, menu);

        scanItem = menu.findItem(R.id.menu_scan);
        updateVisibilityOfMenuItems(menu);
        return true;
    }
    
    private void updateVisibilityOfMenuItems(Menu menu) {
	    menu.findItem(R.id.menu_stop).setVisible(true);
	    
	    if(DEBUG) {
	    	menu.findItem(R.id.menu_test).setVisible(true);
	    	menu.findItem(R.id.menu_createBottle).setVisible(true);
	    } else {
	    	menu.findItem(R.id.menu_test).setVisible(false);
	    	menu.findItem(R.id.menu_createBottle).setVisible(false);
	    }
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
        				SCAN_PERIOD_LOC, SCAN_RANGE_LOC, mLocationListener);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                mLocationManager.removeUpdates(mLocationListener);
                break;
            case R.id.menu_test:
        		if(DEBUG) Log.e(TAG, "+++ GenerateTestBottleForListOnly+++");
            	mScannedBottlesListAdapter.addTestBottleToScannedList("ca:fe:ca:fe:ca:fe");
       			mScannedBottlesListAdapter.notifyDataSetChanged();
            	break;
            case R.id.menu_createBottle:
        		if(DEBUG) Log.e(TAG, "+++ GenerateTestBottleForWebservice+++");
        		//start an async task to send created test bottle to webservice
            	new CreateNewBottleAsync(getApplicationContext())
            		.execute(AppUtilities.getInstance().getServerBottleUrl());
            	break;
            case R.id.menu_settings:
            	startActivity(new Intent(this, SettingsActivity.class));
            	break;
        }
        return true;
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(DEBUG) Log.e(TAG, "+++ onPreferenceChange +++");
		// TODO Auto-generated method stub
		// Does actual nothing, because the app gets the precision, when setting geolocation
		return true;
	}

	private void setupActivityGuiOnCreate() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminate(true);
		getActionBar().setTitle(R.string.app_name);
	}
	
	private void initializeListAdapterWithScannedBottlesOnCreate() {
        mScannedBottlesListAdapter = 
        	new ScannedBottlesListAdapter(this, getResources(), mBottleRack);
        setListAdapter(mScannedBottlesListAdapter);
	}
	
	private void initializeLocationManagerAndListenerOnCreate() {
		if(DEBUG) Log.e(TAG, "+++ initializeLocationManagerandListenerOnCreate +++");
		
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

	private void requestLocationUpdatesOnResume() {
	    //TODO: Decide: Do we want a GPS location, or fits a network only location
		// What do we expect?
		// To register for both just request location update types both (see location strategies tutorial)
		// For both we need the ACCESS_FINE_LOCATION permission, for network we only need ACCESS_COARSE_LOCATION
		// Register the listener with the Location Manager to receive location updates
		// Register for Network-Provider Updates
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				SCAN_PERIOD_LOC, SCAN_RANGE_LOC, mLocationListener);
		// Register for GPS-Provider Updates
	//	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
	//			SCAN_PERIOD_LOC, SCAN_RANGE_LOC, mLocationListener);
	}
	
	private void initializeInternetConnection () {
		if(DEBUG) Log.e(TAG, "+++ initializeInternetConnection +++");

		if(mConnManager.getActiveNetworkInfo() == null || 
				!mConnManager.getActiveNetworkInfo().isConnected()) {
			Toast.makeText(this, 
					R.string.toast_ws_not_enabled, Toast.LENGTH_LONG).show();
			Intent enableNetWork = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			startActivity(enableNetWork);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void initializeBluetoothAdapterOnCreate() {
		if(DEBUG) Log.e(TAG, "+++ initializeBluetoothAdapterOnCreate +++");

        // different behaviour since API 18 
 		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
 			// TODO: do it this way, when supporting at least API 18, do we?
 			final BluetoothManager bluetoothManager = 
 					(BluetoothManager) 
 					getSystemService(Context.BLUETOOTH_SERVICE);	
 			mBluetoothAdapter = bluetoothManager.getAdapter();
 		} else
 	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	private void testBluetoothSupportOfDeviceOnCreate() {
		if(DEBUG) Log.e(TAG, "+++ testBluetoothSupportOfDeviceOnCreate +++");
		
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
	
	private void ensureBluetoothIsEnabledOnResume() {
        // If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
	    if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, R.string.toast_bluetooth_not_enabled, 
					Toast.LENGTH_SHORT).show();
	        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
       }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		if(DEBUG) Log.e(TAG, "+++ onListItemClick +++");

		mBluetoothAdapter.stopLeScan(mLeScanCallback);
        
		final Bottle bottle = mScannedBottlesListAdapter.getBottle(position);
		if (bottle == null) 
			return;
		else 
			updateBottleAndRack(bottle);
        
        showDetailsOfClickedBottle(bottle);
    }

    private void showDetailsOfClickedBottle(Bottle bottle) {
		Intent intent = new Intent(this, BottleDetails.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(SHOW_BOTTLE_DETAILS, bottle);
		intent.putExtras(bundle);
	
	    startActivity(intent);
    }

	private void updateBottleAndRack(Bottle bottle) {
		setGeoLocationWithActualPrecision(bottle);
		
		if( (mBottleRack.addBottleToRack(bottle)) == true) {
			if(DEBUG) Log.d("Bottle added to \"Bottle Rack\"", bottle.getMac());
			
			// Set found timestamp to NOW -> the moment of clicking first
			bottle.setFoundDate(DataTransformer.getUnixTimestampWithCurrentTime());
			if(DEBUG) Log.d(TAG, bottle.getFoundDate());
			
		    bottle.setColor(getResources().getColor(R.color.green));
			
		    mScannedBottlesListAdapter.notifyDataSetChanged();
		} else {
			if(DEBUG) Log.d("Bottle already in \"Bottle Rack\"", bottle.getMac());
		}    
	}

    private void setGeoLocationWithActualPrecision (Bottle bottle) {
		if(DEBUG) Log.e(TAG, "+++ setGeoLocationWithActualPrecision +++");
		
		// DONE: precision problem on example "Sternburg Brauerei Leipzig"
		// 51.330308,12.400882 -> 6 after point -> extremely exact
		// 51.33,12.40 -> near right street "Mühlstraße"
		// 51.3,12.4 -> city ("Leipzig/Lössnig")
		// 51.0,12.0 -> region (in corner between "Zeitz, Gera, Eisenberg")
		// 50.0,10.0 -> right country -> "Germany - Würzburg/FFM"
		// useful options for precision settings should be:
		// country (10.0 precise), region (11.0 precise), city (11.1 pr.), street(11.11 pr.), exactly (11.111111 pr.)
		int geoPrecision = getGeoPrecisionBySharedPrefs();
    	Log.e(TAG, "Set geo location with precision: " + geoPrecision);

        // Set location to Bottle-object
        // TODO: To use both: find the best choice of both, here we need more brain, than it should take.. 
        // maybe decide for only one provider
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
        	if(DEBUG) Log.e(TAG, "Set location from GPS");
        	bottle.setGeoLocation(location, geoPrecision);
        } else {
        	location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);        
        	if (location != null) {
        		if(DEBUG) Log.e(TAG, "Set location from network");
            	bottle.setGeoLocation(location, geoPrecision);
        	} else { 
        		if(DEBUG) Log.e(TAG, "Unknown location");
        	}
        }
    }

	private int getGeoPrecisionBySharedPrefs() { 
		return Integer.valueOf(mSPreferences.getString("precision_preference", 
			getResources().getString(R.string.invisible)));
	}
}
