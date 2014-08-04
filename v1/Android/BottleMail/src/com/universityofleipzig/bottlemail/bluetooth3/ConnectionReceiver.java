package com.universityofleipzig.bottlemail.bluetooth3;

import java.util.ArrayList;

public abstract class ConnectionReceiver {
	
	ArrayList<ConnectionFinishedListener> mListener = new ArrayList<ConnectionFinishedListener>();
	
	void subscribe(ConnectionFinishedListener listener){
		if(!mListener.contains(listener))
			mListener.add(listener);
	}
	
	abstract void incomingData(Byte[] data);
	
	protected void notifyListener(){
		for(ConnectionFinishedListener listener: mListener)
			listener.notify(null);
	}
}
