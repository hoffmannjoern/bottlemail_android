package com.universityofleipzig.bottlemail.data;


public class MessageEvent extends MSGEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6489868873373436918L;
	private BMail mMessage;
	
	 public MessageEvent(Object source, BMail message) {
	    super(source);
	    this.mMessage = message;
	  }

	  public BMail getMessage() {
	    return this.mMessage;
	  }
}
