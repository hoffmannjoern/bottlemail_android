package com.universityofleipzig.bottlemail.data;

public class AppUtilities {
	
	private static AppUtilities appUtils = new AppUtilities();
	private String mServerUrl = null;
	private String mAppVersion = null;
	private String mAppVersionName = null;
	
	private AppUtilities(){
		
		//TODO: aus Datenbank oder config-Datei einlesen
		this.mServerUrl = "http://tipc011.informatik.uni-leipzig.de/bmwebservice/";
		this.mAppVersion = "die Allererste";
		this.mAppVersionName = "Flaschenkind";
	}
	
	public static AppUtilities getInstance(){
		return appUtils;
	}
}
