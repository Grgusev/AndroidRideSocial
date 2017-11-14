package com.pjtech.android.ridesocial.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by android on 6/13/17.
 */

public class RideInfo {
    //created Router ID
    public String routerID;
    public String firebaseID;

    //suggested Router
    public LatLng meetUp;
    public LatLng dropOff;

    //Riding User Count
    public ArrayList<UserRideInfo> rideInfos = new ArrayList<>();

    //total fare
    public float totalFare;
}
