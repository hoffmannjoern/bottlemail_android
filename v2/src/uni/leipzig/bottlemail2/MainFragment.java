package uni.leipzig.bottlemail2;

import java.util.ArrayList;

import uni.leipzig.bluetoothLE.BleHandler;
import uni.leipzig.data.Bottle;
import uni.leipzig.data.BottleRack;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Clemens on 23.05.13.
 */
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

//	private WebserviceHandler mWebserviceHandler;
	
	private BleHandler mBleHandler;
	//private BluetoothHandler mBluetoothHandler;

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

//	    TODO
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
		
//		try {
//			BottleReceiver br = new BottleReceiver(this);
//		
//			// Register the BroadcastReceiver
//			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//			getActivity().registerReceiver(br, filter);
//			
//			Toast.makeText(this.getActivity(), "Suche nach Flaschen", Toast.LENGTH_SHORT).show();
//			
//			mBtHandler.startDiscovery();
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}

	public void cancelDiscoveringBottles(){
//		if(mBtHandler != null)
//			mBtHandler.cancelDiscovery();
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
	
//	void checkBluetoothDevice(BluetoothDevice bluetoothDevice){
//		
//		Log.d("checkBluetoothDevice", bluetoothDevice.getAddress());
//		
//		//TODO: �berpr�fen, ob Device eine Flasche ist
//		Bottle bottle = testCreateBottle(mCount, bluetoothDevice.getName());
//		
//		bottle.setMac(bluetoothDevice.getAddress());
//		
//		addBottle(bottle);
//		mCount++;
//	}

//	@Override
//	public void onDestroy() {
//		mBtHandler.cancelDiscovery();
//		super.onDestroy();
//	}
//	
//	public void UpdateBottle(Bottle bottle)
//	{
//	    InsertUpdateBottle(bottle, false);
//	}
//	
//	public void InsertBottle(Bottle bottle)
//	{
//	    InsertUpdateBottle(bottle, true);
//	}
//	
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
//	
//	
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
