package com.bourquelot.trailxplorer;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public final class arraygpx{

    private arraygpx(){
        locationArray = new ArrayList<>();
    }
    private static List<Location> locationArray;

    public static void addlocationArray(Location location){
        locationArray.add(location);
    }
    public static void setlocationArray(List<Location> val){
        locationArray = val;
    }
    public static List<Location> getlocationArray(){
        return locationArray;
    }
}
