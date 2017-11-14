package com.pjtech.android.ridesocial.utils;

import android.app.Activity;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.pjtech.android.ridesocial.RideSocialApp;

import java.util.Arrays;

/**
 * Created by android on 6/16/17.
 */

public class FacebookUtils {
    public static boolean isInitialized = false;

    public static void init()
    {
        if (isInitialized) return;

        FacebookSdk.sdkInitialize(RideSocialApp.getContext());
        isInitialized = true;
    }

    public static void loginFB(Activity activity)
    {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
    }

    public static void delink(Activity activity)
    {
        LoginManager.getInstance().logOut();;
    }
}
