package com.universityofleipzig.bottlemail.bluetooth3;

public interface MessageCallback {
	void incomingData(int id, String author, String title, String text);
}
