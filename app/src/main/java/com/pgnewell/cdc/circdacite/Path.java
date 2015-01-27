package com.pgnewell.cdc.circdacite;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pgn on 1/5/15.
 */
public class Path {
    private long id;
    private String name;
    private ArrayList<Location> locations;

    public Path( String name ) {
        this.name = name;
        locations = new ArrayList<Location>();
    }

    public void addLocation( Location loc ) {
        this.locations.add(loc);
    }

    public void dropLocation( Location loc ) {
        for (Iterator<Location> it = this.locations.iterator(); it.hasNext();)
            if (it.next().getId() == loc.getId())
                it.remove();
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

   public void setName( String name ) {
        this.name = name;
   }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }


}
