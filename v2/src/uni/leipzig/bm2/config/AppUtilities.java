package uni.leipzig.bm2.config;

public class AppUtilities {
	
	private static AppUtilities appUtils = new AppUtilities();
	//private String mServerUrl = null;
	private String mServerBottleUrl;
	private String mServerMessageUrl;
	private double mAppVersion;
	private String mAppVersionName;
	
	private AppUtilities(){
		
		//this.mServerUrl = "http://tipc011.informatik.uni-leipzig.de/bmwebservice/";
		this.mServerBottleUrl = "bottlemail.gurkware.de/API/bottle/";
		this.mServerMessageUrl = "bottlemail.gurkware.de/API/message/";
		this.mAppVersion = 2.0;
		this.mAppVersionName = "Rainbow warrior";//"Flaschenkind";
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
}
