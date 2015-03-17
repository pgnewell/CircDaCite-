package com.pgnewell.cdc.circdacite;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.sql.SQLException;


public class LocationActivity extends ActionBarActivity {
    private LocationsDbAdapter dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new LocationsDbAdapter(this);
        try {
            dbHelper.open(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.location_edit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
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

    public void onClick(View view) {
        //@SuppressWarnings("unchecked")
        CDCLocation loc = null;
        final EditText name = (EditText)findViewById(R.id.edit_location_name);
        final EditText address = (EditText)findViewById(R.id.edit_location_address);
        switch (view.getId()) {
            case R.id.locate_on_map:
                // save the new comment to the database
//                comment = datasource.createLocation(comments[nextInt]);
//                adapter.add(comment);
                break;
            case R.id.loc_save_button:
                   String nm = name.getText().toString();
                String addr = address.getText().toString();
                loc = new CDCLocation( nm, addr, this );
                dbHelper.createLocation(loc);
                break;
        }
    }

    public void OnResume() throws SQLException {
        dbHelper.open(true);
    }

    public void OnPause() {
        dbHelper.close();
    }

}
