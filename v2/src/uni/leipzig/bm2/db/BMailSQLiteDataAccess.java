package uni.leipzig.bm2.db;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.BottleRack;
import android.database.sqlite.SQLiteDatabase;


public class BMailSQLiteDataAccess {

	private static final boolean DEBUG = BottleMailConfig.DB_DEBUG;	
    private final static String TAG = BottleRack.class.getSimpleName();

	// Database fields
	private SQLiteDatabase database;
	private BottlEMailSQLiteHelper bmDbHelper;
	private String[] bMailAllColumns = {
			bmDbHelper.BMAILS_COLUMN_ID, bmDbHelper.BMAILS_COLUMN_BOTTLE_ID, 
			bmDbHelper.BMAILS_COLUMN_MESSAGE_ID, bmDbHelper.BMAILS_COLUMN_MESSAGE, 
			bmDbHelper.BMAILS_COLUMN_AUTHOR, bmDbHelper.BMAILS_COLUMN_CREATED_AT, 
			bmDbHelper.BMAILS_COLUMN_IS_DELETED, bmDbHelper.BMAILS_COLUMN_LATITUDE, 
			bmDbHelper.BMAILS_COLUMN_LONGITUDE,	
	};

}
