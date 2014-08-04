package com.universityofleipzig.bottlemail.helper;

/**
 * 
 * TODO finde einen besseren Weg
 * 
 * Diese Object dient lediglich zum merken der BottleID, die gefunden wurde.
 * Ein anderes Teammitglied sollte sich darum kümmern diese Informationen,
 * dem BottleDetails (SherlockActivity) mitzugeben.
 * 
 * Zum Speichern der Daten verwende ich einen Singelton
 * 
 * @author schueller
 *
 */
public class TestBottleDetailHelper {

	private static TestBottleDetailHelper instance = null;
	private int bottleID = -1;
	
	private TestBottleDetailHelper () {
		
	}
	
	public static TestBottleDetailHelper getInstance () {
		if (instance == null) {
			instance =  new TestBottleDetailHelper () ;
		}
		return instance;
	}
	
	public void setLastID(int id){
		this.bottleID = id;
	}
	
	public int getLastID () {
		return this.bottleID;
	}
	
}
