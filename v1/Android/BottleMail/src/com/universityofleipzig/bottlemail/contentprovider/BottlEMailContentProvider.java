package com.universityofleipzig.bottlemail.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import com.universityofleipzig.bottlemail.database.BottlEMailDatabaseHelper;
import com.universityofleipzig.bottlemail.database.BottlEMailTables;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BottlEMailContentProvider extends ContentProvider
{

    // database
    private BottlEMailDatabaseHelper database;

    // Used for the UriMacher
    private static final int BMAIL = 10;
    private static final int BOTTLE = 20;
    private static final int BMAIL_ID = 30;
    private static final int BOTTLE_ID = 40;
    private static final int BMAILS_OF_BOTTLE = 50;
    private static final int FOUND_BOTTLES = 60;

    private static final String AUTHORITY = "com.universityofleipzig.bottlemail.contentprovider.bottlemailcontentprovider";

    private static final String BASE_PATH = "bmails";
    public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY
            + "/" + BASE_PATH );

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/records";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/record";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH );
    static
    {
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BMAIL", BMAIL );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BMAILID/#", BMAIL_ID );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BMAILS/OF_BOTTLE/#",
                BMAILS_OF_BOTTLE );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BOTTLE", BOTTLE );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BOTTLEID/#", BOTTLE_ID );
        sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/BOTTLES/FOUND",
                FOUND_BOTTLES );
    }

    @Override
    public boolean onCreate()
    {
        database = new BottlEMailDatabaseHelper( getContext() );
        return false;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder )
    {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        

        int uriType = sURIMatcher.match( uri );
        switch ( uriType )
        {
            case BMAIL_ID:
                checkBmailsColumns( projection );
                queryBuilder.setTables( BottlEMailTables.BMAILS_TABLE );
                queryBuilder.appendWhere( BottlEMailTables.BMAILS_COLUMN_ID
                        + "=" + uri.getLastPathSegment() );
                break;
            case BMAILS_OF_BOTTLE:
                checkBmailsColumns( projection );
                queryBuilder.setTables( BottlEMailTables.BMAILS_TABLE );
                queryBuilder
                        .appendWhere( BottlEMailTables.BMAILS_COLUMN_BOTTLE_ID
                                + "=" + uri.getLastPathSegment() );
                sortOrder = BottlEMailTables.BMAILS_COLUMN_CREATED_AT;
                break;

            case BOTTLE_ID:
                checkBottlesColumns( projection );
                queryBuilder.setTables( BottlEMailTables.BOTTLES_TABLE );
                queryBuilder.appendWhere( BottlEMailTables.BOTTLES_COLUMN_ID
                        + "=" + uri.getLastPathSegment() );
                break;
            case FOUND_BOTTLES:
                checkBottlesColumns( projection );
                queryBuilder.setTables( BottlEMailTables.BOTTLES_TABLE );
                queryBuilder.appendWhere( BottlEMailTables.BOTTLES_COLUMN_FOUND
                        + "= 1" );
                break;
            default:
                throw new IllegalArgumentException( "Unknown URI: " + uri );
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query( db, projection, selection,
                selectionArgs, null, null, sortOrder );
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri( getContext().getContentResolver(), uri );

        return cursor;
    }

    @Override
    public String getType( Uri uri )
    {
        return null;
    }

    @Override
    public Uri insert( Uri uri, ContentValues values )
    {
        int uriType = sURIMatcher.match( uri );
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch ( uriType )
        {
            case BMAIL:
                id = sqlDB.insert( BottlEMailTables.BMAILS_TABLE, null, values );
                break;
            case BOTTLE:
                id = sqlDB
                        .insert( BottlEMailTables.BOTTLES_TABLE, null, values );
                break;
            default:
                throw new IllegalArgumentException( "Unknown URI: " + uri );
        }
        getContext().getContentResolver().notifyChange( uri, null );
        return Uri.parse( BASE_PATH + "/" + id );
    }

    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs )
    {
        int uriType = sURIMatcher.match( uri );
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        String id;
        switch ( uriType )
        {
            case BMAIL_ID:
                id = uri.getLastPathSegment();
                if ( TextUtils.isEmpty( selection ) )
                {
                    rowsDeleted = sqlDB.delete( BottlEMailTables.BMAILS_TABLE,
                            BottlEMailTables.BMAILS_COLUMN_ID + "=" + id, null );
                } else
                {
                    rowsDeleted = sqlDB.delete( BottlEMailTables.BMAILS_TABLE,
                            BottlEMailTables.BMAILS_COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs );
                }
                break;
            case BOTTLE_ID:
                id = uri.getLastPathSegment();
                if ( TextUtils.isEmpty( selection ) )
                {
                    rowsDeleted = sqlDB
                            .delete( BottlEMailTables.BOTTLES_TABLE,
                                    BottlEMailTables.BOTTLES_COLUMN_ID + "="
                                            + id, null );
                } else
                {
                    rowsDeleted = sqlDB.delete( BottlEMailTables.BOTTLES_TABLE,
                            BottlEMailTables.BOTTLES_COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs );
                }
                break;
            default:
                throw new IllegalArgumentException( "Unknown URI: " + uri );
        }
        getContext().getContentResolver().notifyChange( uri, null );
        return rowsDeleted;
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection,
            String[] selectionArgs )
    {

        int uriType = sURIMatcher.match( uri );
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        String id = uri.getLastPathSegment();
        switch ( uriType )
        {
            case BMAIL_ID:
                id = uri.getLastPathSegment();
                if ( TextUtils.isEmpty( selection ) )
                {
                    rowsUpdated = sqlDB.update( BottlEMailTables.BMAILS_TABLE,
                            values, BottlEMailTables.BMAILS_COLUMN_ID + "="
                                    + id, null );
                } else
                {
                    rowsUpdated = sqlDB.update( BottlEMailTables.BMAILS_TABLE,
                            values, BottlEMailTables.BMAILS_COLUMN_ID + "="
                                    + id + " and " + selection, selectionArgs );
                }
                break;
            case BOTTLE_ID:
                id = uri.getLastPathSegment();
                if ( TextUtils.isEmpty( selection ) )
                {
                    rowsUpdated = sqlDB.update( BottlEMailTables.BOTTLES_TABLE,
                            values, BottlEMailTables.BOTTLES_COLUMN_ID + "="
                                    + id, null );
                } else
                {
                    rowsUpdated = sqlDB.update( BottlEMailTables.BOTTLES_TABLE,
                            values, BottlEMailTables.BOTTLES_COLUMN_ID + "="
                                    + id + " and " + selection, selectionArgs );
                }
                break;

            default:
                throw new IllegalArgumentException( "Unknown URI: " + uri );
        }
        getContext().getContentResolver().notifyChange( uri, null );
        return rowsUpdated;
    }

    private void checkBottlesColumns( String[] projection )
    {
        String[] available = { BottlEMailTables.BOTTLES_COLUMN_ID,
                BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE,
                BottlEMailTables.BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS,
                BottlEMailTables.BOTTLES_COLUMN_FOUND,
                BottlEMailTables.BOTTLES_COLUMN_MAC,
                BottlEMailTables.BOTTLES_COLUMN_NAME,
                BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMINOR,
                BottlEMailTables.BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR };
        if ( projection != null )
        {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList( projection ) );
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList( available ) );
            // Check if all columns which are requested are available
            if ( !availableColumns.containsAll( requestedColumns ) )
            {
                throw new IllegalArgumentException(
                        "Unknown columns in projection" );
            }
        }
    }
    
    
    private void checkBmailsColumns( String[] projection )
    {
        String[] available = { BottlEMailTables.BMAILS_COLUMN_ID,
                BottlEMailTables.BMAILS_COLUMN_BOTTLE_ID,
                BottlEMailTables.BMAILS_COLUMN_AUTHOR,
                BottlEMailTables.BMAILS_COLUMN_CREATED_AT,
                BottlEMailTables.BMAILS_COLUMN_IS_DELETED,
                BottlEMailTables.BMAILS_COLUMN_LOCATION,
                BottlEMailTables.BMAILS_COLUMN_MESSAGE };
        if ( projection != null )
        {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList( projection ) );
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList( available ) );
            // Check if all columns which are requested are available
            if ( !availableColumns.containsAll( requestedColumns ) )
            {
                throw new IllegalArgumentException(
                        "Unknown columns in projection" );
            }
        }
    }

}
