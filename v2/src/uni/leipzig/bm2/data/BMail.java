package uni.leipzig.bm2.data;

import java.util.Calendar;

import org.json.JSONException;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.location.Location;
import android.util.Base64;

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
	private String author;
	private double latitude;
	private double longitude;
	private boolean isDeleted = false;
	
	
	//Kontruktor für Webservice
	public BMail(int mID, String txt, String author, 
			String tStamp, Location geoLocation, boolean isDel) throws JSONException{
		
		this.bmailID = mID;
		//TODO: What about title of message?
		//this.title = ?
		this.text = txt;
		this.timestamp = tStamp;
		this.author = author;
		this.latitude = geoLocation.getLatitude();	
		this.longitude = geoLocation.getLongitude();	
		this.isDeleted = isDel;	
	}
	
	//Konstruktor für BluetoothGruppe
	public BMail(int mID, String txt, String tStamp){
		
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

}
