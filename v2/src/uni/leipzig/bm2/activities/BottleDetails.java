package uni.leipzig.bm2.activities;

import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bottlemail2.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BottleDetails extends ActionBarActivity {

	private static final String TAG = "BottleDetails";
	
	private Context mContext;
	private LayoutInflater mInflater;

	private boolean isInternetPresent;
	
	private Bottle mBottle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e(TAG,"+++ OnCreate +++");
		
		setContentView(R.layout.activity_bottle_details);

		usePlaceholderFragments(savedInstanceState);
		
		mContext = getApplicationContext();
		mInflater = LayoutInflater.from(mContext);

		ActionBar actionBar = getSupportActionBar();
		
	    // Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // For the main activity, make sure the app icon in the action bar
	        // does not behave as a button
			actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
		}
		
		//Get Bottle of intent
		Bundle extras = getIntent().getExtras();
		mBottle = extras.getParcelable(MainFragment.SHOW_BOTTLE_DETAILS);

		Toast.makeText(this, 
				mBottle.getBottleName(), Toast.LENGTH_SHORT).show();

		// set actionbar titles
		actionBar.setTitle(mBottle.getBottleName());
		actionBar.setSubtitle(R.string.action_subtitle_compose);

		assureInternetConnection();
		
	}

	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment 
	public void assureInternetConnection(){
		
		Log.d(TAG, "+++ check internet status +++");
		// get Internet status
		isInternetPresent = isOnline();
		
		if( isInternetPresent ) {
//			mWebserviceHandler = WebserviceHandler.getInstance();
					
		} else {
            // Ask user to connect to Internet
            showAlertDialog(this, 
            		getString(R.string.alert_title_no_internet_conn),
                    getString(R.string.alert_body_no_internet_conn), 
                    false);
		}
		
	}
	
	/**
	 * check if device is online
	 * @return online status
	 */
	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment 
	public boolean isOnline() {
		
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
    
    /**
     * shows a alert dialog with a title, body, close and accept button
     * @param context
     * @param title
     * @param message
     * @param status
     */
	//TODO State of the art ? but does what it is supposed to do... 
	// at the moment
	public void showAlertDialog(Context context, String title, 
			String message, Boolean status) {

		if ( status == true ) {
			
			new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(getString(R.string.alert_btn_okay), 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		} else {
			
			new AlertDialog.Builder(context).setTitle(title)
			.setMessage(message).setPositiveButton(
					getString(R.string.alert_btn_accept), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.startActivity(
							new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton(getString(R.string.alert_btn_close), 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.finish();
				}
			})
			.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar 
		// if it is present.
		getMenuInflater().inflate(R.menu.bottle_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void usePlaceholderFragments (Bundle savedInstanceState) {

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_bottle_details,
					container, false);
			return rootView;
		}
	}
}
