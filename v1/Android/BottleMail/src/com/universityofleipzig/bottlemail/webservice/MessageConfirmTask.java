package com.universityofleipzig.bottlemail.webservice;

import java.util.EventListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.universityofleipzig.bottlemail.data.BMail;

public class MessageConfirmTask extends Task {
	
	
	private EventListener mListener;
	
	MessageConfirmTask(EventListener listener) {
    	this.mListener = listener;
    }
	
	void executePost(String url, int[] deletedMailIDs ) {
		
		JSONObject jsonO = new JSONObject();
		
		for(int i=0; i<deletedMailIDs.length;i++){
			try {
				jsonO.put(Integer.toString(i), deletedMailIDs[i]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executePost(url, jsonO.toString());	
		
//		for(int i=0; i<deletedMailIDs.length;i++){
//			jsonO.put(Integer.toString(i), deletedMailIDs[i]);
//		}
//		try {			
//			executePost(url, jsonO.toString());
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
	}
	
	void executePost(String url, SparseArray<BMail> deletedMailIDs ) {
		
		JSONObject jsonO = new JSONObject();
		
		
		for(int i=0; i<deletedMailIDs.size();i++){
			try {
				jsonO.put(Integer.toString(i), deletedMailIDs.valueAt(i).getBmailID());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executePost(url, jsonO.toString());	
	}
	@Override
    protected void onPostExecute(Void v) {
		mListener.notify();
	}

}
