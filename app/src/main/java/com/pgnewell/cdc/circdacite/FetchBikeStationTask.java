/*
 */
package com.pgnewell.cdc.circdacite;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.pgnewell.cdc.circdacite.Bixi.Station;
import com.pgnewell.cdc.circdacite.db.CdcContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

public class FetchBikeStationTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchBikeStationTask.class.getSimpleName();

    private final Context mContext;

    public FetchBikeStationTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        // Needs a url passed in as first parameter
        if (params.length == 0) {
            return null;
        }
        final String FORECAST_BASE_URL = params[0];

        HttpURLConnection urlConnection = null;

        try {

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().build();

            URL thisURL = new URL(builtUri.toString());

            // Create the request to Hubway, and open the connection
            urlConnection = (HttpURLConnection) thisURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            Vector<ContentValues> cVVector = Station.readIt(inputStream);
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(CdcContract.BikeStationEntry.CONTENT_URI, cvArray);

            return null;
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

}

