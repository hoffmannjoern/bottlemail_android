package uni.leipzig.bm2.webservice;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.BMail;
import uni.leipzig.bm2.data.Bottle;
import android.util.Log;

public class JSONParser {

	private static final boolean DEBUG = BottleMailConfig.WS_DEBUG;	
    private final static String TAG = JSONParser.class.getSimpleName();
    
	public JSONObject writeJSONNewBottle(String mac, int type, String name) {
		if(DEBUG) Log.e(TAG, "+++ writeJSONNewBottle +++");
		
		JSONObject object = new JSONObject();
		try {
			object.put("cpuid", mac);
			object.put("type", type);
			if (name != "")
				object.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
		
		return object;
	}
	
	public Bottle readJSONGetBottle (JSONObject object) {
		if(DEBUG) Log.e(TAG, "+++ readJSONGetBottle +++");
		
		return null;
	}
	
	public JSONObject writeJSONAddMessage(BMail bmail) {
		if(DEBUG) Log.e(TAG, "+++ writeJSONAddMessage +++");
		
		JSONObject object = new JSONObject();
		try {
			object.put("message", bmail.getText());
			// TODO: Calender to string of format "Y-m-d H:i:s"
			object.put("date", bmail.getTimestamp());
			//TODO: put image
			object.put("user", bmail.getAuthor());
			object.put("latitude", bmail.getLatitude());
			object.put("longitude", bmail.getLongitude());
			//TODO: should we give an explicit precsision 
			// or implicit by giving location with only that precise data
			//object.put("precision", SETPRECISION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
		
		return object;
	}
	
	public BMail readJSONGetMessageJSON(JSONObject object) {
		if(DEBUG) Log.e(TAG, "+++ readJSONGetMessageJSON +++");
		
		return null;
	}

	public ArrayList<BMail> readJSONGetMessagesJSON(JSONObject object) {
		if(DEBUG) Log.e(TAG, "+++ readJSONGetMessageJSON +++");
		
		return null;
	}
}
