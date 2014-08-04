package com.universityofleipzig.bottlemail.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.universityofleipzig.bottlemail.MessagesListener;
import com.universityofleipzig.bottlemail.contentprovider.BottlEMailContentProvider;
import com.universityofleipzig.bottlemail.data.BMail;
import com.universityofleipzig.bottlemail.data.Bottle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

public class BottlEMailDatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "bottlemail.db";
    private static final int DATABASE_VERSION = 2;

    public BottlEMailDatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
      BottlEMailTables.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
        int newVersion) {
        BottlEMailTables.onUpgrade(database, oldVersion, newVersion);
    }
      
}
