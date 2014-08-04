package com.universityofleipzig.bottlemail.webservice;

import java.util.EventListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.Bottle;

public class MessageSendTask extends Task {
	
	
	private EventListener mListener;
	
	MessageSendTask(EventListener listener) {
    	this.mListener = listener;
    }
	
	void executePost(String url, Bottle bottle, BMail bMail ) {
		
		JSONObject jsonO = new JSONObject();
		
		try {
			jsonO.put("btlID", bottle.getBottleID());
			jsonO.put("msgID", bMail.getBmailID());
			jsonO.put("txt", bMail.getText());
			jsonO.put("author", bMail.getAuthor());
			jsonO.put("time", bMail.getTimestamp());
			
			executePost(url, jsonO.toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Override
    protected void onPostExecute(Void v) {
		mListener.notify();
	}

}
