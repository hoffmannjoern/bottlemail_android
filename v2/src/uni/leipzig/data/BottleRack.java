package uni.leipzig.data;

import android.util.SparseArray;

public class BottleRack {

	//TODO just copied from Version1, no changes
	
	private static BottleRack bottleRack = new BottleRack();
	//private HashMap<String,Bottle> bottles; 
	
	private String[] registeredBottles;
	
	private SparseArray<Bottle> bottles;

	private BottleRack(){
		
		//this.bottles = new HashMap<String,Bottle>();
		this.bottles = new SparseArray<Bottle>();
	}

	public static BottleRack getInstance(){
		
		return bottleRack ;	
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

	//check, if a certain bottle is in the rack
	public boolean bottleExists(int btlID){
		
		return (bottles.indexOfKey(btlID)>=0);		
	}
	
	public void addBottleToRack(Bottle btl){
		/*
		 * Hier sollte geprueft werden, ob die Flasche schon vorhanden ist oder nicht,
		 * wenn ja sollte eine Exception geworfen werden 
		 */
		this.bottles.append(btl.getBottleID(), btl);		
	}
	
	//TODO: neue Flasche erzeugen
}
