package uni.leipzig.bm2.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import uni.leipzig.bm2.config.AppUtilities;
import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.filemanager.FileManager;
import uni.leipzig.bottlemail2.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class MessageActivity extends Activity {

	private static final boolean DEBUG = BottleMailConfig.ACTIVITY_DEBUG;	
    private final static String TAG = MessageActivity.class.getSimpleName();

    private ExpandableListView mMessagesList = null;
    
	private static Bottle mBottle;
	private static String mBottleName;
	private static String mBottleMac;
	
	private FileManager mFileManager = null;
	private Vector<String> messages;
	
    private final ExpandableListView.OnChildClickListener messagesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if(DEBUG) Log.e(TAG,"+++ ExpandableListView.onChildClick +++");
                    return false;
                }
        };  
        
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
		mBottleMac = mBottle.getMac();
	
		// set actionbar titles
		getActionBar().setTitle(mBottleName);
		getActionBar().setSubtitle(R.string.subtitle_activity_message);

		try {
			mFileManager = new FileManager(AppUtilities.getInstance().getPathToExtStorageDir(), mBottleMac + ".txt");
			messages = mFileManager.getNumberNewestMessages(10);
			
			mMessagesList = (ExpandableListView) findViewById(R.id.elv_message);
			mMessagesList.setOnChildClickListener(messagesListClickListner);
			
			displayMessages(messages);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
			Toast.makeText(this, R.string.menu_get, Toast.LENGTH_SHORT).show();
			getMessages();
			displayMessages(messages);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    private void displayMessages(Vector<String> messages) {
        if(DEBUG) Log.e(TAG,"+++ displayMessages +++");

        if(DEBUG) {
			try {
				Log.e(TAG, "Number of messages: " + Integer.valueOf(mFileManager.getLinesCount()).toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if (messages == null) return;

        ArrayList<HashMap<String, String>> messagesList 
        		= new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> messagesContent
        		= new ArrayList<ArrayList<HashMap<String, String>>>();
        
        for (String message : messages) {
            if(DEBUG) Log.e(TAG, "message:" + message);
        	String[] parts = message.split("\\|");
            HashMap<String, String> currentMessage = new HashMap<String, String>();
            currentMessage .put("message", parts[0]);
            currentMessage .put("user", parts[1]);
            if(DEBUG) Log.e(TAG, "parts0/1:" + parts[0] + "  " + parts[1]);
            messagesList.add(currentMessage);

            ArrayList<HashMap<String, String>> messageContentData =
                    new ArrayList<HashMap<String, String>>();
            for (int i = 2; i < 8; i+=2) {
                HashMap<String, String> data = new HashMap<String, String>();
            	data.put("field", parts[i]);
            	data.put("info", parts[i+1]);
            	messageContentData.add(data);
            }
            
            messagesContent.add(messageContentData);
        }

        SimpleExpandableListAdapter messagesListAdapter = new SimpleExpandableListAdapter(
                this,
                messagesList,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"message", "user"},
                new int[] { android.R.id.text1, android.R.id.text2 },
                messagesContent,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {"field", "info"},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        if(mMessagesList != null)
        	mMessagesList.setAdapter(messagesListAdapter);
    }
	
	private void getMessages() {
        if(DEBUG) Log.e(TAG,"+++ getMessages +++");
        
		// TODO: Communicate with WebService through Rest-Api
        //FIXME: Only for persistent external storage 
        try {
			messages.addAll(mFileManager.getLastLinesFromTo(messages.size(), 10));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, R.string.toast_unable_to_write_external, Toast.LENGTH_LONG).show();
		}
	}
	
	
}
