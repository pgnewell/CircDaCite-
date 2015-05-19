package com.pgnewell.cdc.circdacite.google;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by paul on 5/18/15.
 */
public class CalendarTask //extends AsyncTask<String, Void, Void>
        {

    static final String[] columns = {
            CalendarContract.EventsEntity.EVENT_LOCATION,
            CalendarContract.EventsEntity.DTSTART,
            CalendarContract.EventsEntity.DTEND
    };
    static final int iLoc = 0;
    static final int iStart = 1;
    static final int iEnd = 2;

    public List<Event> events = null;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public CalendarTask(Context context) {
        // Get the ContentResolver which will send a message to the ContentProvider.
        String[] projection = { CalendarContract.Events._ID,
                CalendarContract.Events.TITLE };
        ContentResolver resolver = context.getContentResolver();
        Date now = new Date();
        Toast.makeText(context,
                "now is the time: " + now.toString(),Toast.LENGTH_SHORT)
                .show();
        // Get a Cursor containing all of the rows in the Words table.
        Cursor cursor = resolver.query(
                CalendarContract.Events.CONTENT_URI, columns,
                CalendarContract.EventsEntity.DTSTART + ">=" + Calendar.getInstance().getTimeInMillis(), null,
                null);
        while( cursor.moveToNext()) {
            Toast.makeText(
                    context,
                    "[" + cursor.getString(0) + " - " +
                            (new Date(cursor.getLong(1))).toString() + " to " +
                            (new Date(cursor.getLong(2))).toString() + "]",
                    Toast.LENGTH_SHORT)
                .show();
        }

    }
    public class Event {
        String mLocation;
        long mStart;
        long mEnd;
        public Event( String location, long start, long end) {
            mLocation = location; mStart = start; mEnd = end;
        }

    }

//    @Override
//    protected Void doInBackground(String... params) {
//        return null;
//    }
}
