package uni.leipzig.bm2.db;

import uni.leipzig.bm2.config.BottleMailConfig;
import uni.leipzig.bm2.data.BottleRack;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BottlEMailSQLiteHelper extends SQLiteOpenHelper {

	private static final boolean DEBUG = BottleMailConfig.DB_DEBUG;	
    private final static String TAG = BottleRack.class.getSimpleName();

    public static final String BMAILS_TABLE = "Bmails";
    public static final String BMAILS_COLUMN_ID = "ID";
    public static final String BMAILS_COLUMN_BOTTLE_ID = "bottle_ID";
    public static final String BMAILS_COLUMN_MESSAGE_ID = "message_ID";
    public static final String BMAILS_COLUMN_MESSAGE = "message";
    public static final String BMAILS_COLUMN_AUTHOR = "author";
    public static final String BMAILS_COLUMN_CREATED_AT = "createdat";
    public static final String BMAILS_COLUMN_IS_DELETED = "isdeleted";
    public static final String BMAILS_COLUMN_LATITUDE = "latitude";
    public static final String BMAILS_COLUMN_LONGITUDE = "longitude";
    
    //FIXME: What do we need?
    public static final String BOTTLES_TABLE = "Bottles";
    public static final String BOTTLES_COLUMN_ID = "ID";
    public static final String BOTTLES_COLUMN_BOTTLE_ID = "bottleID";
    public static final String BOTTLES_COLUMN_MAC = "mac";
    public static final String BOTTLES_COLUMN_NAME = "bottleName";
    public static final String BOTTLES_COLUMN_FOUND_DATE = "foundDate";
    public static final String BOTTLES_COLUMN_IS_DELETED = "isDeleted";
    public static final String BOTTLES_COLUMN_LATITUDE = "latitude";
    public static final String BOTTLES_COLUMN_LONGITUDE = "longitude";
    public static final String BOTTLES_COLUMN_COLOR = "color";
    public static final String BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLE = "absoluteTotalNumberOfMsgsOnBottle";
    public static final String BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLEFROMWS = "absoluteTotalNumberOfMsgsOnBottleFromWS";
    public static final String BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR = "protocolVersionMajor";
    public static final String BOTTLES_COLUMN_PROTOCOLVERSIONMINOR = "protocolVersionMinor";
    
    private static final String DATABASE_NAME = "bottlemail.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String CREATE_TABLE_BMAILS = "create table "
        + BMAILS_TABLE + "(" 
        + BMAILS_COLUMN_ID + " integer primary key, " 
        + BMAILS_COLUMN_BOTTLE_ID + " integer, "  
        + BMAILS_COLUMN_MESSAGE_ID + " integer, " 
        + BMAILS_COLUMN_MESSAGE + " text not null, " 
        + BMAILS_COLUMN_AUTHOR + " text not null, " 
        + BMAILS_COLUMN_CREATED_AT + " integer, " 
        + BMAILS_COLUMN_IS_DELETED + " integer, " 
        + BMAILS_COLUMN_LATITUDE + " text not null, " 
        + BMAILS_COLUMN_LONGITUDE + " text not null ) ";
    
    private static final String CREATE_TABLE_BOTTLES =  "create table "
        + BOTTLES_TABLE + "(" 
        + BOTTLES_COLUMN_ID + " integer primary key, " 
        + BOTTLES_COLUMN_BOTTLE_ID + " integer, "
        + BOTTLES_COLUMN_MAC + " text not null, " 
        + BOTTLES_COLUMN_NAME + " text not null, " 
        + BOTTLES_COLUMN_FOUND_DATE + " integer not null, " 
        + BOTTLES_COLUMN_IS_DELETED + " integer, " 
        + BOTTLES_COLUMN_LATITUDE + " text not null, " 
        + BOTTLES_COLUMN_LONGITUDE + " text not null, " 
        + BOTTLES_COLUMN_COLOR + " integer not null, " 
        + BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLE + " integer, " 
        + BOTTLES_COLUMN_NUMBEROFMSGSONBOTTLEFROMWS + " integer, " 
        + BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR + " integer, "
        + BOTTLES_COLUMN_PROTOCOLVERSIONMINOR + " integer )";

    public BottlEMailSQLiteHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
		if(DEBUG) Log.e(TAG, "+++ Constructor +++");	
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
		if(DEBUG) Log.e(TAG, "+++ onCreate +++");
		
		database.execSQL(CREATE_TABLE_BMAILS);
		database.execSQL(CREATE_TABLE_BOTTLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if(DEBUG) Log.e(TAG, "+++ onUpgrade +++");
		
    	Log.w(BMailSQLiteDataAccess.class.getName(),
    			"Upgrading database from version " + oldVersion + " to "
    					+ newVersion + ", which will destroy all old data");
    	db.execSQL("DROP TABLE IF EXISTS " + BMAILS_TABLE);
    	onCreate(db);
    }
      
}
