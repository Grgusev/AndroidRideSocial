package com.pjtech.android.ridesocial.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 6/9/17.
 */

public class PhotoUploadResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("url")
    public String urlPath;
}
