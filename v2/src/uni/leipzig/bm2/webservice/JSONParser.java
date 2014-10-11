package uni.leipzig.bm2.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import uni.leipzig.bm2.data.BMail;

public class JSONParser {

	public JSONObject writeNewBottleJSON(String mac, int type, String name) {
		JSONObject object = new JSONObject();
		try {
			object.put("cpuid", mac);
			object.put("type", type);
			if (name != "")
				object.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
		
		return object;
	}
	
	public JSONObject writeAddMessageJSON(BMail bmail) {
		JSONObject object = new JSONObject();
		try {
			object.put("message", bmail.getText());
			// TODO: Calender to string of format "Y-m-d H:i:s"
			object.put("date", bmail.getTimestamp());
			//TODO: put image
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
		
		return object;
	}
}
