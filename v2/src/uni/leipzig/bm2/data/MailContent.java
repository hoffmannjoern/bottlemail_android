package uni.leipzig.bm2.data;

import java.util.Calendar;

import org.json.JSONException;

import android.location.Location;

public class MailContent {

	//TODO just copied from Version1 
	// -> old name: BMail changed to MailContent
	
	//evtl. BottleObjekt 
	private int bmailID;
	private String text;
	private String author;
	private Calendar timestamp;
	private boolean isDeleted = false;
	private Location location;
	private String title=null;
	
	
	public String getTitle() {
		return title;
	}

	//Kontruktor für Webservice
	public MailContent(int mID, String txt, String author, 
			Calendar tStamp, boolean isDel) throws JSONException{
		
		this.bmailID = mID;
		this.text = txt;
		this.author = author;
		//TODO: timestamp format/datentyp
		this.timestamp = tStamp;
		this.isDeleted = isDel;
		//TODO: Format location
		//this.location = new Location(author);		
	}
	
	//Konstruktor für BluetoothGruppe
	public MailContent(int mID, String txt, Calendar tStamp){
		
		this.bmailID = mID;
		this.text = txt;
		this.timestamp =tStamp;
	}

	public int getBmailID() {
		return bmailID;
	}

	public String getText() {
		return text;
	}

	public String getAuthor() {
		return author;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public Location getLocation() {
		return location;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "BMail [bmailID=" + bmailID + ", text=" + text + ", author="
				+ author + ", timestamp=" + timestamp + ", isDeleted="
				+ isDeleted + ", location=" + location + "]";
	}

}
