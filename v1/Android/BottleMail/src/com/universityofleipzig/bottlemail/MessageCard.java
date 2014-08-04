package com.universityofleipzig.bottlemail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.universityofleipzig.bottlemail.data.BMail;

public class MessageCard extends Card {

	private BMail bmail;

	public MessageCard(BMail bmail) {
		super();
		this.bmail = bmail;
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.messagecard,
				null);
		
		// wenn nachricht gestezt
		if (bmail != null) {
			((TextView) view.findViewById(R.id.title)).setText(bmail.getAuthor());
			((TextView) view.findViewById(R.id.description)).setText(bmail.getText());
		}
		
		return view;
	}

	@Override
	public String toString() {
		return "MessageCard [bmail=" + bmail + "]";
	}
}
