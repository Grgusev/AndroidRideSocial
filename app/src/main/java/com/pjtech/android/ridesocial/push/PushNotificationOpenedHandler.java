package com.pjtech.android.ridesocial.push;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

/**
 * Created by android on 6/16/17.
 */

public class PushNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        android.util.Log.e("notification", result.notification.toJSONObject().toString());
    }
}
