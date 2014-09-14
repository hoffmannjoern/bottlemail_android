package uni.leipzig.bm2.activities;

import java.util.Locale;

import uni.leipzig.bm2.bluetoothLE.LeDeviceListAdapter;
import uni.leipzig.bottlemail2.R;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements TabListener {
	
	private static final boolean DEBUG = true;
	
    private final static String TAG = "MainActivity";
    
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private static final int REQUEST_ENABLE_BT = 666;
	private static final String BLUETOOTH_DEVICE_FOUND = 
			"uni.leipzig.bm2.activities.BLUETOOTH_DEVICE_FOUND";
	
	private static BluetoothAdapter mBluetoothAdapter;
	private static boolean mScanning;
	private static Handler mHandler;
	
	private static LeDeviceListAdapter mLeDeviceListAdapter;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(DEBUG) Log.e(TAG, "+++ onCreate +++");
		
		//usePlaceholderFragments(savedInstanceState);
		
        setUpActionBarWithTabs();
        
        initializeBluetoothAdapter();
 		testBluetoothSupportOfDevice();
 		scanLeDevice(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(DEBUG) Log.e(TAG, "+++ onResume +++");
		
 		scanLeDevice(true);
	}
	
	@Override
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
	}
	
	@Override
	protected void onStop(){
		super.onStop();

		if(DEBUG) Log.e(TAG, "+++ onStop +++");
		
		//TODO: Shut down bluetooth or go to sleep, 
		// when leaving the app... if possible and useful
	}

	private void initializeBluetoothAdapter () {

		if(DEBUG) Log.e(TAG, "+++ initializeBluetoothAdapter +++");
		
        mHandler = new Handler();
        
        // different behaviour since API 18 
 		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
 			// TODO: do it this way, when supporting at least API 18, do we?
 			final BluetoothManager bluetoothManager = 
 					(BluetoothManager) 
 					getSystemService(Context.BLUETOOTH_SERVICE);	
 			mBluetoothAdapter = bluetoothManager.getAdapter();
 		} else {
 	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 		} 	
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
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

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
//           			mLeDeviceListAdapter.addDevice(device);
//           			mLeDeviceListAdapter.notifyDataSetChanged();
           			
           			//send to MainFragment to use in ListView
           			Bundle deviceBundle = new Bundle();
           			deviceBundle.putParcelable(BLUETOOTH_DEVICE_FOUND, device);
           			MainFragment mainFragment = new MainFragment();
           			mainFragment.setArguments(deviceBundle);
               }
           });
       }
    };
	
	private void setUpActionBarWithTabs(){

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
	
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
    }
    

	@SuppressWarnings("unused")
	private void usePlaceholderFragments (Bundle savedInstanceState) {

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

    /**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}	
	

	@Override
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
	}

	@Override
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
		
	}
}
