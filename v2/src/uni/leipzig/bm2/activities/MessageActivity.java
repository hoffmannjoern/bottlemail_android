package uni.leipzig.bm2.activities;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bottlemail2.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MessageActivity extends Activity {

	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
    private final static String TAG = MessageActivity.class.getSimpleName();
    
	private static Bottle mBottle;
	private static String mBottleName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(DEBUG) Log.e(TAG,"+++ onCreate +++");
        
		setContentView(R.layout.activity_message);

		//TODO: Get Ble and/or Webservice connection or open new ones
		// that we have to test for again
		Bundle extras = getIntent().getExtras();
		mBottle = extras.getParcelable(BottleDetails.SHOW_MESSAGES);
		
		mBottleName = mBottle.getBottleName();
		
		// set actionbar titles
		getActionBar().setTitle(mBottleName);
		getActionBar().setSubtitle(R.string.subtitle_activity_message);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        if(DEBUG) Log.e(TAG,"+++ onCreateOptionsMenu +++");
        
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if(DEBUG) Log.e(TAG,"+++ onOptionsItemSelected +++");
        
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_getNewMessages) {
			// TODO: Get more messages and put them into Expandable View
			getMessages();
			Toast.makeText(this, R.string.menu_get, Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void getMessages() {
        if(DEBUG) Log.e(TAG,"+++ getMessages +++");
        
		// TODO: Communicate with WebService through Rest-Api
	}
}
