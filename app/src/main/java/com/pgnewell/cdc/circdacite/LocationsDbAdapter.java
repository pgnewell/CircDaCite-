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
import java.util.Iterator;

/**
 * Created by pgn on 12/28/14.
 */
public class LocationsDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String LOC_ROWID= "_location";
    public static final String PATH_ROWID= "_path";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String LOC_LAT = "latitude";
    public static final String LOC_LONG = "longitude";

    private static final String TAG = "LocationsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private boolean mWritable;

    private static final String DATABASE_NAME = "CDC";
    private static final String LOCATIONS_TABLE = "locations";
    private static final String PATHS_TABLE = "paths";
    private static final String PATH_LOCATIONS_TABLE = "path_locations";
    private static final int DATABASE_VERSION = 1;

    protected final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context ctx;
        private String createText;
        private int[] createFiles = {
                R.raw.create_db_loc,
                R.raw.create_db_paths,
                R.raw.create_db_paths_loc,
                R.raw.insert_loc_db};
        private int dropFiles = R.raw.drop_db;
        private String dropText = "";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            createText = "";
            for (int idx=0; idx < createFiles.length; idx++ )
                createText += readSQLText(context, createFiles[idx]);
            dropText = readSQLText(context, dropFiles);
        }

        private String readSQLText (Context context, int file) {
            InputStream str = context.getResources().openRawResource(file);
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            int size = 0;
            byte[] buffer = new byte[1024];
            try {
                while((size=str.read(buffer,0,1024))>=0) outputStream.write(buffer, 0, size);
                str.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outputStream.toString();

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            myExecSQL(db, createText);
        }

        private void myExecSQL(SQLiteDatabase db, String command) {
            String[] statements = (String[]) command.split(";");
            int idx;
            for (String statement : statements ) {
                Log.w(TAG, statement);
                if (!statement.trim().isEmpty())
                    db.execSQL(statement);
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            myExecSQL(db, dropText);
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

    public long createLocation(CDCLocation loc) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, loc.getName());
        initialValues.put(KEY_ADDRESS, loc.getAddress());
        initialValues.put(LOC_LAT, loc.getLat());
        initialValues.put(LOC_LONG, loc.getLng());

        return mDb.insert(LOCATIONS_TABLE, null, initialValues);
    }

    public long createPath(Path path) {
        long path_id, loc_path_id;
        ContentValues pathInitialValues = new ContentValues();
        pathInitialValues.put(KEY_NAME, path.getName());
        path_id = mDb.insert(PATH_LOCATIONS_TABLE, null, pathInitialValues);
        path.setId(path_id);
        for (Iterator<CDCLocation> it = path.locations.iterator(); it.hasNext();) {
            ContentValues locInitialValues = new ContentValues();
            CDCLocation loc = it.next();
            locInitialValues.put(LOC_ROWID, loc.getId());
            locInitialValues.put(PATH_ROWID, path.getId());
            loc_path_id = mDb.insert(PATH_LOCATIONS_TABLE, null, locInitialValues);
            if (loc_path_id == -1)
                return loc_path_id;
        }
        return path_id;
    }

    public boolean deleteAllLocations() {

        int doneDelete = 0;
        doneDelete = mDb.delete(LOCATIONS_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public CDCLocation fetchLocationByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        CDCLocation loc;
        mCursor = mDb.query(true, LOCATIONS_TABLE, new String[] {KEY_ROWID,
                        KEY_NAME, KEY_ADDRESS, LOC_LAT, LOC_LONG}, KEY_NAME + " = ?",
                new String[] {inputText}, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            loc = CDCLocation.extract(mCursor);
        } else {
            throw new SQLException("did not find a " + inputText);
        }
        return loc;

    }

    public Cursor fetchLocationsByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(LOCATIONS_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, LOC_LAT, LOC_LONG},
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, LOCATIONS_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, LOC_LAT, LOC_LONG},
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
            mCursor = mDb.query(PATHS_TABLE, new String[] {KEY_ROWID,KEY_NAME },
                    null, null, null, null, null);

        }
        else {
            mCursor = mDb.query(true, LOCATIONS_TABLE, new String[] {KEY_ROWID,
                            KEY_NAME, KEY_ADDRESS, LOC_LAT, LOC_LONG},
                    KEY_NAME + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllLocations() {

        Cursor mCursor = mDb.query(LOCATIONS_TABLE, new String[] {KEY_ROWID,
                        KEY_NAME, KEY_ADDRESS, LOC_LAT, LOC_LONG},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public CDCLocation fetchNextLocation( Cursor cursor ) {
        int count = cursor.getCount();
        int id = cursor.getInt(0);
        CDCLocation location = new CDCLocation(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getDouble(3),
                cursor.getDouble(4)
        );
        cursor.moveToNext();
        return location;
    }
 /*
 * cursor should perhaps be static or another class, etc this is cheap but given what we have put
 * this function in its proper place rather than having the calling function do it */
    public void closeLocation( Cursor cursor ) {
        cursor.close();
    }

    public Cursor fetchAllPaths() {

        Cursor mCursor = mDb.query(PATHS_TABLE, new String[] {KEY_ROWID,KEY_NAME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}
