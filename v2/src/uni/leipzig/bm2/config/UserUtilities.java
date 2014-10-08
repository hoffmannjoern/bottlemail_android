package uni.leipzig.bm2.config;

public class UserUtilities {
	
	private static UserUtilities userUtils = new UserUtilities();
	private String userName = "";
	
	private UserUtilities(){
		
		//TODO: aus Datenbank oder config-Datei einlesen
		this.userName = "Robinson Crusoe";
	}
	
	public static UserUtilities getInstance(){
		return userUtils;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName (String newName) {
		this.userName = newName;
	}
}
