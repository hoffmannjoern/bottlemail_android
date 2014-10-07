package uni.leipzig.bm2.data;

import java.util.Calendar;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

public class Bottle implements Parcelable{

	private static final boolean DEBUG = BottleMailConfig.BOTTLE_DEBUG;	
    private final static String TAG = Bottle.class.getSimpleName();
    
	//TODO just copied from Version1, no changes
	
	private int bottleID;
	private String bottleName;
	private String mac;
	private String geolocation;
	private int color;
	//<bmailID,BMailObjekt>
	private SparseArray<MailContent> bmails = 
			new SparseArray<MailContent>();
	private int absoluteTotalNumberOfMsgsOnBottle;
	private int absoluteTotalNumberOfMsgsOnBottleFromWS;
	private String protocolVersionMajor;
	private String protocolVersionMinor;

	//wird bei erster ueberpruefung auf geloeschte Nachrichten gesetzt
	private Calendar delTimestamp = null;

	public Bottle(int id, String name, String mac){
		if(DEBUG) Log.e(TAG, "+++ Constructor(id, name, mac) +++");
		
		this.bottleID = id;
		this.bottleName = name;		
		this.mac = mac;
	}
	
	public Bottle(String name, String mac) {
		if(DEBUG) Log.e(TAG, "+++ Constructor(name, mac) +++");
		
		this.bottleName = name;
		this.mac = mac;
	}
	
	public Bottle(int id, String name){
		if(DEBUG) Log.e(TAG, "+++ Constructor(id, name) +++");
		
		this.bottleID = id;
		this.bottleName = name;	
		//TODO: set mac with help of webservice
		this.mac = "ca:fe:ca:fe:ca:fe";
	}

	public Bottle(Parcel parcel){
		if(DEBUG) Log.e(TAG, "+++ Constructor(parcel) +++");
		
		bottleID = parcel.readInt();
		bottleName = parcel.readString();
		mac = parcel.readString();
	}

	public MailContent createNewBMail(int mID, String txt, 
			String author, Calendar tStamp, boolean isDel) throws Exception{
		
		if(bMailExists(mID)){
			//TODO: doppelte Nachrichten
			throw new Exception("message already exists");	
		}
		else{
			MailContent mail = new MailContent(
					mID, txt, author, tStamp, isDel);
			bmails.put(mail.getBmailID(), mail);
			return mail;
		}
		
	}
	
	public MailContent getBMail(int id){
		return bmails.get(id);
	}

	//loescht Nachrichten von Bottle in App
	//wird erst aufgerufen, wenn Nachrichten 
	//erfolgreich von Modul geloescht wurden
	public void deleteMessagesFromBottle(
			SparseArray<MailContent> messagesToDelete){
		
		MailContent bMail = null;
		
		for(int i = 0; i< messagesToDelete.size();i++){
			
			bMail = messagesToDelete.valueAt(i);
			bMail.setText("Message deleted!");
						
			this.bmails.put(messagesToDelete.keyAt(i), bMail);
		}
		
	}

	//TODO: Mail an Webservice und Bluetooth senden
	public void sendMessage(MailContent msg){
		
		
		//msg.setBmailID(this.absoluteTotalNumberOfMsgsOnBottle+1);
	}
	
	public boolean bMailExists(int mID){
		
		return (this.bmails.indexOfKey(mID) >= 0);			
		
	}


	public int getBottleID() {
		return bottleID;
	}

	public String getBottleName() {
		return bottleName;
	}

	public void setAbsoluteTotalNumberOfMsgsOnBottle(
			int absoluteTotalNumberOfMsgsOnBottle) {
		this.absoluteTotalNumberOfMsgsOnBottle = 
				absoluteTotalNumberOfMsgsOnBottle;
	}

	public void setAbsoluteTotalNumberOfMsgsOnBottleFromWS(
			int absoluteTotalNumberOfMsgsOnBottleFromWS) {
		this.absoluteTotalNumberOfMsgsOnBottleFromWS = 
				absoluteTotalNumberOfMsgsOnBottleFromWS;
	}

	public void setProtocolVersionMajor(String protocolVersionMajor) {
		this.protocolVersionMajor = protocolVersionMajor;
	}

	public void setProtocolVersionMinor(String protocolVersionMinor) {
		this.protocolVersionMinor = protocolVersionMinor;
	}	

	public Calendar getDelTimestamp() {
		return delTimestamp;
	}

	public void setDelTimestamp(Calendar delTimestamp) {
		this.delTimestamp = delTimestamp;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getGeoLocation() {
		return geolocation;
	}
	
	public void setGeoLocation(String geolocation) {
		this.geolocation = geolocation;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int resourceID){
		this.color = resourceID;
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
		
		arg0.writeInt(bottleID);
		arg0.writeString(bottleName);
		arg0.writeString(mac);
	}
	
	public static final Parcelable.Creator CREATOR = 
			new Parcelable.Creator() { 
		public Bottle createFromParcel(Parcel in) { 
			return new Bottle(in); 
		}   
		public Bottle[] newArray(int size) { 
			return new Bottle[size]; 
		} 
	};
}