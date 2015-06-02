package com.pgnewell.cdc.circdacite.Bixi;

import android.content.ContentValues;
import android.util.Xml;

import com.pgnewell.cdc.circdacite.db.CdcContract;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.pgnewell.cdc.circdacite.db.CdcContract.BikeStationEntry;
/**
 * Created by paul on 5/14/15.
 */
public class Station {
    final long id;
    final String name;
    final String terminalName;
    Date lastCommWithServer;
    double lat;
    double longitude;
    boolean installed;
    boolean locked;
    Date installDate;
    Date removalDate;
    boolean temporary;
    boolean _public;
    int nbBikes;
    int nbEmptyDocks;
    Date latestUpdateTime;

    private static final String ns = null;


    private Station(long id, String name, String terminalName, Date lastCommWithServer, double lat,
                    double longitude, boolean installed, boolean locked, Date installDate,
                    Date removalDate, boolean temporary, boolean _public, int nbBikes,
                    int nbEmptyDocks, Date latestUpdateTime
    ) {
        this.id = id;
        this.name = name;
        this.terminalName = terminalName;
        this.lastCommWithServer = lastCommWithServer;
        this.lat = lat;
        this.longitude = longitude;
        this.installed = installed;
        this.locked = locked;
        this.installDate = installDate;
        this.removalDate = removalDate;
        this.temporary = temporary;
        this._public = _public;
        this.nbBikes = nbBikes;
        this.nbEmptyDocks = nbEmptyDocks;
        this.latestUpdateTime = latestUpdateTime;

    }

    public static ContentValues read( XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "station");
        ContentValues stationValues = new ContentValues();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String elName = parser.getName();

            parser.require(XmlPullParser.START_TAG, ns, elName);
            String text = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, elName);

            if (text == null || text.isEmpty())
                continue;

            switch (elName) {
                case BikeStationEntry.COLUMN_ID:
                case BikeStationEntry.COLUMN_REMOVAL_DATE:
                case BikeStationEntry.COLUMN_INSTALL_DATE:
                case BikeStationEntry.COLUMN_LATEST_UPDATE_TIME:
                case BikeStationEntry.COLUMN_LAST_COMM_WITH_SERVER:
                    stationValues.put(elName,readLong(text)); break;

                case BikeStationEntry.COLUMN_NAME:
                case BikeStationEntry.COLUMN_TERMINAL_NAME:
                    stationValues.put(elName,text); break;

                case BikeStationEntry.COLUMN_LAT:
                case BikeStationEntry.COLUMN_LONG:
                    stationValues.put(elName,readDouble(text)); break;

                case BikeStationEntry.COLUMN_LOCKED:
                case BikeStationEntry.COLUMN_TEMPORARY:
                case BikeStationEntry.COLUMN_PUBLIC:
                case BikeStationEntry.COLUMN_INSTALLED:
                    stationValues.put(elName,readBoolean(text)); break;

                case BikeStationEntry.COLUMN_NB_BIKES:
                case BikeStationEntry.COLUMN_NB_EMPTY_DOCKS:
                    stationValues.put(elName,readInt(text)); break;

                default:
                    skip(parser);
            }
        }
        return stationValues;
    }

    private static int readInt(String text) { return Integer.parseInt(text); }
    private static Long readLong(String text) { return Long.parseLong(text); }
    private static boolean readBoolean(String text) { return Boolean.parseBoolean(text); }
    private static Double readDouble(String text) { return Double.parseDouble(text); }

    public static Vector<ContentValues> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Vector<ContentValues> stations = new Vector<>();

        parser.require(XmlPullParser.START_TAG, ns, "stations");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("station")) {
                stations.add(Station.read(parser));
            } else {
                Station.skip(parser);
            }
        }
        return stations;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public static Vector<ContentValues> readIt(InputStream stream) throws IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);
            parser.nextTag();
            return readFeed(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }

        return null;
    }
}
