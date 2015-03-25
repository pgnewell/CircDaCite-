package com.pgnewell.cdc.circdacite;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by pgn on 12/4/14.
 */

public class CDCLocation {
    private long id;
    private String name;
    private String address;
    private LatLng latLng;

    public CDCLocation(String name, String address, double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
        this.name = name;
        this.address = address;
    }

    public CDCLocation(String name, String address, Context context) {
        this.name = name;
        this.address = address;
        setLocation(context);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getLat() {
        return latLng.latitude;
    }

    public double getLng() {
        return latLng.longitude;
    }

    public void setName(String address) {
        this.name = address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatLng(double lat, double lng) {
        this.latLng = new LatLng(lat,lng);
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name + ":\n" + address;
    }

    public void setLocation(Context context) {
        if(Geocoder.isPresent()){
            Geocoder gc = new Geocoder(context);
            try {
                List<Address> list;

                for (int idx=0; idx< 3; idx++ ) {
                    list = gc.getFromLocationName(address, 1);
                    if (list != null) {
                        Address add = list.get(0);
                        this.latLng = new LatLng(add.getLongitude(),add.getLatitude());
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static CDCLocation extract( Cursor cursor ) {
        CDCLocation loc = new CDCLocation(
            cursor.getString(1),cursor.getString(2),cursor.getDouble(3), cursor.getDouble(4)
        );
        return loc;
    }

}
