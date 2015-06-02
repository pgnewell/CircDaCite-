package com.pgnewell.cdc.circdacite;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.pgnewell.cdc.circdacite.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveLocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveLocationFragment extends DialogFragment {
    public interface SaveLocationDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";

    private String mLocName;
    private String mLocAddress;

    private SaveLocationDialogListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param location Parameter 1.
     * @return A new instance of fragment SaveLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveLocationFragment newInstance(LatLng location) {
        SaveLocationFragment fragment = new SaveLocationFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, location.latitude);
        args.putDouble(ARG_LONGITUDE, location.longitude);
        fragment.setArguments(args);
        return fragment;
    }

    public SaveLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_save_location, null))
                .setMessage(R.string.dialog_save_location)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(SaveLocationFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(SaveLocationFragment.this);
                    }
                });
        return builder.create();
    }
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.fragment_save_location, null))
///*            // Add action buttons
//            .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    EditText locName = (EditText)
//                            ((AlertDialog) dialog).findViewById(R.id.edit_location_name);
//                    EditText locAddress = (EditText)
//                            ((AlertDialog) dialog).findViewById(R.id.edit_location_address);
//                }
//            })
//            .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    SaveLocationFragment.this.getDialog().cancel();
//                }
//            })*/
//        ;
//        return builder.create();
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_save_location, container, false);
//    }

/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SaveLocationDialogListener) activity;
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
        public void onFragmentInteraction(Uri uri);
    }

}
