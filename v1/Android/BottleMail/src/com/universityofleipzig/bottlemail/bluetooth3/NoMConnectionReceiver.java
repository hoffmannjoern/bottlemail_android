package com.universityofleipzig.bottlemail.bluetooth3;

import android.util.Log;

import com.universityofleipzig.bottlemail.data.DataTransformer;

public class NoMConnectionReceiver extends ConnectionReceiver {
	
	private static final String TAG = "NoMConnectionReceiver";
	private NoMFinishedCallback mNoMFinishedCallback;
	
	public NoMConnectionReceiver(NoMFinishedCallback noMFinishedCallback){
		mNoMFinishedCallback = noMFinishedCallback;
	}
	
	@Override
	public void incomingData(Byte[] data){
		if(data.length == 10){
			if(data[1] ==  1 && data[2] == 1){
				if(data[9] ==  33){

					mNoMFinishedCallback.incomingData(DataTransformer.getInt(data, 5, 4));
					
					notifyListener();
				}
			}
		}
	}
}