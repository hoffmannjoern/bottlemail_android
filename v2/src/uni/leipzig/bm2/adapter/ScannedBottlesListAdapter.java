package uni.leipzig.bm2.adapter;

import java.util.ArrayList;

import uni.leipzig.bm2.activities.BottleDetails;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
import uni.leipzig.bottlemail2.R;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// Adapter for holding devices found through scanning.
public class ScannedBottlesListAdapter extends BaseAdapter {
	
	private static final boolean DEBUG = true;	
    private final static String TAG = BottleDetails.class.getSimpleName();

    private ArrayList<Bottle> mScannedBottles;
    private LayoutInflater mInflator;
    private Resources mResources;
    private BottleRack mBottleRack;

    public ScannedBottlesListAdapter(Context context, Resources resources, BottleRack bottleRack) {
        super();
		if(DEBUG) Log.e(TAG, "+++ BottleListAdapter +++");

		//TODO: Maybe replace bottlerack with db-access
        mBottleRack = bottleRack;
        mScannedBottles = new ArrayList<Bottle>();
        // Override list of scanned bottles with known
        // FIXME: Crashes, when returning from real bottle details to main_acti
        // Test bottle is no problem.
        // Fix that later when doing the memory part
//        if (!mBottleRack.isEmpty()) {
//	        for (int i=0; i < mBottleRack.getNumberOfBottles(); i++) {
//	        	mScannedBottles.add(mBottleRack.getBottle(i));
//	        }
//        }
        mInflator = LayoutInflater.from(context);
        mResources = resources;
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
				bottle.setColor(mResources.getColor(R.color.black));
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

    public void addTestBottleToScannedList() {
    	Bottle bottle = new Bottle(0, "Bottle0");
		for (int i = 0; i < mScannedBottles.size(); i++) {
			if (mScannedBottles.get(i).getMac().equals(bottle.getMac())){
    			Log.e(TAG, "Device already in list!");
        		if(DEBUG) Log.e(TAG, "+++ TestBottle already in list +++");
				return;
			}
		}
		if(DEBUG) Log.e(TAG, "+++ Add TestBottle to ScannedList +++");
		bottle.setColor(mResources.getColor(R.color.black));
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
            view = mInflator.inflate(R.layout.listitem_scanned_bottle, viewGroup, false);
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
        	bottle.setColor(mResources.getColor(R.color.green));
        	viewHolder.bottleName.setTextColor(
        			bottle.getColor());
        }
        viewHolder.bottleAddress.setText(bottle.getMac());

        return view;
    }
}

class ViewHolder {
    TextView bottleName;
    TextView bottleAddress;
}

