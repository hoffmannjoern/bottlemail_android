package com.universityofleipzig.bottlemail.database;

import java.util.Calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class BottlEMailTables
{

    public static final String BMAILS_TABLE = "Bmails";
    public static final String BMAILS_COLUMN_ID = "ID";
    public static final String BMAILS_COLUMN_BOTTLE_ID = "bottle_ID";
    public static final String BMAILS_COLUMN_MESSAGE = "message";
    public static final String BMAILS_COLUMN_AUTHOR = "author";
    public static final String BMAILS_COLUMN_CREATED_AT = "createdat";
    public static final String BMAILS_COLUMN_IS_DELETED = "isdeleted";
    public static final String BMAILS_COLUMN_LOCATION = "location";
    
    public static final String BOTTLES_TABLE = "Bottles";
    public static final String BOTTLES_COLUMN_ID = "ID";
    public static final String BOTTLES_COLUMN_NAME = "name";
    public static final String BOTTLES_COLUMN_FOUND = "found";
    public static final String BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE = "absoluteTotalNumberOfMsgsOnBottle";
    public static final String BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS = "absoluteTotalNumberOfMsgsOnBottleFromWS";
    public static final String BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR = "protocolVersionMajor";
    public static final String BOTTLES_COLUMN_PROTOCOLVERSIONMINOR = "protocolVersionMinor";
    public static final String BOTTLES_COLUMN_MAC = "mac";
    
    // Database creation sql statement
    private static final String CREATE_TABLE_BMAILS = "create table "
        + BMAILS_TABLE + "(" 
        + BMAILS_COLUMN_ID + " integer primary key, " 
        + BMAILS_COLUMN_BOTTLE_ID + " integer, " 
        + BMAILS_COLUMN_MESSAGE + " text not null, " 
        + BMAILS_COLUMN_AUTHOR + " text not null, " 
        + BMAILS_COLUMN_CREATED_AT + " integer, " 
        + BMAILS_COLUMN_IS_DELETED + " integer, " 
        + BMAILS_COLUMN_LOCATION + " integer ) ";
    private static final String CREATE_TABLE_BOTTLES =  "create table "
        + BOTTLES_TABLE + "(" 
        + BOTTLES_COLUMN_ID + " integer primary key, " 
        + BOTTLES_COLUMN_NAME + " text not null, " 
        + BOTTLES_COLUMN_MAC + " text not null, " 
        + BOTTLES_COLUMN_FOUND + " integer not null, " 
        + BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLE + " integer, " 
        + BOTTLES_COLUMN_ABSOLUTETOTALNUMBEROFMSGSONBOTTLEFROMWS + " integer, " 
        + BOTTLES_COLUMN_PROTOCOLVERSIONMAJOR + " integer, "
        + BOTTLES_COLUMN_PROTOCOLVERSIONMINOR + " integer )";

    public static void onCreate(SQLiteDatabase database) {
      database.execSQL(CREATE_TABLE_BMAILS);
      database.execSQL(CREATE_TABLE_BOTTLES);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(BottlEMailTables.class.getName(),
          "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + BMAILS_TABLE);
      onCreate(db);
    }

}
