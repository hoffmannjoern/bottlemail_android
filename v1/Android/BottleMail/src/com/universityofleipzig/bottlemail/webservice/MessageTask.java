package com.universityofleipzig.bottlemail.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.data.MessageEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

class MessageTask extends Task {
	
	private Bottle mBottle;
	private GenericListener<MSGEvent> mListener;
	
	MessageTask(Bottle bottle, GenericListener<MSGEvent> listener) {
    	mBottle = bottle;
    	mListener = listener;
    }
		
	@Override
    protected void onPostExecute(Void v) {
		try {
			
			BMail message = null;
			Calendar date = Calendar.getInstance();
			date.setTime(SimpleDateFormat.getDateInstance().parse(getResult().getString("timestamp")));
				
			message = mBottle.createNewBMail(getResult().getInt("msgID"), getResult().getString("txt"), getResult().getString("author"), date, getResult().getBoolean("isDel"));
			mListener.notify(new MessageEvent(this, message));
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
