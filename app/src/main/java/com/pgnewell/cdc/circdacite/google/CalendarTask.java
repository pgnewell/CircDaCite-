package com.pgnewell.cdc.circdacite.google;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 5/28/15.
 */
public class CalendarTask {

    static Context mContext;

    static final String[] columns = {
            CalendarEntity.ALLOWED_ATTENDEE_TYPES,
            CalendarEntity.ALLOWED_AVAILABILITY,
            CalendarEntity.ALLOWED_REMINDERS,
            CalendarEntity.CALENDAR_ACCESS_LEVEL,
            CalendarEntity.CALENDAR_COLOR,
            CalendarEntity.CALENDAR_COLOR_KEY,
            CalendarEntity.CALENDAR_DISPLAY_NAME,
            CalendarEntity.CALENDAR_TIME_ZONE,
            CalendarEntity.CAN_MODIFY_TIME_ZONE,
            CalendarEntity.CAN_ORGANIZER_RESPOND,
            CalendarEntity.IS_PRIMARY,
            CalendarEntity.MAX_REMINDERS,
            CalendarEntity.OWNER_ACCOUNT,
            CalendarEntity.SYNC_EVENTS,
            CalendarEntity.VISIBLE
    };
    static long idx = 0;
    static long IALLOWED_ATTENDEE_TYPES = idx++;
    static long IALLOWED_AVAILABILITY = idx++;
    static long IALLOWED_REMINDERS = idx++;
    static long ICALENDAR_ACCESS_LEVEL = idx++;
    static long ICALENDAR_COLOR = idx++;
    static long ICALENDAR_COLOR_KEY = idx++;
    static long ICALENDAR_DISPLAY_NAME = idx++;
    static long ICALENDAR_TIME_ZONE = idx++;
    static long ICAN_MODIFY_TIME_ZONE = idx++;
    static long ICAN_ORGANIZER_RESPOND = idx++;
    static long IIS_PRIMARY = idx++;
    static long IMAX_REMINDERS = idx++;
    static long IOWNER_ACCOUNT = idx++;
    static long ISYNC_EVENTS = idx++;
    static long IVISIBLE = idx;

    public CalendarTask(Context context) {
        mContext = context;
    }

    public CalendarTask(String name) {

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public List<ContentValues> CalendarList() {
        ContentResolver resolver = mContext.getContentResolver();
        List<ContentValues> calendars = new ArrayList<>();

        Cursor cursor = resolver.query(
                CalendarContract.CalendarEntity.CONTENT_URI, columns, null, null, null
        );

        while( cursor.moveToNext()) {
            ContentValues calendar = new ContentValues();
            int idx = 0;
            for(String field: columns)
                calendar.put(field, cursor.getString(idx++));
            calendars.add(calendar);
        }

        return calendars;

    }
/*
String  ALLOWED_ATTENDEE_TYPES  A comma separated list of attendee types supported for this calendar in the format "#,#,#".
String  ALLOWED_AVAILABILITY    A comma separated list of availability types supported for this calendar in the format "#,#,#".
String  ALLOWED_REMINDERS       A comma separated list of reminder methods supported for this calendar in the format "#,#,#".
String  CALENDAR_ACCESS_LEVEL   The level of access that the user has for the calendar

Type: INTEGER (one of the values below)
String  CALENDAR_COLOR  The color of the calendar.
String  CALENDAR_COLOR_KEY      A key for looking up a color from the CalendarContract.Colors table.
String  CALENDAR_DISPLAY_NAME   The display name of the calendar.
String  CALENDAR_TIME_ZONE      The time zone the calendar is associated with.
int     CAL_ACCESS_CONTRIBUTOR  Full access to modify the calendar, but not the access control settings
int     CAL_ACCESS_EDITOR       Full access to modify the calendar, but not the access control settings
int     CAL_ACCESS_FREEBUSY     Can only see free/busy information about the calendar
int     CAL_ACCESS_NONE         Cannot access the calendar
int     CAL_ACCESS_OVERRIDE     not used
int     CAL_ACCESS_OWNER        Full access to the calendar
int     CAL_ACCESS_READ         Can read all event details
int     CAL_ACCESS_RESPOND      Can reply yes/no/maybe to an event
int     CAL_ACCESS_ROOT         Domain admin
String  CAN_MODIFY_TIME_ZONE    Can the organizer modify the time zone of the event? Column name.
String  CAN_ORGANIZER_RESPOND   Can the organizer respond to the event? If no, the status of the organizer should not be shown by the UI.
String  IS_PRIMARY      Is this the primary calendar for this account.
String  MAX_REMINDERS   The maximum number of reminders allowed for an event.
String  OWNER_ACCOUNT   The owner account for this calendar, based on the calendar feed.
String  SYNC_EVENTS     Is this calendar synced and are its events stored on the device? 0 - Do not sync this calendar or store events for this calendar.
String  VISIBLE

 */

}
