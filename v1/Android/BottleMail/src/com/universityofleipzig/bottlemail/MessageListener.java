package com.universityofleipzig.bottlemail;

import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.data.MessageEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

public class MessageListener implements GenericListener<MSGEvent> {
	
	private MainActivity mActivity;
	
	MessageListener(MainActivity activity){
		mActivity = activity;
	}
	
	@Override public void notify(MSGEvent e){
//		mActivity.test(e.getMessage());
	}

}
