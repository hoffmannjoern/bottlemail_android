package uni.leipzig.bm2.activities;

import java.util.ArrayList;

import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
import uni.leipzig.bottlemail2.R;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainFragment extends Fragment {

	//TODO just copied from Version1, added only needed stuff
	
	// debugging
	private static final String TAG = "MainFragment";
	
	/**
	 * The fragment argument representing 
	 * the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_SECTION_TITLE = "section_title";
	
	public static final String SHOW_BOTTLE_DETAILS = 
			"uni.leipzig.bm2.activities.SHOW_BOTTLE_DETAILS";
	
	//private WebserviceHandler mWebserviceHandler;
	
	private ArrayList<Bottle> mBottleValues = new ArrayList<Bottle>();
	private ArrayAdapter<Bottle> mBottleAdapter;

	private BottleRack bottleRack = BottleRack.getInstance();
	
	public MainFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Log.e(TAG,"+++ OnCreate +++");
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
			//TODO listNearBottles, discovering starts onResume of MainActivity
			// found devices should get catched in LeScanCallback and
			// will get here through arguments intent of MainAct. to this
			BluetoothDevice bleDevice = getArguments().
					getParcelable("BLUETOOTH_DEVICE_FOUND");
			checkBluetoothDeviceAndAddIfBottle(bleDevice);
		} else {
			//listKnownBottles
			addBottle(testCreateBottle(0));
			
		}
	
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				
				Bottle bottle = (Bottle)listview.getItemAtPosition(i);
				// aus string zweite zeile in Integer wandeln           	
				Intent intent = new Intent(getActivity(), BottleDetails.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(SHOW_BOTTLE_DETAILS, bottle);
				
				intent.putExtras(bundle);
	
				startActivity(intent);
			}
		});
	
		return rootView;
	}
	
//	int mCount = 2;
	
	void checkBluetoothDeviceAndAddIfBottle(BluetoothDevice bluetoothDevice){
		
		if (bluetoothDevice != null) {
			Log.d("checkBluetoothDevice", bluetoothDevice.getAddress());
			
			//TODO: test if device is a bottle; push the right bottle number
			Bottle bottle = createBottleFromDevice(bluetoothDevice.getName());
	//		Bottle bottle = createBottleFromDevice(
	//				position, bluetoothDevice.getName());
			
			bottle.setMac(bluetoothDevice.getAddress());
			
			addBottle(bottle);
	//		mCount++;
		} else {
			Log.w(TAG, "checkBluetoothDevice got null device");
		}
	}

	public void addBottle(Bottle bottle) {
		
		Log.d("addBottle", bottle.getBottleName());
		
		mBottleValues.add(bottle);
		mBottleAdapter.notifyDataSetChanged();
	}

	private Bottle createBottleFromDevice(String name){
		
		Bottle bottle = new Bottle(mBottleAdapter.getCount()+1, name);
		bottleRack.addBottleToRack(bottle);
		return bottle;
	}

	private Bottle testCreateBottle(int bottleID){
		
		Bottle bottle = new Bottle(bottleID, "Flasche " + bottleID);
		bottleRack.addBottleToRack(bottle);
		return bottle;
	}

//	public void UpdateBottle(Bottle bottle)
//	{
//	    InsertUpdateBottle(bottle, false);
//	}
	
//	public void InsertBottle(Bottle bottle)
//	{
//	    InsertUpdateBottle(bottle, true);
//	}
	
//	private void InsertUpdateBottle(Bottle bottle, boolean insert)
//	{
//	    ContentValues values = new ContentValues();
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_ID, bottle.getBottleID());
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_MAC, bottle.getMac());
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_NAME, bottle.getBottleName());
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_FOUND, 1);
//	    /*values.put(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR, );
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR, );
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE, );
//	    values.put(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS, );*/
//	
//	    if (insert)
//	        getActivity().getContentResolver().insert(Uri.parse(BottlEMailContentProvider.CONTENT_URI+"/BOTTLE"), values);
//	    else 
//	        getActivity().getContentResolver().update(Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BOTTLEID/" + bottle.getBottleID()), values, null, null);
//	    
//	}
	
	
//	public SparseArray<Bottle> getFoundBottles()
//	{
//	    Uri uri = Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BOTTLES/FOUND");
//	    
//	    SparseArray<Bottle> resultBmails = new SparseArray<Bottle>();
//	    
//	    String[] projection = { BottlEMailTables.BOTTLES_COLUMN_ID,
//	            BottlEMailTables.BOTTLES_COLUMN_FOUND, BottlEMailTables.BOTTLES_COLUMN_MAC,
//	            BottlEMailTables.BOTTLES_COLUMN_NAME, BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR,
//	            BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR, BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE,
//	            BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS};
//	    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
//	        null);
//	    if (cursor != null)
//	    {
//	        String name = "";
//	        String mac = "";
//	        int bottleID = -1;
//	        int protocol_minor_version = 0;
//	        int protocol_major_version = 0;
//	        int absolute_number_of_msgs = 0;
//	        int absolute_number_of_msgs_from_ws = 0;
//	        
//	        while (cursor.moveToNext())
//	        {
//	            
//	            name = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_NAME));
//	            mac = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_MAC));
//	            protocol_minor_version = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR));
//	            protocol_major_version = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR));
//	            absolute_number_of_msgs = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE));
//	            absolute_number_of_msgs_from_ws = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS));
//	            bottleID = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BOTTLES_COLUMN_ID));
//	
//	            resultBmails.append( bottleID, new Bottle( bottleID, name ));
//	        }
//	        cursor.close();
//	        
//	
//	    }
//	    return resultBmails;
//	    
//	}

}