package uni.leipzig.bm2.data;

import java.util.Calendar;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

public class Bottle implements Parcelable{

	private static final boolean DEBUG = BottleMailConfig.DATA_DEBUG;	
    private final static String TAG = Bottle.class.getSimpleName();
    
	private int bottleID;
	private String mac = "";
	private String bottleName;
	private String foundDate = null;
	private String deletedAt = null;
	// Standard geo-location: Sternburg Brauerei Leipzig 
	private double longitude = 12.400581;
	private double latitude = 51.330117;
	private int color = 0;
	private int numberOfMsgsOnBottle;
	private int numberOfMsgsOnBottleFromWS;
	private String protocolVersionMajor;
	private String protocolVersionMinor;
	
	//<bmailID,BMailObjekt>
	private SparseArray<BMail> bmails = 
			new SparseArray<BMail>();

	public Bottle(int bottleID, String name, String mac){
		if(DEBUG) Log.e(TAG, "+++ Constructor(id, name, mac) +++");
		
		this.bottleID = bottleID;
		this.bottleName = name;		
		this.mac = mac;
	}
	
	public Bottle(String name, String mac) {
		if(DEBUG) Log.e(TAG, "+++ Constructor(name, mac) +++");
		
		this.bottleName = name;
		this.mac = mac;
	}
	
	public Bottle(Parcel parcel){
		if(DEBUG) Log.e(TAG, "+++ Constructor(parcel) +++");
		
		bottleID = parcel.readInt();
		bottleName = parcel.readString();
		mac = parcel.readString();
		longitude = parcel.readDouble();
		latitude = parcel.readDouble();
	}

	@Override
	public String toString(){
		return getBottleName();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		if(DEBUG) Log.e(TAG, "+++ writeToParcel +++");
		
		arg0.writeInt(bottleID);
		arg0.writeString(bottleName);
		arg0.writeString(mac);
		arg0.writeDouble(longitude);
		arg0.writeDouble(latitude);
	}
	
	// compilant problems with older versions, can't fix the warning
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = 
			new Parcelable.Creator() { 
		public Bottle createFromParcel(Parcel in) { 
			if(DEBUG) Log.e(TAG, "+++ createFromParcel +++");
			return new Bottle(in); 
		}   
		public Bottle[] newArray(int size) {
			if(DEBUG) Log.e(TAG, "+++ createFromParcel +++");
			return new Bottle[size]; 
		} 
	};
	public int getBottleID() {
		return bottleID;
	}
	
	public void setBottleID(int bottleID) {
		this.bottleID = bottleID;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBottleName() {
		return bottleName;
	}

	public void setBottleName(String bottleName) {
		this.bottleName = bottleName;
	}
	
	public String getFoundDate() {
		return foundDate;
	}

	public void setFoundDate(String foundDate) {
		this.foundDate = foundDate;
	}
	
	public String getDeleteDate() {
		return deletedAt;
	}

	public void setDeleteDate(String deletedAt) {
		this.deletedAt = deletedAt;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setGeoLocation(Location geolocation) {
		this.longitude = geolocation.getLongitude();
		this.latitude = geolocation.getLatitude();
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int resourceID){
		this.color = resourceID;
	}

	public void setNumberOfMsgsOnBottle(
			int numberOfMsgsOnBottle) {
		this.numberOfMsgsOnBottle = 
				numberOfMsgsOnBottle;
	}
	
	public int getNumberOfMsgsOnBottle () {
		return numberOfMsgsOnBottle;
	}

	public void setNumberOfMsgsOnBottleFromWS(
			int numberOfMsgsOnBottleFromWS) {
		this.numberOfMsgsOnBottleFromWS = 
				numberOfMsgsOnBottleFromWS;
	}

	public int getNumberOfMsgsOnBottleFromWS () {
		return numberOfMsgsOnBottleFromWS;
	}

	public void setProtocolVersionMajor(String protocolVersionMajor) {
		this.protocolVersionMajor = protocolVersionMajor;
	}

	public String getProtocolVersionMajor () {
		return protocolVersionMajor;
	}
	
	public void setProtocolVersionMinor(String protocolVersionMinor) {
		this.protocolVersionMinor = protocolVersionMinor;
	}	

	public String getProtocolVersionMinor () {
		return protocolVersionMinor;
	}
	
	
	public BMail createNewBMail(int mID, String txt, 
			String author, Calendar tStamp, boolean isDel) throws Exception{
		if(DEBUG) Log.e(TAG, "+++ createNewBMail +++");
		
		if(bMailExists(mID)){
			//TODO: doppelte Nachrichten
			throw new Exception("message already exists");	
		}
		else{
			BMail mail = new BMail(
					mID, txt, author, tStamp, isDel);
			bmails.put(mail.getBmailID(), mail);
			return mail;
		}
		
	}
	
	public BMail getBMail(int id){
		if(DEBUG) Log.e(TAG, "+++ getBMail +++");
		
		return bmails.get(id);
	}

	//loescht Nachrichten von Bottle in App
	//wird erst aufgerufen, wenn Nachrichten 
	//erfolgreich von Modul geloescht wurden
	public void deleteMessagesFromBottle(
			SparseArray<BMail> messagesToDelete){
		if(DEBUG) Log.e(TAG, "+++ deleteMessagesFromBottle +++");
		
		BMail bMail = null;
		
		for(int i = 0; i< messagesToDelete.size();i++){
			
			bMail = messagesToDelete.valueAt(i);
			bMail.setText("Message deleted!");
						
			this.bmails.put(messagesToDelete.keyAt(i), bMail);
		}
		
	}

	//TODO: Mail an Webservice und Bluetooth senden
	public void sendMessage(BMail msg){
		if(DEBUG) Log.e(TAG, "+++ sendMessage +++");
				
		//msg.setBmailID(this.absoluteTotalNumberOfMsgsOnBottle+1);
	}
	
	public boolean bMailExists(int mID){
		if(DEBUG) Log.e(TAG, "+++ bMailExists +++");
		
		return (this.bmails.indexOfKey(mID) >= 0);			
	}

}