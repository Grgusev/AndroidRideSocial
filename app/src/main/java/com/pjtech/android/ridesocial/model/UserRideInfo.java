package com.pjtech.android.ridesocial.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by android on 6/13/17.
 */

public class UserRideInfo {
    //user id
    public String userId;

    public String requestId;

    //user info
    public String username;
    public String cellNum;

    //router Info
    public LatLng orgPosition;
    public LatLng destPosition;
    public String facebookId;

    //other Info
    public double madePayment;
    public boolean isHosting;
    public double rating;
}
