package uni.leipzig.bm2.data;

import java.util.ArrayList;


import android.util.SparseArray;

public class BottleRack {
	
	//TODO just copied from Version1, no changes
	
	private static BottleRack bottleRack = new BottleRack();
	//private HashMap<String,Bottle> bottles; 
	
	private String[] registeredBottles;
	
	private SparseArray<Bottle> bottles;
	private ArrayList<Bottle> arrayOfBottles;

	private BottleRack(){
		
		//this.bottles = new HashMap<String,Bottle>();
		this.bottles = new SparseArray<Bottle>();
		this.arrayOfBottles = new ArrayList<Bottle>();
	}

	public static BottleRack getInstance(){
		
		return bottleRack ;	
	}
	
	public int getNumberOfBottles() {
		return bottles.size();
	}
	
	// returns a certain bottle from the rack
	public Bottle getBottle(long btlID){
		
		return bottles.get( (int) btlID);
	}

	// returns all bottles from the rack
//	public HashMap<String, Bottle> getAllBottles(){
//		return (HashMap<String,Bottle>) bottles.clone();
//	}
	
	public SparseArray<Bottle> getAllBottles(){		
		
		return bottles;
	}

	public boolean bottleIsKnown (String mac) {
		for (int i = 0; i < arrayOfBottles.size(); i++) {
			if (arrayOfBottles.get(i).getMac().equals(mac) == true)
				return true;
		}
		return false;
	}
	
	//check, if a certain bottle is in the rack
	public boolean bottleExists(int btlID){
		
		return (bottles.indexOfKey(btlID)>=0);		
	}
	
	public boolean addBottleToRack(Bottle btl){
		// is bottle already known
		if( !bottleIsKnown(btl.getMac()) ) {
			this.arrayOfBottles.add(btl);
			this.bottles.append(btl.getBottleID(), btl);
			return true;
		}
		return false;
	}
	
	public ArrayList<Bottle> getArrayListOfBottles () {
		return arrayOfBottles;
	}
	
	//TODO: neue Flasche erzeugen
}
