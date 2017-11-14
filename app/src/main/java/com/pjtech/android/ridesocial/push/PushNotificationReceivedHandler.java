package com.pjtech.android.ridesocial.push;

import android.content.Intent;
import android.content.IntentFilter;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.utils.RideInfoUtils;

/**
 * Created by android on 6/16/17.
 */

public class PushNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        int result = RideInfoUtils.parseRouterInfo(notification.payload.additionalData);

        if (result == -1)
        {
            Intent intent = new Intent();
            intent.setAction(RequestType.PUSH_RECEIVE_FAILED_ROUTER);
            RideSocialApp.mContext.sendBroadcast(intent);
        }
        else if (result == -2)
        {
            Intent intent = new Intent();
            intent.setAction(RequestType.SELECTED_HOSTING_NOT_YOU);
            RideSocialApp.mContext.sendBroadcast(intent);
        }
        else if (result == -3)
        {
            Intent intent = new Intent();
            intent.setAction(RequestType.SELECTED_NOT_HOSTING);
            RideSocialApp.mContext.sendBroadcast(intent);
        }
        else if (result == 1)
        {
            Intent intent = new Intent();
            intent.setAction(RequestType.PUSH_RECEIVE_CREATE_ROUTER);
            RideSocialApp.mContext.sendBroadcast(intent);
        }
        else if (result == 3)
        {
            Intent intent = new Intent();
            intent.setAction(RequestType.PUSH_RECEIVE_HOSTING_SELECTED_YOU);
            RideSocialApp.mContext.sendBroadcast(intent);
        }
        else if (result == -6)
        {
            return;
        }
    }
}
