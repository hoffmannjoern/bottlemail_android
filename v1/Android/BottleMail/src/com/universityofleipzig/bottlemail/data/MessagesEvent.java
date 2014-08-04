package com.universityofleipzig.bottlemail.data;


import android.util.SparseArray;


public class MessagesEvent extends MSGEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7011900011400671885L;
	private SparseArray<BMail> mBmails;
	
	 public MessagesEvent( Object source, SparseArray<BMail> mails) {
	    super(source);
	    this.mBmails = mails;
	  }

	  public SparseArray<BMail> getMessages() {
	    return this.mBmails;
	  }
}
