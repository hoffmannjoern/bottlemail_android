package com.universityofleipzig.bottlemail.bluetooth3;

public class QueueEntry {
	
	private String mCommand;
	private ConnectionReceiver mCallback;
	
	public QueueEntry(String command, ConnectionReceiver callback){
		mCommand = command;
		mCallback = callback;
	}
	
	public String getCommand(){
		return mCommand;
	}
	
	public ConnectionReceiver getCallback(){
		return mCallback;
	}
}
