package com.pgnewell.cdc.circdacite;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;

public class MapsFragment extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        OnMapReadyCallback,
        SaveLocationFragment.SaveLocationDialogListener {

    private static final LatLng KENDALL = new LatLng(42.3628735,-71.0900971);

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds mBounds;
    private Marker mLastSelectedMarker;

    private TextView mTopText;
    private LocationsDbAdapter dbHelper;
    private SaveLocationFragment.SaveLocationDialogListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mTopText = (TextView) findViewById(R.id.top_text);

        dbHelper = new LocationsDbAdapter(this);

        try {
            dbHelper.open(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.location_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.location_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge = R.drawable.ic_launcher;
            ((ImageView) view.findViewById(R.id.loc_icon)).setImageResource(badge);

            String title = marker.getTitle().toString();
            TextView titleUi = ((TextView) view.findViewById(R.id.loc_edit_name));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            snippetUi.setText(snippet);
        }
    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng ll = KENDALL;
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria c = new Criteria();
        String p = lm.getBestProvider(c, true);

        Location loc = lm.getLastKnownLocation(p);

        if(loc!=null) {
            ll = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title("You're here"));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                //mMap.addMarker(new MarkerOptions().position(position).title("New location"));
                DialogFragment dialog = SaveLocationFragment.newInstance(position);
                dialog.show(getFragmentManager(), "SaveLocation");
                // mMap.setOnMarkerDragListener( );
                //path.add(new PathLocation(position, current_type, ""));

            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Map with lots of markers.");

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));
                }
            });
        }
    }

    private void addMarkersToMap() {
        Cursor cursor = dbHelper.fetchAllLocations();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        while (!cursor.isAfterLast()) {
            CDCLocation loc = dbHelper.fetchNextLocation(cursor);
            mMap.addMarker(new MarkerOptions()
                    .position(loc.getLatLng())
                    .title(loc.getName())
                    .snippet(loc.getAddress())
                    .draggable(true)
                    );
            boundsBuilder.include(loc.getLatLng());
        }
        mBounds = boundsBuilder.build();
        // Uses a colored icon.
        //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        //      .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
        //      .infoWindowAnchor(0.5f, 0.5f));

        // Creates a draggable marker. Long press to drag.

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String title = marker.getTitle().toString();
        mListener = (SaveLocationFragment.SaveLocationDialogListener) this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choice");
        CharSequence[] x = new String[] {"a","b"};
        builder.setItems(x,null);
        AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();

//        setContentView(R.layout.fragment_pathmenuitem);
//        PathMenuFragment pathMenu = PathMenuFragment.newInstance("a","b");
//        pathMenu.show(getFragmentManager(), "path_menu");
//        String tag = "path";
//        getFragmentManager().beginTransaction()
//                .add(new Fragment(),tag)
//                .commit();
        //Toast.makeText(this, "Why is " + title + " coming up", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        String title = marker.getTitle();
        try {
            CDCLocation loc = dbHelper.fetchLocationByName(title);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    public void onDialogPositiveClick(DialogFragment dialog) {
        Bundle args = dialog.getArguments();
        double locLat = args.getDouble(SaveLocationFragment.ARG_LATITUDE);
        double locLong = args.getDouble(SaveLocationFragment.ARG_LONGITUDE);
        // User touched the dialog's positive button
        EditText locName = (EditText)
                dialog.getDialog().findViewById(R.id.edit_location_name);
        EditText locAddress = (EditText)
                dialog.getDialog().findViewById(R.id.edit_location_address);
        CDCLocation location = new CDCLocation(
                locName.getText().toString(), locAddress.getText().toString(), locLat, locLong
        );
        dbHelper.createLocation(location);
        mMap.addMarker(new MarkerOptions()
                        .position(location.getLatLng())
                        .title(location.getName())
                        .snippet(location.getAddress())
        );

    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
            dialog.getDialog().cancel();
    }


    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        addMarkersToMap();
    }

}
