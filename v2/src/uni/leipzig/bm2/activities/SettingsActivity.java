package uni.leipzig.bm2.activities;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bottlemail2.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends Activity implements OnPreferenceChangeListener {

	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
    private final static String TAG = SettingsActivity.class.getSimpleName();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.e(TAG, "+++ onCreate +++");

		getActionBar().setDisplayHomeAsUpEnabled(true);
		//setContentView(R.layout.activity_settings);
		
    	getFragmentManager().beginTransaction().replace(
    			android.R.id.content, new MainSettingsFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(DEBUG) Log.e(TAG, "+++ onCreateOptionsMenu +++");

		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(DEBUG) Log.e(TAG, "+++ onOptionsItemSelected +++");

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if(item.getItemId() == android.R.id.home) 
			finish();
		return super.onOptionsItemSelected(item);
	}

    public static class MainSettingsFragment extends PreferenceFragment{

    	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
        private final static String TAG = MainSettingsFragment.class.getSimpleName();
        
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
    		if(DEBUG) Log.e(TAG, "+++ onCreate +++");

    		// Load the preferences from an XML resource
    		addPreferencesFromResource(R.xml.settings);
    		
    		OnPreferenceChangeListener onPreferenceChangeListener = new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					// TODO Auto-generated method stub
					OnPreferenceChangeListener listener = (OnPreferenceChangeListener) getActivity();
					listener.onPreferenceChange(preference, newValue);
					return true;
				}
			};

			EditTextPreference editPref = (EditTextPreference) 
					getPreferenceManager().findPreference("username_preference");
			editPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
			
			ListPreference listPref = (ListPreference) 
					getPreferenceManager().findPreference("connection_preference");
			listPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
			
			listPref = (ListPreference) 
					getPreferenceManager().findPreference("precision_preference");
			listPref.setOnPreferenceChangeListener(onPreferenceChangeListener);			
    	}  
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

}
