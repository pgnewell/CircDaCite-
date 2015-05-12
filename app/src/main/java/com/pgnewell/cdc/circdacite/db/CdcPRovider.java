package com.pgnewell.cdc.circdacite.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by paul on 5/6/15.
 */
public class CdcProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CdcDbAdapter mOpenHelper;

    static final int LOCATION = 100;
    static final int PATH = 200;
    static final int PATH_LOCATION = 300;
    static final int BIKE_STATION = 400;


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

/*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the integer constants defined above.  You can test this by uncommenting
        the testUriMatcher test within TestUriMatcher.
     */

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CdcContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, CdcContract.PATH_PATH, PATH);
        matcher.addURI(authority, CdcContract.PATH_LOCATION, LOCATION );
        matcher.addURI(authority, CdcContract.PATH_BIKE_STATION, BIKE_STATION);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases

            case PATH:
                return CdcContract.PathEntry.CONTENT_TYPE;
            case PATH_LOCATION:
                return CdcContract.PathEntry.CONTENT_TYPE;
            case BIKE_STATION:
                return CdcContract.BikeStationEntry.CONTENT_TYPE;
            case LOCATION:
                return CdcContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }
}
