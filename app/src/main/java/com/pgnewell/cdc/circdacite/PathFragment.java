package com.pgnewell.cdc.circdacite;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.pgnewell.cdc.circdacite.db.CdcContract;
import com.pgnewell.cdc.circdacite.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PathFragment extends Fragment implements AbsListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String[] PATH_VIEW_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            CdcContract.PathViewEntry._ID,
            CdcContract.PathViewEntry.COLUMN_NAME,
            CdcContract.PathViewEntry.COLUMN_START_LOC,
            CdcContract.PathViewEntry.COLUMN_START_LAT,
            CdcContract.PathViewEntry.COLUMN_START_LON,
            CdcContract.PathViewEntry.COLUMN_END_LOC,
            CdcContract.PathViewEntry.COLUMN_END_LAT,
            CdcContract.PathViewEntry.COLUMN_END_LON
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_START_LOC = 2;
    static final int COL_START_LAT = 3;
    static final int COL_START_LON = 4;
    static final int COL_END_LOC = 5;
    static final int COL_END_LAT = 6;
    static final int COL_END_LON = 7;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAT = "latitude";
    private static final String ARG_LONG = "longitude";

    // TODO: Rename and change types of parameters
    private double mLatitude;
    private double mLongitude;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private PathsAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static PathFragment newInstance(double latitude, double longitude) {
        PathFragment fragment = new PathFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, latitude);
        args.putDouble(ARG_LONG, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor
     */
    public PathFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        double lat = args.getDouble(ARG_LAT);
        double lon = args.getDouble(ARG_LONG);
        String[] dArgs = {Double.toString(lat),Double.toString(lon)};
        String sortOrder =
                "((" + CdcContract.PathViewEntry.COLUMN_START_LAT + " - ?) ** 2 + " +
                " (" + CdcContract.PathViewEntry.COLUMN_START_LON + " - ?) ** 2) ** .5" +
                        " ASC";
        Uri pathViewUri = CdcContract.PathViewEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                pathViewUri,
                PATH_VIEW_COLUMNS, null, dArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLatitude = getArguments().getDouble(ARG_LAT);
            mLongitude = getArguments().getDouble(ARG_LONG);
        }

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        updateBikeStations();
        // removed as a result of aligning with the Android course.
        // not sure where this is going but this will simplify the display while pushing the
        // details to PathsAdapter.
//        String[] columns = new String[]{
//                CdcDbAdapter.KEY_NAME,
//                CdcDbAdapter.PL_START,
//                CdcDbAdapter.PL_END
//        };
//
//        int[] to = new int[] {
//                R.id.path_name,
//                R.id.start_loc,
//                R.id.end_loc
//        };
//        MainActivity act = (MainActivity) getActivity();
//        // TODO: Change Adapter to display your content
//        mAdapter = new SimpleCursorAdapter(
//                act, R.layout.fragment_show_path,
//                act.path_view,
//                columns,
//                to,
//                0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_path, container, false);
        mAdapter = new PathsAdapter(getActivity(), null, 0);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private void updateBikeStations() {

        FetchBikeStationTask stationTask = new FetchBikeStationTask(getActivity());
        //TODO: replace with Settings value
        String url = getResources().getString(R.string.hubway_xml_uri);

        stationTask.execute(url);
    }

}
