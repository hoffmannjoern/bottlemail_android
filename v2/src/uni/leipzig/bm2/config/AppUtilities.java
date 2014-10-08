package uni.leipzig.bm2.config;

public class AppUtilities {
	
	private static AppUtilities appUtils = new AppUtilities();
	private String mServerUrl = null;
	private double mAppVersion = 0;
	private String mAppVersionName = null;
	
	private AppUtilities(){
		
		//TODO: aus Datenbank oder config-Datei einlesen
		this.mServerUrl = "http://tipc011.informatik.uni-leipzig.de/bmwebservice/";
		this.mAppVersion = 2.0;
		this.mAppVersionName = "Bottager";//"Flaschenkind";
	}
	
	public static AppUtilities getInstance(){
		return appUtils;
	}
	
	public String getServerUrl () {
		return this.mServerUrl;
	}

	public double getAppVersion() {
		return this.mAppVersion;
	}

	public String getAppVersionName () {
		return this.mAppVersionName;
	}
}
