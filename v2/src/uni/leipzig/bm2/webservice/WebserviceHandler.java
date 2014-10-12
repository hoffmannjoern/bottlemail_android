package uni.leipzig.bm2.webservice;

import java.util.ArrayList;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.BMail;
import uni.leipzig.bm2.data.Bottle;
import android.util.Log;

public class WebserviceHandler {

	private static final boolean DEBUG = BottleMailConfig.WS_DEBUG;	
    private final static String TAG = WebserviceHandler.class.getSimpleName();
    
    /**
     * Post created new bottle to webservice by using POST 
     * @param bottle new 
     * @return new bottleID, or on failure -1
     */
	public int postBottle(Bottle bottle) {
        if(DEBUG) Log.e(TAG,"+++ postBottle(Bottle) +++");
        
        //String mac = bottle.getMac();
        //int type = 1; //1 - normal, 2 - twitter
        //String name = bottle.getName(); //optional
        
        if(DEBUG) Log.e(TAG, "Failed to create bottle");
		return -1;
	}
	
	/**
	 * Post found new bottle to webservice by using POST 
	 * @param bottleMac mac of found bottle
	 * @param type 1-normal bottle, 2-twitter bottle
	 * @param name of new bottle
	 * @return new bottleID, or on failure -1
	 */
	public int postBottle(String bottleMac, int type, String name) {
        if(DEBUG) Log.e(TAG,"+++ postBottle(Mac, Type, Name) +++");
        

        if(DEBUG) Log.e(TAG, "Failed to create bottle");
		return -1;
	}
	
	/**
	 * Post found new bottle to webservice by using POST 
	 * @param bottleMac mac of found bottle
	 * @param type 1-normal bottle, 2-twitter bottle
	 * @return new bottleID, or on failure -1
	 */
	public int postBottle(String bottleMac, int type) {
        if(DEBUG) Log.e(TAG,"+++ postBottle(Mac, Type) +++");
        

        if(DEBUG) Log.e(TAG, "Failed to create bottle");
		return -1;
	}

	// Maybe we have to identify a bottle by mac:
	// TODO: decide about identification of bottles
	/**
	 * Get bottle from webservice with bottleID by using GET
	 * @param bottleID 
	 * @return Bottle with opt. name, number of messages, creation timestamp and type; or on failure null
	 */
	public Bottle getBottle(int bottleID) {
		if(DEBUG) Log.e(TAG,"+++ getBottle +++");
		
		Bottle bottle = null;

        if(DEBUG) Log.e(TAG, "Failed to find bottle");
		return bottle;
	}

//	/**
//	 * Get bottle from webservice with mac by using GET
//	 * @param bottleID 
//	 * @return Bottle with opt. name, number of messages, creation timestamp and type; or on failure null
//	 */
//	public Bottle getBottle(String mac) {
//		if(DEBUG) Log.e(TAG,"+++ getBottle +++");
//		
//		Bottle bottle = null;
//		
//		return bottle;
//	}
	
	/**
	 * Post a given BMail to Webservice by using POST
	 * @param bottleID references the bottle to order the message in
	 * @param bmail contains message, timestamp, optional: (image), user, latitude, longitude, precision
	 * @return id of message, on failure -1
	 */
	public int postMessage (int bottleID, BMail bmail) {
        if(DEBUG) Log.e(TAG,"+++ postMessage +++");

        if(DEBUG) Log.e(TAG, "Failed to post message");
        return -1;
	}
	
	/**
	 * Get a known message from Webservice by using GET
	 * @param messageID has to be already known for that function
	 * @return filled BMail, on failure null
	 */
	public BMail getMessage(int messageID) {
        if(DEBUG) Log.e(TAG,"+++ getMessage +++");

        if(DEBUG) Log.e(TAG, "Failed to get message");
		return null;
	}
	
	/**
	 * Get the last messages of bottle by using GET
	 * @param bottleID references the bottle to which the messages have to belong
	 * @param numberOfMessages how many messages?
	 * @return list of BMails with at least 0 and at most numberOfMessages messages, or null on failure
	 */
	public ArrayList<BMail> getMessages(int bottleID, int numberOfMessages) {
        if(DEBUG) Log.e(TAG,"+++ getMessages +++");

        if(DEBUG) Log.e(TAG, "Failed to get " + numberOfMessages + "messages");
		return null;
	}

	/**
	 * Get the last 10 messages of bottle by using GET
	 * @param bottleID references the bottle to which the messages have to belong
	 * @return list of BMails with at least 0 and at most 10 messages, or null on failure
	 */
	public ArrayList<BMail> getMessages(int bottleID) {
        if(DEBUG) Log.e(TAG,"+++ getMessages +++");

        if(DEBUG) Log.e(TAG, "Failed to get 10 messages");
		return null;
	}
	
}
