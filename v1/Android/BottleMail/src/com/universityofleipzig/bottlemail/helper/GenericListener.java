package com.universityofleipzig.bottlemail.helper;

import java.util.EventListener;

public interface GenericListener<T> extends EventListener {
	void notify(T e);
}
