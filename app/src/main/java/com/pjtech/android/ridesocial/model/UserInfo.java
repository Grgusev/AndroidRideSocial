package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 6/9/17.
 */

public class UserInfo {
    @SerializedName("id")
    public String userId;

    @SerializedName("status")
    public String status;

    @SerializedName("name")
    public String userName;

    @SerializedName("cell_number")
    public String cellNumber;

    @SerializedName("photo")
    public String photoUrl;

    @SerializedName("facebook_url")
    public String facebookUrl;

    @SerializedName("verify_code")
    public String verifyCode;

    @SerializedName("uber")
    public int uberEnable;

    @SerializedName("lyft")
    public int lyftEnable;

    @SerializedName("date_last_saved")
    public String dateLastSaved;

    @SerializedName("rating")
    public int rating;

}
