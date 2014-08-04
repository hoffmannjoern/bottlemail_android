package com.universityofleipzig.bottlemail;

import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.data.MessageEvent;
import com.universityofleipzig.bottlemail.data.MessagesEvent;
import com.universityofleipzig.bottlemail.helper.GenericListener;

public class MessagesListener implements GenericListener<MSGEvent> {

	private BottleDetails mBottleDetails;

	MessagesListener(BottleDetails bottleDetails) {
		mBottleDetails = bottleDetails;
	}

	@Override
	public void notify(MSGEvent e) {
		if (e.getClass().equals(MessagesEvent.class)) {
			this.notifyMessages((MessagesEvent)e);
		} else if (e.getClass().equals(MessageEvent.class)) {
			this.notifyMessage((MessageEvent)e);
		}
		// mActivity.test(e.getMessage());
	}

	private void notifyMessages(MessagesEvent e) {
		mBottleDetails.fillBMails(e.getMessages());
	}

	private void notifyMessage(MessageEvent e) {
		mBottleDetails.fillBMail(e.getMessage());
	}

}
