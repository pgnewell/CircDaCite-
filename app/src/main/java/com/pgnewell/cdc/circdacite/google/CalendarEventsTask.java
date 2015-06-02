package com.pgnewell.cdc.circdacite.google;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;
import com.pgnewell.cdc.circdacite.CDCLocation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by paul on 5/18/15.
 * Yes, I am encapsulating an encapsulation. An interface to google's content provider interface.
 * Yes, this should be ridiculous and it is, an example of how empty the provider interface is.
 */
public class CalendarEventsTask {
    static Context mContext;

    static final String[] eventColumns = {
            CalendarContract.EventsEntity.TITLE,
            CalendarContract.EventsEntity.DESCRIPTION,
            CalendarContract.EventsEntity.EVENT_LOCATION,
            CalendarContract.EventsEntity.EVENT_TIMEZONE,
            CalendarContract.EventsEntity.RRULE,
            CalendarContract.EventsEntity.RDATE,
            CalendarContract.EventsEntity.LAST_DATE,
            CalendarContract.EventsEntity.DURATION,
            CalendarContract.EventsEntity.DTSTART,
            CalendarContract.EventsEntity.DTEND
    };

    static int i = 0;
    static final int ITITLE = i++;
    static final int IDESC = i++;
    static final int ILOC = i++;
    static final int ITZ = i++;
    static final int IRULE = i++;
    static final int IDATE = i++;
    static final int ILASTDATE = i++;
    static final int IDURATION = i++;
    static final int ISTART = i++;
    static final int IEND = i++;

    static final String selection = CalendarContract.EventsEntity.EVENT_LOCATION + "!='' AND ((" +
            CalendarContract.EventsEntity.DTSTART + ">= ? AND " +
            CalendarContract.EventsEntity.DTSTART + "<= ?)" +
            " OR " + CalendarContract.Events.RRULE + "!= '')";

    public CalendarEventsTask(Context context) {
        mContext = context;
    }
/*
    public CalendarTask(Context context) {
        // Get the ContentResolver which will send a message to the ContentProvider.
        String[] projection = { CalendarContract.Events._ID,
                CalendarContract.Events.TITLE };
        ContentResolver resolver = context.getContentResolver();
    }
*/

    // TODO: should be protected but it's needed for now
    public final static class CalEvent {
        public String mLocation;
        public String mTitle;
        public String mDesc;
        public String mRule;
        public String mDate;
        public String mLastDate;
        public String mStart;
        public String mEnd;
        public String mTZ;
        public String mDur;
        public LatLng latLng;

        public CalEvent(Cursor cursor) {
            mTitle = cursor.getString(ITITLE);
            mDesc = cursor.getString(IDESC);
            mLocation = cursor.getString(ILOC);
            mTZ = cursor.getString(ITZ);
            mRule = cursor.getString(IRULE);
            mDate = cursor.getString(IDATE);
            mLastDate = cursor.getString(ILASTDATE);
            mStart = cursor.getString(ISTART);
            mEnd = cursor.getString(IEND);
            mDur = cursor.getString(IDURATION);
            CDCLocation loc = new CDCLocation(mTitle,mLocation,mContext);
            latLng = loc.getLatLng();
        }
        public CalEvent(CalEvent event) {
            mTitle = event.mTitle;
            mDesc = event.mDesc;
            mLocation = event.mLocation;
            mTZ = event.mTZ;
            mRule = event.mRule;
            mDate = event.mDate;
            mLastDate = event.mLastDate;
            mStart = event.mStart;
            mEnd = event.mEnd;
            mDur = event.mDur;
            latLng = event.latLng;
        }

    }

    /**
     * Returns a list of events from the Android calendar that have locations that can
     * be interpreted with positions (lat/long) on a map.
     * @param start - milliseconds, start of date range for queried events
     * @param end - milliseconds, end of range
     * @return a List of all events that are within the time range and have parsable
     * positions
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public List<CalEvent> PendingEventsWithLocations(long start, long end) {
        DateTime rangeStart = new DateTime(start);
        DateTime rangeEnd = new DateTime(end);
        ContentResolver resolver = mContext.getContentResolver();
        List<CalEvent> events = new ArrayList<>();
        String [] dates = {Long.toString(start),Long.toString(end)};

        Cursor cursor = resolver.query(
                CalendarContract.Events.CONTENT_URI, eventColumns, selection, dates, null
        );
        while( cursor.moveToNext()) {
            CalEvent event = new CalEvent(cursor);

            String rRule = cursor.getString(IRULE);
            if (rRule != null && !rRule.isEmpty()) {
                List<CalEvent> repeatingEvents = RepeatingEvents(event, rangeStart, rangeEnd);
                events.addAll(repeatingEvents);
            } else // not a recurring, we know it falls in the period
                events.add(event);

//            Toast.makeText(
//                    mContext,
//                    "[" + cursor.getString(ITITLE) + " - " +
//                            cursor.getString(IRULE) + " - " +
//                            (new Date(cursor.getLong(ISTART))).toString() + " to " +
//                            (new Date(cursor.getLong(IEND))).toString() + "]",
//                    Toast.LENGTH_SHORT)
//                    .show();
        }

        return events;
    }

    /**
     * parse rfc-2445 duration
     * example: D3DT4H20M (D 3D T(separates time) 4H 20M) is 3 days 4 hours 20 minutes
     * @param dur
     * @return seconds which should be multiplied by 1000 to be useful
     */
    private static long Duration (String dur) {
        Pattern durRegex = Pattern.compile(
                "^\\s*P(?:(\\d+)W)?(?:(\\d+)D)?T?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?\\s*$"
        );
        Matcher matcher = durRegex.matcher(dur);
        int duration = 0;
        if (matcher.find()) {
            String days = matcher.group(1);
            String weeks = matcher.group(2);
            String hours = matcher.group(3);
            String minutes = matcher.group(4);
            String seconds = matcher.group(5);
            if (days != null && !hours.isEmpty())
                duration += Integer.valueOf(days) * 60 * 60 * 24;
            if (weeks != null && !hours.isEmpty())
                duration += Integer.valueOf(weeks) * 60 * 60 * 24 * 7;
            if (hours != null && !hours.isEmpty())
                duration += Integer.valueOf(hours) * 60 * 60;
            if (minutes != null && !minutes.isEmpty())
                duration += Integer.valueOf(minutes) * 60;
            if (seconds != null && !seconds.isEmpty())
                duration += Integer.valueOf(seconds);
        }
        return duration;
    }


    private static List<CalEvent> RepeatingEvents(CalEvent event, DateTime rangeStart, DateTime rangeEnd) {
        List<CalEvent> events = new ArrayList<>();
        try {
            // This is a recurring event find out if there is anything in our period
            long duration = Duration(event.mDur);
            DateTime startTime = new DateTime(Long.valueOf(event.mStart));
            if (startTime.isAfter(rangeEnd))
                return null;

            // We need to get a date that is the day before the range starts with the time
            // of the recurring event (Fuuuhhhcked up!) it looks like joditime was designed
            // to solve this problem but I can't figure how it does that
            int startMillis = startTime.getMillisOfDay();
            int rangeMillis = rangeStart.getMillisOfDay();
            DateTime queryStartDate = rangeStart.minusDays(1)
                    .minus(rangeMillis)
                    .plus(startMillis);
            for (DateTime date :
                    DateTimeIteratorFactory.createDateTimeIterable(
                            "RRULE:" + event.mRule, queryStartDate,
                            DateTimeZone.forID(event.mTZ), true)) {

                if(date.toDateTime().isAfter(rangeEnd))
                    break;
                DateTime endTime = date.plus(duration*1000);
                // rangeStart and rangeEnd represent the range of times for the sought records
                // date and endTime represent the span of the current event
                // if any time in the first range is also a time in the second range,
                // then this is one of our records--
                if (rangeEnd.isAfter(date) && rangeStart.isBefore(endTime)) {
                    CalEvent newEvent = new CalEvent(event);
                    newEvent.mStart = Long.toString(date.getMillis());
                    events.add(newEvent);
                }
            }
        } catch (ParseException e) {
            StackTraceElement[] messages = e.getStackTrace();
            for (StackTraceElement message : messages) {
                Log.e("LOG_TAG", message.toString());
            }
        }
        return events;
    }
//    @Override
//    protected Void doInBackground(String... params) {
//        return null;
//    }
}
