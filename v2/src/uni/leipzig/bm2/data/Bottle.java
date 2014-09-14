package uni.leipzig.bm2.data;

import java.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Bottle implements Parcelable{

	//TODO just copied from Version1, no changes
	
	private int bottleID;
	private String bottleName;
	//<bmailID,BMailObjekt>
	private SparseArray<MailContent> bmails = 
			new SparseArray<MailContent>();
	private int absoluteTotalNumberOfMsgsOnBottle;
	private int absoluteTotalNumberOfMsgsOnBottleFromWS;
	private String protocolVersionMajor;
	private String protocolVersionMinor;
	private String mac;

	//wird bei erster ueberpruefung auf geloeschte Nachrichten gesetzt
	private Calendar delTimestamp = null;
	
	public Bottle(int id, String name){
		
		this.bottleID = id;
		this.bottleName = name;		
	}

	public Bottle(Parcel parcel){
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