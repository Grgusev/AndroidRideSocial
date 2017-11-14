package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 6/9/17.
 */

public class VerifyResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("name")
    public String userName;

    @SerializedName("cell_number")
    public String cellNumber;
}
