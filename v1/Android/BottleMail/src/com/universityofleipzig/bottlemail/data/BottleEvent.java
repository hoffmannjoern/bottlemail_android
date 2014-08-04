package com.universityofleipzig.bottlemail.data;

import java.util.EventObject;

public class BottleEvent extends EventObject {
	
	private Bottle mBottle;

	public BottleEvent(Object source, Bottle bottle){
		super(source);
		mBottle = bottle;
	}
	
	public Bottle getBottle() {
		return mBottle;
	}
}
