package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 6/9/17.
 */

public class UserLoginResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("name")
    public String userName;
}
