package uni.leipzig.bm2.data;

import java.util.ArrayList;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.util.Log;

public class BottleRack {

	private static final boolean DEBUG = BottleMailConfig.DATA_DEBUG;	
    private final static String TAG = BottleRack.class.getSimpleName();
    
	private static BottleRack bottleRack = new BottleRack();
	//private HashMap<String,Bottle> bottles; 
	//private SparseArray<Bottle> bottles;
	private ArrayList<Bottle> arrayOfBottles;
	
	//private String[] registeredBottles;

	private BottleRack(){
		if(DEBUG) Log.e(TAG, "+++ Constructor +++");
		
		//this.bottles = new HashMap<String,Bottle>();
		//this.bottles = new SparseArray<Bottle>();
		this.arrayOfBottles = new ArrayList<Bottle>();
	}

	public static BottleRack getInstance(){
		if(DEBUG) Log.e(TAG, "+++ getInstance +++");		
		return bottleRack ;	
	}
	
	public int getNumberOfBottles() {
		return arrayOfBottles.size();
	}
	
	// returns a certain bottle from the rack
	public Bottle getBottle(int index){
		return arrayOfBottles.get(index);
	}
	
	public Bottle getBottle(String mac) {
		
		for (int i=0; i<arrayOfBottles.size(); i++) {
			if (arrayOfBottles.get(i).getMac().equals(mac)) {
				return arrayOfBottles.get(i);
			}
		}
		return null;
	}

	public boolean isEmpty() {
		if(arrayOfBottles.isEmpty())
			return false;
		return true;
	}
	
	// returns all bottles from the rack
//	public HashMap<String, Bottle> getAllBottles(){
//		return (HashMap<String,Bottle>) bottles.clone();
//	}
	
	public ArrayList<Bottle> getAllBottles(){		
		
		return arrayOfBottles;
	}

	public boolean bottleIsKnown (String mac) {
		for (int i = 0; i < arrayOfBottles.size(); i++) {
			if (arrayOfBottles.get(i).getMac().equals(mac) == true)
				return true;
		}
		return false;
	}
	
	//check, if a certain bottle is in the rack
	public boolean bottleExists(Bottle bottle){
		if(arrayOfBottles.contains(bottle))
			return true;
		return false;
		//return (bottles.indexOfKey(btlID)>=0);		
	}
	
	/**
	 * 
	 * @param btl Bottle object (needs at least mac)
	 * @return true if added, false if mac of bottle is already known
	 */
	public boolean addBottleToRack(Bottle btl){
		// is bottle already known
		if( !bottleIsKnown(btl.getMac()) ) {
			this.arrayOfBottles.add(btl);
			//this.bottles.append(btl.getBottleID(), btl);
			return true;
		}
		return false;
	}
	
	public ArrayList<Bottle> getArrayListOfBottles () {
		return arrayOfBottles;
	}
	
	//TODO: neue Flasche erzeugen
}
