package com.universityofleipzig.bottlemail;

import java.util.Calendar;
import java.util.EventListener;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;

import com.universityofleipzig.bottlemail.bluetooth2.BluetoothHandler;
import com.universityofleipzig.bottlemail.bluetooth3.MessageCallback;
import com.universityofleipzig.bottlemail.bluetooth3.BtHandler;
import com.universityofleipzig.bottlemail.contentprovider.BottlEMailContentProvider;
import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.Bottle;
import com.universityofleipzig.bottlemail.data.BottleRack;
import com.universityofleipzig.bottlemail.data.MSGEvent;
import com.universityofleipzig.bottlemail.database.BottlEMailTables;
import com.universityofleipzig.bottlemail.helper.GenericListener;
import com.universityofleipzig.bottlemail.webservice.WebserviceHandler;

/**
 * Created by Clemens on 21.05.13.
 */
public class BottleDetails extends SherlockActivity {

	private static final String TAG = "BottleDetails";
	private Context mContext;
	private LayoutInflater mInflater;
	private CardUI mMessageCardView;

	private Bottle mBottle;
	private WebserviceHandler mWebserviceHandler;
	//private BluetoothHandler mBluetoothHandler;
	
	private BtHandler mBtHandler;

	private boolean isInternetPresent;
	
	private MessageCallback mMessageCallback;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e(TAG,"+++ OnCreate +++");
		
		assureInternetConnection();
		
		//mBluetoothHandler = BluetoothHandler.getInstance();
		
		mBtHandler = BtHandler.getInstance();
		createBottleDetailsCB();
		
		setContentView(R.layout.activity_bottledetails);
		mContext = getApplicationContext();
		mInflater = LayoutInflater.from(mContext);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		Bundle extras = getIntent().getExtras();
		
		//Flasche zu der Verbindung aufgebaut werden soll
		mBottle = extras.getParcelable("bottle");

		Toast.makeText(this, mBottle.getBottleName(), Toast.LENGTH_SHORT).show();

		// set actionbar titles
		actionBar.setTitle(mBottle.getBottleName());
		actionBar.setSubtitle(R.string.action_subtitle_compose);

		//MessagesListener ms = new MessagesListener(this);

		// init CardView
		mMessageCardView = (CardUI) findViewById(R.id.activity_bottledetails_cardsview);
		mMessageCardView.setSwipeable(false);
		
		//fill Cards
		
		//TODO: zuerst vom App vorhandene Daten lesen
		//danach Flasche ansprechen
		//danach internet
		
		//TODO: numberOfMessages aus Utilities
		
		mBtHandler.getMessages(mBottle, 3, mMessageCallback);
		
		
		//aus internet laden
//		if ( isInternetPresent ) {
//			Log.e(TAG,"Get All Messages TEST");
//			testGetAllMessages(mBottle, ms);
//		}
	}

	public void OnStart () {
		super.onStart();
		Log.e(TAG,"+++ OnStart +++");
	}
	
	@Override
	public void onRestart () {
		super.onRestart();
		Log.e(TAG,"+++ OnRestart +++");
	}
	
	public Bottle getBottle(int id) {
		return BottleRack.getInstance().getBottle(id);
	}

	public void testSendToWS(Bottle btl, BMail bMail, EventListener listener) {

	}

	public void testGetMessages(Bottle bottle, int numberOfMessages) {
		
		
		
	}

	public void testGetAllMessages(Bottle bottle, GenericListener<MSGEvent> listener) {
		
		
		mWebserviceHandler.asyncGetAllMessages(bottle, listener);
		//mBluetoothHandler.asyncGetAllMessages(bottle, listener);
		
	}

	public void fillBMails(SparseArray<BMail> mails) {
		for (int i = 0; i < mails.size(); i++) {
			mMessageCardView.addCard(new MessageCard(mails.valueAt(i)));
		}
		mMessageCardView.refresh();
	}

	public void fillBMail(BMail mail) {
		Log.e( TAG, "fillBMail:BMail:" +mail);
		MessageCard msg = new MessageCard(mail);
		Log.e( TAG, "fillBMail:MessageCard" +msg.toString());
		mMessageCardView.addCard(msg);
		mMessageCardView.refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.bottledetails, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.homeAsUp:
			finish();
			return true;
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_compose:
			Toast.makeText(this, "Open MessageActivity", Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent(mContext, ComposeMessage.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroy() {
		
		mBtHandler.disconnect();
		//mBtHandler.cancelDiscovery();
		
		super.onDestroy();
	}

	public void addMSG(BMail bm) {
		
		MessageCard mCard = new MessageCard(bm);
		this.mMessageCardView.addCard(mCard);
		this.mMessageCardView.refresh();
	}
	
	
	/**
	 * check if device is online
	 * @return online status
	 */
	public boolean isOnline() {
		
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void UpdateBmail(BMail bmail, int bottleID)
    {
        InsertUpdateBmail(bmail, bottleID, false);
    }
    
    public void InsertBmail(BMail bmail, int bottleID)
    {
        InsertUpdateBmail(bmail, bottleID, true);
    }
    
    private void InsertUpdateBmail(BMail bmail, int bottleID, boolean insert)
    {
        ContentValues values = new ContentValues();
        values.put(BottlEMailTables.BMAILS_COLUMN_AUTHOR, bmail.getAuthor());
        values.put(BottlEMailTables.BMAILS_COLUMN_MESSAGE, bmail.getText());
        values.put(BottlEMailTables.BMAILS_COLUMN_BOTTLE_ID, bottleID);
        values.put(BottlEMailTables.BMAILS_COLUMN_CREATED_AT, bmail.getTimestamp().getTimeInMillis());
        //values.put(BottlEMailTables.BMAILS_COLUMN_IS_DELETED, bmail.);
        values.put(BottlEMailTables.BMAILS_COLUMN_ID, bmail.getBmailID());
        
        if (insert)
            getContentResolver().insert(Uri.parse( BottlEMailContentProvider.CONTENT_URI+"/BMAIL" ), values);
        else 
            getContentResolver().update(Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BMAILID/" + bmail.getBmailID()), values, null, null);
        
        
    }
    
    public SparseArray<BMail> getBmailsOfBottle(int bottleID)
    {
        Uri uri = Uri.parse(BottlEMailContentProvider.CONTENT_URI + "/BMAILS/OF_BOTTLE/" + bottleID);
        
        SparseArray<BMail> resultBmails = new SparseArray<BMail>();
        
        String[] projection = { BottlEMailTables.BMAILS_COLUMN_ID,
                BottlEMailTables.BMAILS_COLUMN_MESSAGE, BottlEMailTables.BMAILS_COLUMN_AUTHOR,
                BottlEMailTables.BMAILS_COLUMN_CREATED_AT, BottlEMailTables.BMAILS_COLUMN_IS_DELETED,
                BottlEMailTables.BMAILS_COLUMN_LOCATION, BottlEMailTables.BMAILS_COLUMN_BOTTLE_ID};
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
            null);
        if (cursor != null)
        {
            String message = "";
            String author = "";
            long creation_ts = 0;
            Calendar creation = Calendar.getInstance();
            int bmailID = -1;
            boolean isDeleted = false;
            Location location;
            
            while (cursor.moveToNext())
            {
                
                message = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BMAILS_COLUMN_MESSAGE));
                author = cursor.getString(cursor.getColumnIndexOrThrow(BottlEMailTables.BMAILS_COLUMN_AUTHOR));
                creation_ts = cursor.getLong(cursor.getColumnIndexOrThrow(BottlEMailTables.BMAILS_COLUMN_CREATED_AT));
                creation.setTimeInMillis(creation_ts);
                isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BMAILS_COLUMN_IS_DELETED)) == 1;
                bmailID = cursor.getInt(cursor.getColumnIndexOrThrow(BottlEMailTables.BMAILS_COLUMN_ID));

                resultBmails.append( bmailID, new BMail( bmailID, message, creation ) );
            }
            cursor.close();
            

        }
        return resultBmails;
        
    }

    
    /**
     * if not activated and user accepted to activate the internet connection, method 
     * shows the internet activation menue of the android os
     * @param context
     * @param title
     * @param message
     * @param status
     */
	public void showAlertDialog(Context context, String title, String message, Boolean status) {

		if ( status == true ) {
			
			new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		} else {
			
			new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Akzeptieren", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BottleDetails.this.finish();
				}
			})
			.show();
		}
	}
	
	/**
	 * asks the user to connect to the internet, if not yet connected
	 */
	
	public void assureInternetConnection(){
		
		Log.d(TAG, "+++++ check internet status +++++");
		// get Internet status
		isInternetPresent = isOnline();
		
		if( isInternetPresent ) {
			
			// Internet Connection is present
		            //showAlertDialog( this, "Internetzugang", 
		            //		"Du bist mit dem Internet verbunden", true);
		            mWebserviceHandler = WebserviceHandler.getInstance();
					
				} else {    
					
					// Internet connection is not present
		            // Ask user to connect to Internet
		            showAlertDialog(this, "Kein Internetzugang",
		                    "Du bist nicht mit dem Internet verbunden. Möchtest du es jetzt aktivieren?", false);
		           
				}
		
	}
	
	private void createBottleDetailsCB(){
		Log.d(TAG, "+++++ create BottleDetailsCallback");
		mMessageCallback = new MessageCallback(){
			@Override
			public void incomingData(int id, String author, String title, String text){
				Log.d(TAG + " ActivityCallback", "+++++ incomingData +++++");
				
				try {
					BMail bmail;
					if(mBottle.bMailExists(id)){
						bmail = mBottle.getBMail(id);
					}
					else{
						 bmail = mBottle.createNewBMail(id, text, author, Calendar.getInstance(), false);
						 
						 //InsertBmail(bmail, id);
					}
					
					addMSG(bmail);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
}