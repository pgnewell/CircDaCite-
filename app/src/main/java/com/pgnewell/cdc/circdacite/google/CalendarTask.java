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
import com.google.ical.compat.javautil.DateIteratorFactory;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;
import com.pgnewell.cdc.circdacite.CDCLocation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.tz.FixedDateTimeZone;

/**
 * Created by paul on 5/18/15.
 * Yes, I am encapsulating an encapsulation. An interface to google's content provider interface.
 * Yes, this should be ridiculous and it is, an example of how empty the provider interface is.
 */
public enum CalendarTask {
    INSTANCE;
    static Context mContext;

    static final String[] columns = {
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

/*
    public CalendarTask(Context context) {
        // Get the ContentResolver which will send a message to the ContentProvider.
        String[] projection = { CalendarContract.Events._ID,
                CalendarContract.Events.TITLE };
        ContentResolver resolver = context.getContentResolver();
        Date now = new Date();
        Toast.makeText(context,
                "now is the time: " + now.toString(),Toast.LENGTH_SHORT)
                .show();

    }
*/

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

    }

            /**
             * Returns a list of events from the Android calendar that have locations that can
             * be interpreted with positions (lat/long) on a map.
             * @param context - required for Provider
             * @param start - Start datetime for events
             * @param end - End datetime for events
             * @return a List of all events that are within the time range and have parsable
             * positions
             */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static List<CalEvent> PendingEventsWithLocations(Context context, long start, long end) {
        mContext = context;
        DateTime rangeStart = new DateTime(start);
        DateTime rangeEnd = new DateTime(end);
        ContentResolver resolver = context.getContentResolver();
        List<CalEvent> events = new ArrayList<>();
        String [] dates = {Long.toString(start),Long.toString(end)};

        Cursor cursor = resolver.query(
                CalendarContract.Events.CONTENT_URI, columns, selection, dates, null
        );
        while( cursor.moveToNext()) {
            CalEvent event = new CalEvent(cursor);

            String rRule = cursor.getString(IRULE);
            if (rRule != null && !rRule.isEmpty()) {
                try {
                    // This is a recurring event find out if there is anything in our period
                    Pattern durRegex = Pattern.compile("PT?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
                    Matcher matcher = durRegex.matcher(event.mDur);
                    int duration = 0;
                    if (matcher.find()) {
                        String hours = matcher.group(1);
                        String minutes = matcher.group(2);
                        String seconds = matcher.group(3);
                        if (hours != null && !hours.isEmpty())
                            duration += Integer.valueOf(hours) * 60 * 60;
                        if (minutes != null && !minutes.isEmpty())
                            duration += Integer.valueOf(minutes) * 60;
                        if (seconds != null && !seconds.isEmpty())
                            duration += Integer.valueOf(seconds);
                    }

                    DateTime startTime = new DateTime(Long.valueOf(event.mStart));
                    // We need to get a date that is the day before the range starts with the time
                    // of the recurring event (Fuuuhhhcked up!)
                    int startMillis = startTime.getMillisOfDay();
                    int rangeMillis = rangeStart.getMillisOfDay();
                    DateTime queryStartDate = rangeStart.minusDays(1)
                            .minus(rangeMillis)
                            .plus(startMillis);
                    for (DateTime date :
                            DateTimeIteratorFactory.createDateTimeIterable(
                                    // Fuckin stupid way to get the sort of date I want why
                                    // cant Jodi do this
                                    "RRULE:" + rRule, queryStartDate,
                                    DateTimeZone.forID(event.mTZ), true)) {

                        if(date.toDateTime().isAfter(rangeEnd))
                            break;
                        startTime = date;
                        DateTime endTime = date.plus(duration*1000);
                        Toast.makeText(context, "Repeating date:[" + date + "]", Toast.LENGTH_SHORT)
                                .show();

                        // rangeStart and rangeEnd represent the range of times for the sought records
                        // startTime and endTime represent the span of the current event
                        // if any time in the first range is also a time in the second range,
                        // then this is one of our records--
                        if (rangeEnd.isAfter(startTime) && rangeStart.isBefore(endTime))
                            events.add(new CalEvent(cursor));
                    }
                } catch (ParseException e) {
                    StackTraceElement[] messages = e.getStackTrace();
                    for (StackTraceElement message : messages) {
                        Log.e("LOG_TAG", message.toString());
                    }
                }
            } else // not a recurring, we know it falls in the period
                events.add(event);

            Toast.makeText(
                    context,
                    "[" + cursor.getString(ITITLE) + " - " +
                            cursor.getString(IRULE) + " - " +
                            (new Date(cursor.getLong(ISTART))).toString() + " to " +
                            (new Date(cursor.getLong(IEND))).toString() + "]",
                    Toast.LENGTH_SHORT)
                    .show();
        }

        return events;
    }
//    @Override
//    protected Void doInBackground(String... params) {
//        return null;
//    }
}
