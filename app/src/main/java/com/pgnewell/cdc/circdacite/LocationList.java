package com.pgnewell.cdc.circdacite;

import android.annotation.TargetApi; // added automatically for Honeycomb build
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build; // added automatically for Honeycomb build
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.sql.SQLException;


public class LocationList extends ActionBarActivity {
    private LocationsDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView) findViewById(R.id.listLocations);
        dbHelper = new LocationsDbAdapter(this);

        try {
            dbHelper.open(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_location_list);

        //Generate ListView from SQLite Database
        displayListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                        getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(LocationList.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)  // added automatically for Honeycomb build
    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllLocations();

        // The desired columns to be bound
        String[] columns = new String[] {
                LocationsDbAdapter.KEY_NAME,
                LocationsDbAdapter.KEY_NAME,
                LocationsDbAdapter.KEY_ADDRESS,
                LocationsDbAdapter.KEY_ADDRESS //
                //LocationsDbAdapter.LOC_LAT,
                //LocationsDbAdapter.LOC_LONG
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.edit_location_name,
                R.id.info_location_name,
                R.id.edit_location_address,
                R.id.info_location_address
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.location_edit,
                cursor,
                columns,
                to,
                0);

        // Assign adapter to ListView
        if (listView == null)
            listView = (ListView) findViewById(R.id.listLocations);
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.edit_switcher);
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                TextView nameView = (TextView) view.findViewById(R.id.info_location_name);
                TextView addressView = (TextView) view.findViewById(R.id.info_location_address);
                EditText nameText = (EditText) view.findViewById(R.id.edit_location_name);
                EditText addressText = (EditText) view.findViewById(R.id.edit_location_address);
                switcher.showNext();
                //addressView.setVisibility(View.VISIBLE);
                //ImageButton save = (ImageButton) view.get
                ViewGroup layout = (ViewGroup) view;
                //layout.addView

                // Get the state's capital from this row in the database.
                String locationName =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Toast.makeText(getApplicationContext(),
                        locationName, Toast.LENGTH_SHORT).show();

            }
        });

        EditText myFilter = (EditText) findViewById(R.id.locationsFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                try {
                    return dbHelper.fetchLocationsByName(constraint.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

    }

    public void onEditLocation(View view) {
        ViewSwitcher switcher = (ViewSwitcher) view.getParent().getParent().getParent();
        //ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.edit_switcher);
        switcher.showNext();
    }

    public void onAddLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        //@SuppressWarnings("unchecked")
        CDCLocation loc = null;
        switch (view.getId()) {
            case R.id.loc_edit_button:
                // set up edit
            case R.id.new_location_button:
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                break;
            case R.id.locate_on_map:
                break;
        }
    }

    public void OnResume() throws SQLException {
        dbHelper.open(false);
    }

    public void OnPause() {
        dbHelper.close();
    }

}
