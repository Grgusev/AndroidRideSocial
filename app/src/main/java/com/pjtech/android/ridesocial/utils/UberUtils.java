package com.pjtech.android.ridesocial.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.pjtech.android.ridesocial.RideSocialApp;

/**
 * Created by android on 6/15/17.
 */

public class UberUtils {

    public static String uberPackageName = "com.ubercab";
    public static String lyftPackageName = "me.lyft.android";
    public static String uberClientId   = "TpWly3dfN_FdG-vb5QYbi6GtM-NDTf-d";
    public static String lyftClientId   = "w_fnNoyKPRLF";

    public static boolean installedPackage(String packageName)
    {
        PackageManager pm = RideSocialApp.mContext.getPackageManager();
        boolean app_installed = false;
        try {
            app_installed = pm.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    public static void startUberCabServiceCall(Activity activity, LatLng pickup, LatLng dropoff, String nickName)
    {
        try {
            PackageManager pm = RideSocialApp.mContext.getPackageManager();
            pm.getPackageInfo(uberPackageName, PackageManager.GET_ACTIVITIES);
            String uri = "uber://?action=setPickup&pickup[latitude]=" + pickup.latitude +
                    "&pickup[longitude]=" + pickup.longitude + "&pickup[nickname]=" + nickName +
                    "&dropoff[latitude]=" + dropoff.latitude + "&dropoff[longitude]="+dropoff.longitude +
                    "&dropoff[nickname]=" + nickName + "&client_id=" + uberClientId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            activity.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            // No Uber app! Open mobile website.
            String url = "https://m.uber.com/sign-up?client_id=" + uberClientId;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        }
    }

    public static void startLyftCabServiceCall(Activity activity, LatLng pickup, LatLng dropoff, String nickName)
    {
        try {
            PackageManager pm = RideSocialApp.mContext.getPackageManager();
            pm.getPackageInfo(lyftPackageName, PackageManager.GET_ACTIVITIES);
            String uri = "lyft://ridetype?id=lyft&pickup[latitude]=" + pickup.latitude +
                    "&pickup[longitude]=" + pickup.longitude + "&destination[latitude]=" + dropoff.latitude +
                    "&destination[longitude]=" + dropoff.longitude;

            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
            playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            playStoreIntent.setData(Uri.parse(uri));
            activity.startActivity(playStoreIntent);

        } catch (PackageManager.NameNotFoundException e) {
            // No Uber app! Open mobile website.
            String url = "https://www.lyft.com/signup/SDKSIGNUP?clientId=" + lyftClientId + "&sdkName=android_direct";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        }
    }
}
