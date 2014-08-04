package com.universityofleipzig.bottlemail.webservice;

import java.util.Calendar;
import java.util.EventListener;

import android.util.Log;
import android.util.SparseArray;

import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

public class WebserviceHandler {
	
	private static WebserviceHandler wsHandler = new WebserviceHandler();
	private String mServerUrl;
	
	private WebserviceHandler(){
		//TODO: Webservice-Adresse laden
		mServerUrl="http://tipc011.informatik.uni-leipzig.de/bmwebservice/";
	}
	
	public static WebserviceHandler getInstance(){
		return wsHandler;	
	}
	
	public void asyncBottleExists(int id, GenericListener<Boolean> listener) {
		BottleTask task = new BottleTask(listener);
		task.executeGet(mServerUrl + "/bottles/" + id);
	}
	
	public void asyncGetLastBMail(Bottle bottle, GenericListener<MSGEvent> listener){
		MessageTask task = new MessageTask(bottle, listener);
		task.executeGet(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?limit=1");
	}
	
	public void asyncGetMessages(Bottle bottle, int limit, int offset, GenericListener<MSGEvent> listener){
		
		MessagesTask task = new MessagesTask(bottle, listener);
		task.executeGet(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?limit="+ limit +"&offset=" + offset);		
	}
	
	public void asyncGetMessages(Bottle bottle, int limit, GenericListener<MSGEvent> listener){
		
		MessagesTask task = new MessagesTask( bottle, listener);
		task.executeGet(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?limit="+ limit);
	}
	
	//TODO: Anfrage entweder so wie unten oder mit limit = 0
	public void asyncGetAllMessages(Bottle bottle, GenericListener<MSGEvent> listener){
		Log.d("", "asyncGetAllMessages");
		MessagesTask task = new MessagesTask(bottle, listener);
		task.executeGet(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?limit=99999");
	}
	
	//bereits von der Flasche geloeschte Nachrichten (Ueberprufung, ob diese von dem jeweiloigen Androidgeraet bereits ebtfernt sind)
	//Zeitstempel, damit nicht immer alle ueberprueft werden muessen
	//Nachrichten, die mit toBeDeleted markiert sind werden auch geschickt
	
	
	public void asyncGetMessagesToDelete(Bottle bottle, GenericListener<MSGEvent> listener){
		
		MessagesTask task = new MessagesTask( bottle, listener);
		task.executeGet(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?del="+bottle.getDelTimestamp());
	}
	
	//send a message to the webservice
	public void asyncSendMessagesToWebservice(Bottle bottle, BMail bMail, EventListener listener){
		
		MessageSendTask task = new MessageSendTask(listener);
		
		//json object aus mails der flasche erzeugen
		task.executePost(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages/" + bMail.getBmailID(), bottle, bMail);
	}
	
		//send to webservice what message were deleted from the bottle
	public void asyncSendConfirmationDeletedMessages(Bottle bottle, int[] deletedMailIDs, Calendar timestamp, EventListener listener){
		
		
		MessageConfirmTask task = new MessageConfirmTask(listener);
		
		//json objekt mit IDs der erfolgreich geloeschten Nachrichten erzeugen
		task.executePost(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?del=" + timestamp.toString(), deletedMailIDs);
	}
	
		//send to webservice what message were deleted from the bottle
	public void asyncSendConfirmationDeletedMessages(Bottle bottle, SparseArray<BMail> deletedMailIDs, Calendar timestamp, EventListener listener){
			
			
		MessageConfirmTask task = new MessageConfirmTask(listener);
			
		//json objekt mit IDs der erfolgreich geloeschten Nachrichten erzeugen
		task.executePost(mServerUrl + "/bottles/" + bottle.getBottleID() + "/messages?del=" + timestamp.toString(), deletedMailIDs);
	}
	
}
