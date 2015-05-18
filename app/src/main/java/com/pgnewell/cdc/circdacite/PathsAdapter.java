package com.pgnewell.cdc.circdacite;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by paul on 5/15/15.
 */
public class PathsAdapter extends CursorAdapter {
        public PathsAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        private String convertCursorRowToUXFormat(Cursor cursor) {
            // get row indices for our cursor

            return cursor.getString(PathFragment.COL_NAME) + " " +
                    cursor.getString(PathFragment.COL_START_LOC) + " - " +
                    cursor.getDouble(PathFragment.COL_START_LAT) + "/" +
                    cursor.getDouble(PathFragment.COL_START_LON) + " " +
                    cursor.getString(PathFragment.COL_END_LOC) + " - " +
                    cursor.getDouble(PathFragment.COL_END_LAT) + "/" +
                    cursor.getDouble(PathFragment.COL_END_LON);
        }

        /*
            Remember that these views are reused as needed.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_show_path, parent, false);

            return view;
        }

        /*
            This is where we fill-in the views with the contents of the cursor.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // our view is pretty simple here --- just a text view
            // we'll keep the UI functional with a simple (and slow!) binding.

            TextView tv = (TextView)view;
            tv.setText(convertCursorRowToUXFormat(cursor));
        }
}
