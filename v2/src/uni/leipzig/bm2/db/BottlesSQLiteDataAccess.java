package uni.leipzig.bm2.db;

import java.util.ArrayList;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.Bottle;
import uni.leipzig.bm2.data.BottleRack;
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
	private BottlEMailSQLiteHelper bmDbHelper;
	private String[] bottlesAllColumns = {
		bmDbHelper.BOTTLES_COLUMN_ID, 
		bmDbHelper.BOTTLES_COLUMN_BOTTLE_ID,
		bmDbHelper.BOTTLES_COLUMN_MAC, 
		bmDbHelper.BOTTLES_COLUMN_NAME, 
		bmDbHelper.BOTTLES_COLUMN_FOUND_DATE,
		bmDbHelper.BOTTLES_COLUMN_IS_DELETED,
		bmDbHelper.BOTTLES_COLUMN_LATITUDE, 
		bmDbHelper.BOTTLES_COLUMN_LONGITUDE,
		bmDbHelper.BOTTLES_COLUMN_COLOR, 
		bmDbHelper.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE,
		bmDbHelper.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS, 
		bmDbHelper.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR,
		bmDbHelper.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR,
	};
	
	public BottlesSQLiteDataAccess(Context context) {
		if(DEBUG) Log.e(TAG, "+++ Constructor +++");
		
		bmDbHelper = new BottlEMailSQLiteHelper(context);
	}

	public void open() throws SQLException {
		if(DEBUG) Log.e(TAG, "+++ open +++");
		
		database = bmDbHelper.getWritableDatabase();
	}

	public void close() {
		if(DEBUG) Log.e(TAG, "+++ close +++");
		
		bmDbHelper.close();
	}

	public ArrayList<Bottle> getAllBottles() {
		if(DEBUG) Log.e(TAG, "+++ getAllBottles +++");
		
		ArrayList<Bottle> bottles = new ArrayList<Bottle>();

		Cursor cursor = database.query(BottlEMailSQLiteHelper.BOTTLES_TABLE,
				bottlesAllColumns, null, null, null, null, null);

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
				cursor.getInt(0), 
				cursor.getString(1), 
				cursor.getString(2));
		bottle.setFoundDate(cursor.getString(3));
//		bottle.setDuration(cursor.getString(4));
//		bottle.setThumbnail(cursor.getString(5));
//		bottle.setDescription(cursor.getString(6));
//		bottle.setmd5(cursor.getString(7));
		return bottle;
	}
}







