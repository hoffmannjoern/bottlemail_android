package uni.leipzig.bm2.data;

import org.json.JSONException;
import org.json.JSONObject;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Bottle implements Parcelable{

	private static final boolean DEBUG = BottleMailConfig.DATA_DEBUG;	
    private final static String TAG = Bottle.class.getSimpleName();
    
	private int bottleID;
	private String mac = "";
	private String bottleName;
	private String foundDate = null;
	private String deletedAt = null;
	// Standard geo-location: Sternburg Brauerei Leipzig 
	private double latitude = 51.330117;
	private double longitude = 12.400581;
	private int color = 0;
	private int numberOfMsgsOnBottle;
	private int numberOfMsgsOnBottleFromWS;
	private String protocolVersionMajor;
	private String protocolVersionMinor;
	
//	//<bmailID,BMailObjekt>
//	private SparseArray<BMail> bmails = 
//			new SparseArray<BMail>();

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
	
	public void setGeoLocation(Location geolocation, int precision) {
		if (precision == -1) {
			// TODO: do not set a real position
			// actually its 0,0, so its in atlantic ocean,
			// do we want to set a default like Sternburg Brauerei?
			this.latitude = 0;
			this.longitude = 0;
		} else if (precision == 10) {
			// set location so that there is only 10th precision (128.36 -> 120.0)
			this.latitude = Double.valueOf(
					(int) geolocation.getLatitude() 
					- (((int) geolocation.getLatitude()) % 10));
			this.longitude = Double.valueOf(
					(int) geolocation.getLongitude() 
					- (((int) geolocation.getLongitude()) % 10));
		} else if (precision == 6) {
			// set absolute precise location with 6 digits after point-notation
			// that's the default precision of location object, so that state is just 
			// faster than the next, although it fits in there, too.
			this.latitude = geolocation.getLatitude();
			this.longitude = geolocation.getLongitude();
		} else {
			// that is an evil and very very sad hack to use users precision wish
			// geolocation.getLatitude() but with with precision -> 
			// 1. double to String with bind of precision value, 
			// 2. replace german-like "," through readable "." notation
			// 3. convert String back to double value
			this.latitude = Double.valueOf(
					String.format("%."+precision+"f", 
							geolocation.getLatitude()).replace(",", ".")); 
			this.longitude = Double.valueOf(
					String.format("%."+precision+"f", 
							geolocation.getLongitude()).replace(",", "."));
		}
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
	
	
//	public BMail createNewBMail(int mID, String txt, 
//			String author, Calendar tStamp, boolean isDel) throws Exception{
//		if(DEBUG) Log.e(TAG, "+++ createNewBMail +++");
//		
//		if(bMailExists(mID)){
//			//TODO: doppelte Nachrichten
//			throw new Exception("message already exists");	
//		}
//		else{
//			BMail mail = new BMail(
//					mID, txt, author, tStamp, isDel);
//			bmails.put(mail.getBmailID(), mail);
//			return mail;
//		}
//		
//	}
//	
//	public BMail getBMail(int id){
//		if(DEBUG) Log.e(TAG, "+++ getBMail +++");
//		
//		return bmails.get(id);
//	}
//
//	//loescht Nachrichten von Bottle in App
//	//wird erst aufgerufen, wenn Nachrichten 
//	//erfolgreich von Modul geloescht wurden
//	public void deleteMessagesFromBottle(
//			SparseArray<BMail> messagesToDelete){
//		if(DEBUG) Log.e(TAG, "+++ deleteMessagesFromBottle +++");
//		
//		BMail bMail = null;
//		
//		for(int i = 0; i< messagesToDelete.size();i++){
//			
//			bMail = messagesToDelete.valueAt(i);
//			bMail.setText("Message deleted!");
//						
//			this.bmails.put(messagesToDelete.keyAt(i), bMail);
//		}
//		
//	}

//	//TODO: Mail an Webservice und Bluetooth senden
//	public void sendMessage(BMail msg){
//		if(DEBUG) Log.e(TAG, "+++ sendMessage +++");
//				
//		//msg.setBmailID(this.absoluteTotalNumberOfMsgsOnBottle+1);
//	}
//	
//	public boolean bMailExists(int mID){
//		if(DEBUG) Log.e(TAG, "+++ bMailExists +++");
//		
//		return (this.bmails.indexOfKey(mID) >= 0);			
//	}

}