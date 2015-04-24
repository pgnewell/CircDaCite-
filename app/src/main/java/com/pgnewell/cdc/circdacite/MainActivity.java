package com.pgnewell.cdc.circdacite;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.sql.SQLException;

public class MainActivity extends ActionBarActivity
    implements PathFragment.OnFragmentInteractionListener {
    private CdcDbAdapter dbHelper;
    enum NextAction {path_list, location_list, settings};
    public Cursor paths;
    public Cursor locations;
    public Cursor path_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CdcDbAdapter(this);

        try {
            dbHelper.open(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        NextAction action;
        locations = dbHelper.fetchAllLocations();
        if (locations.getCount() == 0)
            action = NextAction.location_list;
        else {
            path_view = dbHelper.fetchAllPathLocs();
            if (path_view.getCount() == 0)
                action = NextAction.path_list;
        }
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.menu_db_dump:
                dbHelper.dump(this);
                return true;
            case R.id.menu_path_list:
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                PathFragment pf = new PathFragment();
                ft.add(R.id.container, pf);
                ft.commit();

                return true;
            case R.id.menu_loc_list:
                return true;
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                        getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     * This is just a placeholder and a wholly different Fragment class should always be used.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void onFragmentInteraction(String id) {

    }

}
