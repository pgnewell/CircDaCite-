package com.pgnewell.cdc.circdacite;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.pgnewell.cdc.circdacite.db.CdcDbAdapter;

import java.sql.SQLException;

/**
 * Created by pgn on 1/5/15.
 */
public class PathList1 extends ActionBarActivity {
    private CdcDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CdcDbAdapter(this);

        try {
            dbHelper.open(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_path_list1);

        //Generate ListView from SQLite Database
        displayListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_path_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)  // added automatically for Honeycomb build
    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllPaths();

        // The desired columns to be bound
        String[] columns = new String[] {
                CdcDbAdapter.KEY_NAME,
                //LocationsDbAdapter.KEY_ADDRESS //
                //LocationsDbAdapter.LOC_LAT,
                //LocationsDbAdapter.LOC_LONG
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.path_name
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.path_info,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listPaths);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String pathName =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Toast.makeText(getApplicationContext(),
                        pathName, Toast.LENGTH_SHORT).show();

            }
        });

        EditText myFilter = (EditText) findViewById(R.id.pathsFilter);
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
                    return dbHelper.fetchPathsByName(constraint.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

    }

    public void onClick(View view) {
        //@SuppressWarnings("unchecked")
        Path loc = null;
        switch (view.getId()) {
            case R.id.loc_edit_button:
                // set up edit
            case R.id.new_path_button:
                Intent intent = new Intent(this, PathActivity.class);

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
