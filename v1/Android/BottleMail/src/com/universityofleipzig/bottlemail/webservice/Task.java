package com.universityofleipzig.bottlemail.webservice;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

abstract class Task extends AsyncTask<String, Void, Void> {

	private JSONObject mResult;
    private JSONArray mResultArray;
    
    Task() {
    }
    
    void executeGet(String url){
    	execute("GET", url);
    }
    
    void executePost(String url, String body){
    	execute("POST", url, body);
    }
    
    @Override
    protected Void doInBackground(String... params) {
        try {
        	String result = "";
        	if(params.length >= 2) {
        		if(params[0] == "GET"){
        			result = Helper.excuteGet(params[1]);
        		}
        		else if(params[0] == "POST")
        		result = Helper.excutePost(params[2], params[3]);
        		
        		if(result.contains("["))
        			mResultArray = new JSONArray(result);
        		else
        			mResult = new JSONObject(result);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }    
    
    /*
     * @return true if the task has a result
     */
    protected Boolean hasResult(){
    	return (mResult != null);
    }
    
   protected Boolean hasResultArray(){
	   return (mResultArray != null);
   }
   
   protected JSONArray getResultArray(){
	   return mResultArray;
   }
   
   protected JSONObject getResult() {
	   return mResult;
   }

} 
