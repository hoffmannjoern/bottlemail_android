package com.universityofleipzig.bottlemail.webservice;

import com.universityofleipzig.bottlemail.helper.GenericListener;

//asynchroner!! Abruf der Daten vom WebService fuer spezifische Flasche
public class BottleTask extends Task {
	
	private GenericListener<Boolean> mListener;
	//Listener vom Controller uebergeben
	BottleTask(GenericListener<Boolean> listener) {
		mListener = listener;
    }
	
	
	@Override
    protected void onPostExecute(Void v) {
		try{
				mListener.notify(hasResult());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
