package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Almond on 10/9/2017.
 */

public class PaymentMethodResoponse {
    @SerializedName("status")
    public String status;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("methods")
    public Map methods;
}
