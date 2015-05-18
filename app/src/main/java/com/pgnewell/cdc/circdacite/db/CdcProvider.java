package com.pgnewell.cdc.circdacite.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by paul on 5/6/15.
 */
public class CdcProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CdcDbAdapter.DatabaseHelper mOpenHelper;
    private final String LOG_TAG = CdcProvider.class.getSimpleName();

    // a bunch of constants determining several types of queries destined to become
    // URIs used for queries and perhaps other db operations
    static final int LOCATION = 100;
    static final int LOCATION_BY_NAME = 101;
    static final int PATH = 200;
    static final int PATH_BY_NAME = 201;
    static final int PATH_LOCATION = 300;
    static final int PATH_VIEW = 400;
    static final int BIKE_STATION = 500;
    static final int BIKE_STATION_NEAR = 504;


    @Override
    public boolean onCreate() {
        mOpenHelper = new CdcDbAdapter.DatabaseHelper(getContext());
        return true;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        List<String> columns = Arrays.asList(
                CdcContract.BikeStationEntry.COLUMN_INSTALL_DATE,
                CdcContract.BikeStationEntry.COLUMN_REMOVAL_DATE
        );
        for (String column: columns) {
            if (values.containsKey(column)) {
                long dateValue = values.getAsLong(column);
                values.put(column, CdcContract.normalizeDate(dateValue));
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        if (selection == null) selection = "1";

        switch (match) {
            case LOCATION_BY_NAME:
            case PATH_BY_NAME:
            case BIKE_STATION_NEAR:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        String tableName = tableName(match);
        returnCount = db.delete(tableName, selection, selectionArgs);
        if ( returnCount > 0 )
            getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
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

        // Match each type defined above according to the way that the query will be made
        // apparently additional parameters can be added in a way that is not entirely clear to me
        // right now.
        matcher.addURI(authority, CdcContract.PATH_PATH, PATH);
        matcher.addURI(authority, CdcContract.PATH_PATH + "/*", PATH_BY_NAME);
        matcher.addURI(authority, CdcContract.PATH_PATH_VIEW, PATH_VIEW);
        matcher.addURI(authority, CdcContract.PATH_LOCATION, LOCATION );
        matcher.addURI(authority, CdcContract.PATH_LOCATION + "/*", LOCATION_BY_NAME );
        matcher.addURI(authority, CdcContract.PATH_PATH_LOCATION, PATH_LOCATION);
        matcher.addURI(authority, CdcContract.PATH_BIKE_STATION, BIKE_STATION);
        matcher.addURI(authority, CdcContract.PATH_BIKE_STATION + "/lat/*/lon/*", BIKE_STATION_NEAR);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {

        // Uri Matcher sorts this into one of following constants (which are what?)
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PATH:
                return CdcContract.PathEntry.CONTENT_TYPE;
            case PATH_VIEW:
                return CdcContract.PathViewEntry.CONTENT_TYPE;
            case PATH_BY_NAME:
                return CdcContract.PathEntry.CONTENT_ITEM_TYPE;
            case PATH_LOCATION:
                return CdcContract.PathLocationEntry.CONTENT_TYPE;
            case BIKE_STATION:
            case BIKE_STATION_NEAR:
                return CdcContract.BikeStationEntry.CONTENT_TYPE;
            case LOCATION:
                return CdcContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_BY_NAME:
                return CdcContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LOCATION: {
                normalizeDate(values);
                long _id = db.insert(CdcContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CdcContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PATH: {
                normalizeDate(values);
                long _id = db.insert(CdcContract.PathEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CdcContract.PathEntry.buildPathUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BIKE_STATION: {
                normalizeDate(values);
                long _id = db.insert(CdcContract.BikeStationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CdcContract.BikeStationEntry.buildPathUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case LOCATION_BY_NAME:
            case PATH_BY_NAME:
            case BIKE_STATION_NEAR:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        String tableName = tableName(match);
        returnCount = db.update(tableName, values, selection, selectionArgs);
        if ( returnCount > 0 )
            getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        String tableName = tableName(match);
        switch (match) {
            // "weather/*/*"
            case LOCATION:
            case LOCATION_BY_NAME:
            case PATH:
            case PATH_BY_NAME:
            case PATH_LOCATION:
            case BIKE_STATION:
            case BIKE_STATION_NEAR:
                retCursor = mOpenHelper.getReadableDatabase().query(tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private String tableName(int query) {
        String tableName;

        switch (query) {
            case LOCATION:
            case LOCATION_BY_NAME:
                tableName = CdcContract.LocationEntry.TABLE_NAME;
                break;
            case PATH:
            case PATH_BY_NAME:
                tableName = CdcContract.PathEntry.TABLE_NAME;
                break;
            case PATH_LOCATION:
                tableName = CdcContract.PathLocationEntry.TABLE_NAME;
                break;
            case BIKE_STATION:
            case BIKE_STATION_NEAR:
                tableName = CdcContract.BikeStationEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + query);
        }
        return tableName;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKE_STATION:
                db.beginTransaction();
                // These are stations so every bulk insert replaces all existing entries.
                // remove all current entries beforehand
                // TODO: Will generate too many id's -- need to address that
                int deleted = delete(uri,"1",null);
                Log.d(LOG_TAG, "deleted " + deleted + " records");
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(CdcContract.BikeStationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
