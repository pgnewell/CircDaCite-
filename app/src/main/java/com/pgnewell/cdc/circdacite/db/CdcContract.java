package com.pgnewell.cdc.circdacite.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by paul on 5/4/15.
 */
public class CdcContract {
    public static final String CONTENT_AUTHORITY = "com.pgnewell.cdc.circdacite";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_LOCATION = "location";
    public static final String PATH_PATH_LOCATION = "path-location";
    public static final String PATH_PATH_VIEW = "path-view";
    public static final String PATH_BIKE_STATION = "bike-station";
    public static final String PATH_PATH = "path";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "lon";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_LOCATION;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PathEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PATH).build();
        public static final String TABLE_NAME = "paths";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATH;
        public static Uri buildPathUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PathLocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "path_locations";
        public static final String COLUMN_ID = "_id"; // WTF?? Yes we require this
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PATH = "_path";
        public static final String COLUMN_LOCATION = "_location";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;

    }

    public static final class PathViewEntry implements BaseColumns {
        public static final String TABLE_NAME = "paths_view";
        public static final String COLUMN_ID = "_id"; // WTF?? Yes we require this
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_START_LOC = "start_loc";
        public static final String COLUMN_END_LOC = "end_loc";
        public static final String COLUMN_START_LAT = "start_loc_lat";
        public static final String COLUMN_END_LAT = "end_loc_lat";
        public static final String COLUMN_START_LON = "start_loc_lon";
        public static final String COLUMN_END_LON = "end_loc_lon";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_LOCATION;

    }

    public static final class BikeStationEntry implements BaseColumns {
        public static final String TABLE_NAME = "bike_stations";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIKE_STATION).build();

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TERMINAL_NAME = "terminalName";
        public static final String COLUMN_LAST_COMM_WITH_SERVER = "lastCommWithServer";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "long";
        public static final String COLUMN_INSTALLED = "installed";
        public static final String COLUMN_LOCKED = "locked";
        public static final String COLUMN_INSTALL_DATE = "installDate";
        public static final String COLUMN_REMOVAL_DATE = "removalDate";
        public static final String COLUMN_TEMPORARY = "temporary";
        public static final String COLUMN_PUBLIC = "public";
        public static final String COLUMN_NB_BIKES = "nbBikes";
        public static final String COLUMN_NB_EMPTY_DOCKS = "nbEmptyDocks";
        public static final String COLUMN_LATEST_UPDATE_TIME = "latestUpdateTime";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_BIKE_STATION;

        public static Uri buildPathUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
