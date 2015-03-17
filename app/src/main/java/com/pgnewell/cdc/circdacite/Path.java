package com.pgnewell.cdc.circdacite;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by pgn on 1/5/15.
 */
public class Path {
    private long id;
    private String name;
    private ArrayList<CDCLocation> locations;

    public Path( String name ) {
        this.name = name;
        locations = new ArrayList<CDCLocation>();
    }

    public void addLocation( CDCLocation loc ) {
        this.locations.add(loc);
    }

    public void dropLocation( CDCLocation loc ) {
        for (Iterator<CDCLocation> it = this.locations.iterator(); it.hasNext();)
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
