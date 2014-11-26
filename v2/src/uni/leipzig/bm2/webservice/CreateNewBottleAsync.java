package uni.leipzig.bm2.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import uni.leipzig.bm2.config.AppUtilities;
import uni.leipzig.bm2.config.BottleMailConfig;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CreateNewBottleAsync extends AsyncTask<String, Void, String> {
	// TODO get this class out of the MainAct

	private static final boolean DEBUG = BottleMailConfig.HTTP_DEBUG;	
    private final String TAG = CreateNewBottleAsync.class.getSimpleName();
    Context mContext;
    
    public CreateNewBottleAsync (Context context) {
    	mContext = context;
    }
    
	@Override
	protected String doInBackground(String... params) {
		// TODO implement that useful
		try {
			return sendBottle();
		} catch (IOException e) {
			return "Unable to retrieve web page. URL may be invalid.";
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	}
	
	private String sendBottle () throws IOException {
		
		InputStream inputStream = null;
		int length = 500;
		
		try {
			URL url = new URL(AppUtilities.getInstance().getServerBottleUrl());
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			// milliseconds
			httpConn.setReadTimeout(10000); 
			httpConn.setConnectTimeout(15000);
			httpConn.setRequestMethod("POST");
			httpConn.setDoInput(true);
			// Starts the query
			// TODO: Get the data of JSONParser, maybe as string or bottle or json himself
			// format it and post it to webservice
			httpConn.connect();
			
			// Display status code (200 is success)
			int response = httpConn.getResponseCode();
			Log.d(TAG, "The response is: " + response);
			inputStream = httpConn.getInputStream();
			
			// Convert the InputStream into a string
			String contentAsString = 
					convertInputStreamToString(inputStream, length);
			return contentAsString;
			
		// Make sure that the InputStream is closed after the app is finished using it.
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	private String convertInputStreamToString(InputStream stream, int len) 
			throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
}