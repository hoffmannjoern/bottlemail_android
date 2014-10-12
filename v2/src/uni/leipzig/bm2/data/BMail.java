package uni.leipzig.bm2.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

public class BMail {

	private static final boolean DEBUG = BottleMailConfig.DATA_DEBUG;	
    private final static String TAG = BMail.class.getSimpleName();

	//TODO just copied from Version1 
	// -> old name: BMail changed to MailContent
	
	//evtl. BottleObjekt 
	private int bmailID;
	private String title = "";
	private String text;
	private String timestamp = DataTransformer.getUnixTimestampWithCurrentTime();
	private Base64 image = null;
	private String author = "";
	private double latitude = 51.330117;
	private double longitude = 12.400581;
	private boolean isDeleted = false;
	
	
	//Kontruktor für Webservice
	public BMail(int mID, String txt, String author, 
			String tStamp, Location geoLocation, boolean isDel) throws JSONException{
		if(DEBUG) Log.e(TAG, "+++ Constructor(mID, txt, author, tStamp, geoLocation, isDel) +++");
		
		this.bmailID = mID;
		//TODO: What about title of message?
		//this.title = ?
		this.text = txt;
		this.timestamp = tStamp;
		this.author = author;
		this.latitude = geoLocation.getLatitude();	
		this.longitude = geoLocation.getLongitude();	
	}
	
	//Konstruktor für BluetoothGruppe
	public BMail(int mID, String txt, String tStamp){
		if(DEBUG) Log.e(TAG, "+++ Constructor(mID, txt, tStamp) +++");
		
		
		this.bmailID = mID;
		this.text = txt;
		this.timestamp = tStamp;
	}

	public int getBmailID() {
		return bmailID;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setLocation(Location location) {
		//TODO: set precision implicit through a variable set in settings, 
		// so that we store only data of that precision?!
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	@Override
	public String toString() {
		return "BMail [bmailID=" + bmailID 
				//TODO: + TITLE!
				+ ", text=" + text + ", timestamp=" + timestamp 
				//TODO: + IMAGE!
				+ ", author=" + author + ", latitude=" + latitude + ", longitude=" + longitude 
				+ ", isDeleted=" + isDeleted + "]";
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
