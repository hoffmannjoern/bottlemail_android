package uni.leipzig.bm2.db;

import java.util.ArrayList;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BottlesSQLiteDataAccess {

	private static final boolean DEBUG = BottleMailConfig.DB_DEBUG;	
    private final static String TAG = BottleRack.class.getSimpleName();

	// Database fields
	private SQLiteDatabase database;
	private BottlEMailSQLiteHelper dbHelper;
	private String[] allColumns = {
		dbHelper.BOTTLES_COLUMN_ID, 
		dbHelper.BOTTLES_COLUMN_BOTTLE_ID,
		dbHelper.BOTTLES_COLUMN_MAC, 
		dbHelper.BOTTLES_COLUMN_NAME, 
		dbHelper.BOTTLES_COLUMN_FOUND_DATE,
		dbHelper.BOTTLES_COLUMN_IS_DELETED,
		dbHelper.BOTTLES_COLUMN_LATITUDE, 
		dbHelper.BOTTLES_COLUMN_LONGITUDE,
		dbHelper.BOTTLES_COLUMN_COLOR, 
		dbHelper.BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLE,
		dbHelper.BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLEFROMWS, 
		dbHelper.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR,
		dbHelper.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR,
	};
	
	public BottlesSQLiteDataAccess(Context context) {
		if(DEBUG) Log.e(TAG, "+++ Constructor +++");
		
		dbHelper = new BottlEMailSQLiteHelper(context);
	}

	public void open() throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ open +++");
		
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		if(DEBUG) Log.e(TAG, "+++ close +++");
		
		dbHelper.close();
	}

	public boolean exists ( String mac ) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ exists +++");
		
		database = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(allColumns[2], mac);
		
		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				allColumns, 
				BottlEMailSQLiteHelper.BOTTLES_COLUMN_MAC + " = '" + mac + "'", 
				null, null, null, null);
		if ( cursor.getCount() > 0 ) {
			
			cursor.close();
			return true;
		} else 
			return false;
	}

	public Bottle setQuery ( String query ) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ setQuery +++");
		
		database = dbHelper.getReadableDatabase();
		
		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				allColumns, query , null, null, null, null);

		if ( cursor.getCount() > 0 ) {
			
			cursor.moveToFirst();
			Bottle hitBottle = cursorToBottle(cursor);
			cursor.close();
			return hitBottle;
		} else {	
			cursor.close();
			return null;
		}
	}

	public Bottle readBottle ( long insertID ) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ redBottle(dbID) +++");
		
		database = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(allColumns[0], insertID);
		
		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				allColumns, 
				BottlEMailSQLiteHelper.BOTTLES_COLUMN_ID + " = " + insertID, 
				null, null, null, null);

		if ( cursor.getCount() > 0 ) {
			
			cursor.moveToFirst();
			Bottle readBottle = cursorToBottle(cursor);
			cursor.close();
			return readBottle;
		} else {	
			cursor.close();
			return null;
		}
	}
	
	public Bottle readBottle(String mac) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ readBottle(mac) +++");
		
		database = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(allColumns[2], mac);
		
		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				allColumns, 
				BottlEMailSQLiteHelper.BOTTLES_COLUMN_MAC + " = '" + mac + "'", 
				null, null, null, null);

		if ( cursor.getCount() > 0 ) {
			
			cursor.moveToFirst();
			Bottle readBottle = cursorToBottle(cursor);
			cursor.close();
			return readBottle;
		} else {	
			cursor.close();
			return null;
		}
	}
	
	/**
	 * build bottle from bottle detail array (strings) and store it in db
	 * @param bottleDetails static String-Array with bottle information
	 * @return
	 * @throws SQLException
	 */
	public Bottle createBottle(String[] bottleDetails) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ createBottle +++");
		
		ContentValues values = new ContentValues();
		for (int i=1; i < allColumns.length; i++) {
			values.put(allColumns[i], bottleDetails[i-1]);
		}
		
		long insertID = database.insert(
				BottlEMailSQLiteHelper.BOTTLES_TABLE, null, values);
		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE, 
				allColumns, 
				BottlEMailSQLiteHelper.BOTTLES_COLUMN_BOTTLE_ID 
				+ " = " + insertID, 
				null, null, null, null);
		
		if ( cursor.getCount() > 0 ) {
			cursor.moveToFirst();
			Bottle newBottle = cursorToBottle(cursor);
			cursor.close();
			return newBottle;
		} else
			return null;
	}

	public boolean storeBottle(Bottle bottle) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ Store bottle in DB +++");
		
		ContentValues values = new ContentValues();
		values.put(allColumns[1], bottle.getBottleID());
		values.put(allColumns[2], bottle.getMac());
		values.put(allColumns[3], bottle.getBottleName());
		values.put(allColumns[4], bottle.getFoundDate());
		values.put(allColumns[5], bottle.getDeleteDate());
		values.put(allColumns[6], bottle.getLatitude());
		values.put(allColumns[7], bottle.getLongitude());
		values.put(allColumns[8], bottle.getColor());
		values.put(allColumns[9], bottle.getNumberOfMsgsOnBottle());
		values.put(allColumns[10],bottle.getNumberOfMsgsOnBottleFromWS());
		values.put(allColumns[11],bottle.getProtocolVersionMajor());
		values.put(allColumns[12],bottle.getProtocolVersionMinor());
		
		long insertID = database.insert(
				BottlEMailSQLiteHelper.BOTTLES_TABLE, null, values);
			
		if ( readBottle(insertID) != null ) {
			return true;
		} else
			return false;
	}

	public void updateWithMac(Bottle bottle) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ updateWithMac +++");
		
		ContentValues values = new ContentValues();
		values.put(allColumns[1], bottle.getBottleID());
		values.put(allColumns[3], bottle.getBottleName());
		values.put(allColumns[4], bottle.getFoundDate());
		values.put(allColumns[5], bottle.getDeleteDate());
		values.put(allColumns[6], bottle.getLatitude());
		values.put(allColumns[7], bottle.getLongitude());
		values.put(allColumns[8], bottle.getColor());
		values.put(allColumns[9], bottle.getNumberOfMsgsOnBottle());
		values.put(allColumns[10],bottle.getNumberOfMsgsOnBottleFromWS());
		values.put(allColumns[11],bottle.getProtocolVersionMajor());
		values.put(allColumns[12],bottle.getProtocolVersionMinor());
		
		database.update(BottlEMailSQLiteHelper.BOTTLES_TABLE, values,
				allColumns[2] + " = '" + bottle.getMac() + "'", null);
	}

	public void updateWithBottleID(Bottle bottle) throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ updateWithID +++");
		
		ContentValues values = new ContentValues();
		values.put(allColumns[2], bottle.getMac());
		values.put(allColumns[3], bottle.getBottleName());
		values.put(allColumns[4], bottle.getFoundDate());
		values.put(allColumns[5], bottle.getDeleteDate());
		values.put(allColumns[6], bottle.getLatitude());
		values.put(allColumns[7], bottle.getLongitude());
		values.put(allColumns[8], bottle.getColor());
		values.put(allColumns[9], bottle.getNumberOfMsgsOnBottle());
		values.put(allColumns[10],bottle.getNumberOfMsgsOnBottleFromWS());
		values.put(allColumns[11],bottle.getProtocolVersionMajor());
		values.put(allColumns[12],bottle.getProtocolVersionMinor());
		
		database.update(BottlEMailSQLiteHelper.BOTTLES_TABLE, values,
				allColumns[1] + " = " + bottle.getBottleID(), null);
	}

	public void deleteBottle(Bottle bottle) {
		if(DEBUG) Log.e(TAG, "+++ deleteVideo +++");	
		
		String mac = bottle.getMac();
		System.out.println("Bottle deleted with mac: " + mac);
		database.delete(BottlEMailSQLiteHelper.BOTTLES_TABLE, 
				BottlEMailSQLiteHelper.BOTTLES_COLUMN_MAC
				+ " = '" + mac + "'", null);
	}
	
	public ArrayList<Bottle> getAllBottles() {
		if(DEBUG) Log.e(TAG, "+++ getAllBottles +++");
		
		ArrayList<Bottle> bottles = new ArrayList<Bottle>();

		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				allColumns, null, null, null, null, null);

		if ( cursor.getCount() > 0 ) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {

				Bottle bottle = cursorToBottle(cursor);
				bottles.add(bottle);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return bottles;
		} else 
			return null;
	}

	private Bottle cursorToBottle(Cursor cursor) {
		if(DEBUG) Log.e(TAG, "+++ cursorToBottle +++");
		
		Bottle bottle = new Bottle(
				cursor.getInt(1), 		//bottleID
				cursor.getString(3),  	//name 
				cursor.getString(2));	//mac
		bottle.setFoundDate(cursor.getString(4));
		bottle.setDeleteDate(cursor.getString(5));
		bottle.setLatitude(cursor.getDouble(6));
		bottle.setLongitude(cursor.getDouble(7));
		bottle.setColor(cursor.getInt(8));
		bottle.setNumberOfMsgsOnBottle(cursor.getInt(9));
		bottle.setNumberOfMsgsOnBottleFromWS(cursor.getInt(10));
		bottle.setProtocolVersionMajor(cursor.getString(11));
		bottle.setProtocolVersionMinor(cursor.getString(12));
		return bottle;
	}
}