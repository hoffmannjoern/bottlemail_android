package com.universityofleipzig.bottlemail.webservice;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.SparseArray;

import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.data.MessagesEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

class MessagesTask extends Task {
	
	private Bottle mBottle;
	private GenericListener<MSGEvent> mListener;
	
	MessagesTask(Bottle bottle, GenericListener<MSGEvent> listener) {
		mBottle = bottle;
    	mListener = listener;
    }
	
		
	@Override
    protected void onPostExecute(Void v) {
		try {
			BMail mail = null;
			SparseArray<BMail> mails = new SparseArray<BMail>();
			
			JSONArray jsonA = getResultArray();
			JSONObject jsonO = null;
			
			for(int i=0; i < jsonA.length(); i++){
				
				jsonO = jsonA.getJSONObject(i);	
				//TODO: Datum und Zeit in UTC speichern
				Calendar date = Calendar.getInstance();
				date.set(2013, 6, 1, 1, 1);
				//date.setTime(SimpleDateFormat.getDateInstance().parse(jsonO.getString("time")));
				//mail = mBottle.createNewBMail(jsonO.getInt("msgID"), jsonO.getString("txt"), jsonO.getString("author"), date, jsonO.getBoolean("isDel"));
				mail = mBottle.createNewBMail(jsonO.getInt("msgID"), jsonO.getString("txt"), jsonO.getString("author"), date, false);
				mails.append(mail.getBmailID(), mail);				 				
			}
			mListener.notify(new MessagesEvent(this,mails));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
