package uni.leipzig.bm2.config;

import android.os.Environment;

public class AppUtilities {
	
	private static AppUtilities appUtils = new AppUtilities();
	//private String mServerUrl = null;
	private String mServerBottleUrl;
	private String mServerMessageUrl;
	private double mAppVersion;
	private String mAppVersionName;
	private String pathToExtStorageDir;
	
	private AppUtilities(){
		
		//this.mServerUrl = "http://tipc011.informatik.uni-leipzig.de/bmwebservice/";
		this.mServerBottleUrl = "bottlemail.gurkware.de/API/bottle/";
		this.mServerMessageUrl = "bottlemail.gurkware.de/API/message/";
		this.mAppVersion = 2.0;
		this.mAppVersionName = "Rainbow warrior";//"Flaschenkind";
		//FIXME: only for external storage usage when using persistent memory for messages
		this.pathToExtStorageDir = 
				Environment.getExternalStorageDirectory().toString() + "/bm_content";
	}
	
	public static AppUtilities getInstance(){
		return appUtils;
	}
	
//	public String getServerUrl () {
//		return this.mServerUrl;
//	}

	public String getServerBottleUrl () {
		return this.mServerBottleUrl;
	}

	public String getServerMessageUrl () {
		return this.mServerMessageUrl;
	}

	public double getAppVersion() {
		return this.mAppVersion;
	}

	public String getAppVersionName () {
		return this.mAppVersionName;
	}
	
	public String getPathToExtStorageDir() {
		return this.pathToExtStorageDir;
	}
}
