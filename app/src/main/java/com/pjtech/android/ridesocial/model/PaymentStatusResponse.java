package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 6/18/17.
 */

public class PaymentStatusResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("method_id")
    public String methodId;

}
