package com.pgnewell.cdc.circdacite.Bixi;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by paul on 5/7/15.
 */
public class BixiContract {
    public static final String CONTENT_AUTHORITY = "com.pgnewell.cdc.circdacite";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BIKE_STATIONS = "bike-stations";

    public static final class BikeStationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIKE_STATIONS).build();

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
                        "/" + PATH_BIKE_STATIONS;

    }
}
