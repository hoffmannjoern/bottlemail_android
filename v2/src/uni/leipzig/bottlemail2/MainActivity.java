package uni.leipzig.bottlemail2;

import java.util.ArrayList;
import java.util.Locale;

import uni.leipzig.bluetoothLE.BleHandler;
import uni.leipzig.data.Bottle;
import uni.leipzig.data.BottleRack;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements TabListener {
	
    private final static String TAG = "MainActivity";
    
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//usePlaceholderFragments(savedInstanceState);
		
        setUpActionBarWithTabs();
		
        Toast.makeText(this, String.valueOf(getString(R.string.toast_activeBluetooth)), Toast.LENGTH_SHORT).show();

	}
	
	private void setUpActionBarWithTabs(){
		// Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        // Create the adapter that will return a fragment for 
        // each of the three primary sections of the app.
        mSectionsPagerAdapter = 
        		new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
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

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
	}
	
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
	//TODO fix or replace and delete this class
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new MainFragmentHereInClass();
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
		
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	

	/**
	 * Created by Clemens on 23.05.13.
	 */
	public class MainFragmentHereInClass extends Fragment {
	
		//TODO just copied from Version1, added only needed stuff
		
		// debugging
		private static final String TAG = "MainFragment";
		
		/**
		 * The fragment argument representing 
		 * the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String ARG_SECTION_TITLE = "section_title";
	
		//private WebserviceHandler mWebserviceHandler;
		
		private BleHandler mBleHandler;
		//private BluetoothHandler mBluetoothHandler;
	
		private ArrayList<Bottle> mBottleValues = new ArrayList<Bottle>();
		private ArrayAdapter<Bottle> mBottleAdapter;
	
		private BottleRack bottleRack = BottleRack.getInstance();
		
		public MainFragmentHereInClass() {
			// TODO Auto-generated constructor stub
		}
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			
			Log.e(TAG,"+++ OnCreate +++");
			
			//mBluetoothHandler = BluetoothHandler.getInstance();
			mBleHandler = BleHandler.getInstance();
			
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, 
				ViewGroup container, Bundle savedInstanceState) {
		
			Log.e(TAG,"+++ OnCreateView +++");
		
		    View rootView = inflater.inflate(
		    		R.layout.fragment_main, container, false);
		
		    final ListView listview = 
		    		(ListView)rootView.findViewById(R.id.listViewMain);
	
		    mBottleAdapter = new ArrayAdapter<Bottle>(getActivity(), 
		    		android.R.layout.simple_list_item_1, mBottleValues);
	
			listview.setAdapter(mBottleAdapter);
			
			Log.d("ARG_SECTION_NUMBER", Integer.toString(getArguments().
					getInt(MainFragment.ARG_SECTION_NUMBER)));
			
			if(getArguments().getInt(MainFragment.ARG_SECTION_NUMBER) == 1) {
				startDiscoveringBottles();
			} else {
				addBottle(testCreateBottle(1));
			}
		
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int i, long l) {
					
					cancelDiscoveringBottles();
		
					Bottle bottle = (Bottle)listview.getItemAtPosition(i);
					// aus string zweite zeile in Integer wandeln           	
					Intent intent = new Intent(getActivity(), BottleDetails.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable("bottle", bottle);
					
					intent.putExtras(bundle);
		
					startActivity(intent);
				}
			});
		
			return rootView;
		}
		
		public void startDiscoveringBottles() {
			
//			try {
//				BottleReceiver br = new BottleReceiver(this);
//			
//				// Register the BroadcastReceiver
//				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//				getActivity().registerReceiver(br, filter);
//				
//				Toast.makeText(this.getActivity(), "Suche nach Flaschen", Toast.LENGTH_SHORT).show();
//				
//				mBleHandler.startDiscovery();
//			
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
		}
	
		public void cancelDiscoveringBottles(){
			if(mBleHandler != null)
				mBleHandler.cancelDiscovery();
		}
		
		private Bottle testCreateBottle(int bottleID){
			
			Bottle bottle = new Bottle(bottleID, "Flasche " + bottleID);
			BottleRack.getInstance().addBottleToRack(bottle);
			return bottle;
		}
		
		private Bottle testCreateBottle(int bottleID, String name){
			
			Bottle bottle = new Bottle(bottleID, name);
			BottleRack.getInstance().addBottleToRack(bottle);
			return bottle;
		}
	
		public void addBottle(Bottle bottle) {
			
			Log.d("addBottle", bottle.getBottleName());
			
			mBottleValues.add(bottle);
			mBottleAdapter.notifyDataSetChanged();
		}
		
		int mCount = 2;
		
		void checkBluetoothDevice(BluetoothDevice bluetoothDevice){
			
			Log.d("checkBluetoothDevice", bluetoothDevice.getAddress());
			
			//TODO: �berpr�fen, ob Device eine Flasche ist
			Bottle bottle = testCreateBottle(mCount, bluetoothDevice.getName());
			
			bottle.setMac(bluetoothDevice.getAddress());
			
			addBottle(bottle);
			mCount++;
		}
	
		@Override
		public void onDestroy() {
			mBleHandler.cancelDiscovery();
			super.onDestroy();
		}
		
		public void UpdateBottle(Bottle bottle)
		{
		    InsertUpdateBottle(bottle, false);
		}
		
		public void InsertBottle(Bottle bottle)
		{
		    InsertUpdateBottle(bottle, true);
		}
		
		private void InsertUpdateBottle(Bottle bottle, boolean insert)
		{
		    ContentValues values = new ContentValues();
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_ID, bottle.getBottleID());
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_MAC, bottle.getMac());
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_NAME, bottle.getBottleName());
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_FOUND, 1);
//		    /*values.put(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR, );
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR, );
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE, );
//		    values.put(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS, );*/
//		
//		    if (insert)
//		        getActivity().getContentResolver().insert(Uri.parse(BottlEMailContentProvider.CONTENT_URI+"/BOTTLE"), values);
//		    else 
//		        getActivity().getContentResolver().update(Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BOTTLEID/" + bottle.getBottleID()), values, null, null);
		    
		}
		
		
//		public SparseArray<Bottle> getFoundBottles()
//		{
//		    Uri uri = Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BOTTLES/FOUND");
//		    
//		    SparseArray<Bottle> resultBmails = new SparseArray<Bottle>();
//		    
//		    String[] projection = { BottlEMailTables.BOTTLES_COLUMN_ID,
//		            BottlEMailTables.BOTTLES_COLUMN_FOUND, BottlEMailTables.BOTTLES_COLUMN_MAC,
//		            BottlEMailTables.BOTTLES_COLUMN_NAME, BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR,
//		            BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR, BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE,
//		            BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS};
//		    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
//		        null);
//		    if (cursor != null)
//		    {
//		        String name = "";
//		        String mac = "";
//		        int bottleID = -1;
//		        int protocol_minor_version = 0;
//		        int protocol_major_version = 0;
//		        int absolute_number_of_msgs = 0;
//		        int absolute_number_of_msgs_from_ws = 0;
//		        
//		        while (cursor.moveToNext())
//		        {
//		            
//		            name = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_NAME));
//		            mac = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_MAC));
//		            protocol_minor_version = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR));
//		            protocol_major_version = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR));
//		            absolute_number_of_msgs = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE));
//		            absolute_number_of_msgs_from_ws = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS));
//		            bottleID = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ID));
//		
//		            resultBmails.append( bottleID, new Bottle( bottleID, name ));
//		        }
//		        cursor.close();
//		        
//		
//		    }
//		    return resultBmails;
//		    
//		}
	
	}

}
