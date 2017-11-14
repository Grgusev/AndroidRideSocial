package com.pjtech.android.ridesocial;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.push.PushNotificationOpenedHandler;
import com.pjtech.android.ridesocial.push.PushNotificationReceivedHandler;
import com.pjtech.android.ridesocial.utils.FacebookUtils;
import io.fabric.sdk.android.Fabric;

/**
 * Created by android on 6/7/17.
 */

public class RideSocialApp extends MultiDexApplication {
    public static Context mContext;
    public static String mPackageName;

    public final static boolean TEST = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = this.getApplicationContext();
        mPackageName = this.getPackageName();
        FacebookUtils.init();

        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new PushNotificationOpenedHandler())
                .setNotificationReceivedHandler( new PushNotificationReceivedHandler() )
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                GlobalData.playerID = userId;
            }
        });

        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    public static String getAppPackageName() {
        if (TextUtils.isEmpty(mPackageName))
            return "";

        return mPackageName;
    }

}
