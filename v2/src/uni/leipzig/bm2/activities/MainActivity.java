package uni.leipzig.bm2.activities;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListActivity {//extends ActionBarActivity implements TabListener {
	
	private static final boolean DEBUG = BottleMailConfig.MAIN_ACTIVITY_DEBUG;	
    private final static String TAG = MainActivity.class.getSimpleName();
    
//	private SectionsPagerAdapter mSectionsPagerAdapter;
//	private ViewPager mViewPager;

//	private static final String BLUETOOTH_DEVICE_FOUND = 
//			"uni.leipzig.bm2.activities.BLUETOOTH_DEVICE_FOUND";

    // Memory-Handling
    //TODO: BottleRack is not saved on device... save to hashed or passphrased file (because of clarified geoloations) in memory
    // - Tutorial for android security has an example for hashing with non-readable passphrase (or sth. like this)
    // - Watch for saving files / logging of bottles, for example in xml-file
    private static BottleRack mBottleRack = BottleRack.getInstance();
    
    // Bluetooth-Handling
	private static BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 666;
	private static boolean mScanning;
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
		
		getActionBar().setTitle(R.string.app_name);

//		setContentView(R.layout.activity_main);
//        setUpActionBarWithTabs();
        
        // different behaviour since API 18 
 		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
 			initializeBluetoothAdapterWithManager();
 		else
 	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 				
 		testBluetoothSupportOfDevice();

		// Aquire a reference to the system Location Manager
		initializeLocationManagerandListener();

        mHandler = new Handler();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(DEBUG) Log.e(TAG, "+++ onCreateOptionsMenu +++");
		
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);            	
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        if(DEBUG)
        	menu.findItem(R.id.menu_test).setVisible(true);
        else
        	menu.findItem(R.id.menu_test).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(DEBUG) Log.e(TAG, "+++ onOptionsItemSelected +++");
		
        switch (item.getItemId()) {
            case R.id.menu_scan:
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
            	mScannedBottlesListAdapter.addTestBottleToScannedList();
       			mScannedBottlesListAdapter.notifyDataSetChanged();
            	break;
        }
        return true;
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

        // Initializes list view adapter.
        mScannedBottlesListAdapter = new ScannedBottlesListAdapter();
        setListAdapter(mScannedBottlesListAdapter);
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
		
		if ( !mBluetoothAdapter.isEnabled() ) {
			// Bluetooth is not enabled
			Toast.makeText(this, R.string.toast_bluetooth_not_enabled, 
					Toast.LENGTH_SHORT).show();
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
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
		
		//TODO: Shut down bluetooth or go to sleep, 
		// when leaving the app... if possible and useful
	}*/

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Bottle bottle = mScannedBottlesListAdapter.getBottle(position);
        if (bottle == null) return;
        
        //TODO: set text color to "bottle_known", if bottle is clicked, because was connected minimal one time
        // - actual the getView 
        
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
        } else {
        	if(DEBUG) Log.d("Bottle already in \"Bottle Rack\"", bottle.getMac());
        }        
        
		// aus string zweite zeile in Integer wandeln           	
		Intent intent = new Intent(this, BottleDetails.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(SHOW_BOTTLE_DETAILS, bottle);
		intent.putExtras(bundle);

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
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
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD_BLE);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
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
	
    // Adapter for holding devices found through scanning.
    private class ScannedBottlesListAdapter extends BaseAdapter {
        private final static String TAG = "BottleListAdapter";
        
        private ArrayList<Bottle> mScannedBottles;
        private LayoutInflater mInflator;

        public ScannedBottlesListAdapter() {
            super();
    		if(DEBUG) Log.e(TAG, "+++ BottleListAdapter +++");
    		
            mScannedBottles = new ArrayList<Bottle>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        /**
         * Adds a bluetooth device in the list, if it is not already in it.
         * @param device the bluetooth device to be possibly added
         * @return 1 if added, -1 if failed data, 0 if already in list
         */
        public int addDeviceToScannedListIfIsBottle(BluetoothDevice device) {
        	if (device != null) {
        		
        		for (int i = 0; i < mScannedBottles.size(); i++) {
        			if (mScannedBottles.get(i).getMac().equals(device.getAddress())){
            			Log.e(TAG, "Device already in list!");
        				return 0;
        			}
        		}
        		if(DEBUG) Log.d("checkBluetoothDevice", "Add device: " + device.getAddress());
    			
    			//TODO: test if device is a bottle; push the right bottle number
    			// find out: kind of bottle
    			if ( device.getName() != null) {
    				//TODO: Set further information about bottle here
    				// - DONE setColor
    				// - DONE IN ONITEMCLICK actual GPS information, if given free 
    				// -> TODO: Do we need that earlier?
    				// - get id from webservice or use mac to identify definetly
    				Bottle bottle = new Bottle(
    						mScannedBottles.size()+1, device.getName(), device.getAddress());
    				bottle.setColor(getResources().getColor(R.color.black));
                    mScannedBottles.add(bottle);
                    return 1;
    			} else {
    				Log.w(TAG, "getName method of bluetooth device returned null");
    			}
            } else {
    			Log.w(TAG, "checkBluetoothDevice got null device");
    		}
    		return -1;
        }

        protected void addTestBottleToScannedList() {
        	Bottle bottle = new Bottle(0, "Bottle0");
    		for (int i = 0; i < mScannedBottles.size(); i++) {
    			if (mScannedBottles.get(i).getMac().equals(bottle.getMac())){
        			Log.e(TAG, "Device already in list!");
            		if(DEBUG) Log.e(TAG, "+++ TestBottle already in list +++");
    				return;
    			}
    		}
    		if(DEBUG) Log.e(TAG, "+++ Add TestBottle to ScannedList +++");
        	mScannedBottles.add(bottle);
        }
        
        public Bottle getBottle(int position) {
            return mScannedBottles.get(position);
        }

        public void clear() {
            mScannedBottles.clear();
        }
        
        @Override
        public int getCount() {
            return mScannedBottles.size();
        }

        @Override
        public Object getItem(int i) {
            return mScannedBottles.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_bottle, null);
                viewHolder = new ViewHolder();
                viewHolder.bottleAddress = (TextView) view.findViewById(R.id.bottle_address);
                viewHolder.bottleName = (TextView) view.findViewById(R.id.bottle_name);
                view.setTag(viewHolder);  
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Bottle bottle = mScannedBottles.get(i);
            final String bottleName = bottle.getBottleName();
            if (bottleName != null && bottleName.length() > 0) {
                viewHolder.bottleName.setText(bottleName);
            } else {
                viewHolder.bottleName.setText(R.string.unknown_device);
            }
            // set color of name text to green if known, else leave it black
            if (mBottleRack.bottleIsKnown(bottle.getMac())) {
            	bottle.setColor(getResources().getColor(R.color.green));
            	viewHolder.bottleName.setTextColor(
            			bottle.getColor());
            }
            viewHolder.bottleAddress.setText(bottle.getMac());

            return view;
        }
    }

    static class ViewHolder {
        TextView bottleName;
        TextView bottleAddress;
    }

/*	private void setUpActionBarWithTabs(){

		if(DEBUG) Log.e(TAG, "+++ setUpActionBarWithTabs +++");
	
        final ActionBar actionBar = setUpNaviModeOfSupportActionBar();

        // lower API actionbar support
 		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
 			actionBar.setHomeButtonEnabled(false);
 		}
		
        // Create the adapter that will return a fragment for 
        // each of the three primary sections of the app.
        mSectionsPagerAdapter = 
        		new SectionsPagerAdapter(
        				getSupportFragmentManager());

        mViewPager = setUpAdapterAndSwipeOfViewPager(actionBar, R.id.pager);

        // +++ Training: Creating Swipe Views with Tabs +++
        // Create a tab listener that is called when the user changes tabs.
        // +++ Fabian Todt: +++
        // This is done by the must have implementation of TabListener's
        // onTabSelected, onTabReselected and onTabUnselected
        // Changes have to be done there

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
	}
	
	//set navigation mode to use tabs
	private ActionBar setUpNaviModeOfSupportActionBar () {
		if(DEBUG) Log.e(TAG, "+++ setUpNaviModeOfSupportActionBar +++");
		
        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        return supportActionBar;
	}
	
	private ViewPager setUpAdapterAndSwipeOfViewPager (
			final ActionBar actionBar, int viewPagerId) {
		if(DEBUG) Log.e(TAG, "+++ setUpAdapterAndSwipeOfViewPager +++");
		
		// Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(viewPagerId);
        
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(
        		new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);

            }
        });
        
        mViewPager.setAdapter(mSectionsPagerAdapter);       
        return mViewPager;
	}
	
    *//**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     *//*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	private final static String TAG = "SectionsPagerAdapter";
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
    		if(DEBUG) Log.e(TAG, "+++ Constructor +++");
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putInt(MainFragment.ARG_SECTION_NUMBER, position + 1);
            args.putString(MainFragment.ARG_SECTION_TITLE, 
            		getPageTitle(position).toString());
            fragment.setArguments(args);
            return fragment;
        }
        
        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }*/

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar 
		// if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	/*@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
        // When the tab is selected, switch to the
        // corresponding page in the ViewPager.
        mViewPager.setCurrentItem(arg0.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}*/
}
