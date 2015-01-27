package com.pgnewell.cdc.circdacite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by pgn on 12/28/14.
 */
public class LocationsDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";

    private static final String TAG = "LocationsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private boolean mWritable;

    private static final String DATABASE_NAME = "CDC";
    private static final String SQLITE_TABLE = "locations";
    private static final int DATABASE_VERSION = 3;

    protected final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context ctx;
        private String createText;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            InputStream str = context.getResources().openRawResource( R.raw.create_db);
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            int size = 0;
            byte[] buffer = new byte[1024];
            try {
                while((size=str.read(buffer,0,1024))>=0) outputStream.write(buffer, 0, size);
                str.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            createText = outputStream.toString();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, createText);
            db.execSQL(createText);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public LocationsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public LocationsDbAdapter open(boolean writable) throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        if (writable)
            mDb = mDbHelper.getWritableDatabase();
        else
            mDb = mDbHelper.getReadableDatabase();

        mWritable = writable;
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createLocation(Location loc) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, loc.getName());
        initialValues.put(KEY_ADDRESS, loc.getAddress());
        initialValues.put(KEY_LAT, loc.getLat());
        initialValues.put(KEY_LONG, loc.getLng());

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public boolean deleteAllLocations() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchLocationsByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, KEY_LAT, KEY_LONG},
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, KEY_LAT, KEY_LONG},
                    KEY_NAME + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchPathsByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query("paths", new String[] {KEY_ROWID,KEY_NAME },
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, KEY_LAT, KEY_LONG},
                    KEY_NAME + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllLocations() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_NAME, KEY_ADDRESS, KEY_LAT, KEY_LONG},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllPaths() {

        Cursor mCursor = mDb.query("paths", new String[] {KEY_ROWID,KEY_NAME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}
