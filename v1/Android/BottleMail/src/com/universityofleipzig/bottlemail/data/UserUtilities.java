package com.universityofleipzig.bottlemail.data;

public class UserUtilities {
	
	private static UserUtilities userUtils = new UserUtilities();
	private String userName = null;
	
	private UserUtilities(){
		
		//TODO: aus Datenbank oder config-Datei einlesen
		this.userName = "Robinson Crusoe";
	}
	
	public static UserUtilities getInstance(){
		return userUtils;
	}

}
