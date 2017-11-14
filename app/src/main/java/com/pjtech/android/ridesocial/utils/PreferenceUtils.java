package com.pjtech.android.ridesocial.utils;

/**
 * Created by Almond on 11/18/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static final String KEY_LONGITUDE = "KEY_LONGITITUDE";
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static String KEY_RECENTLY_MARKER_COUNT = "key_recently_marker_count";
    public static String KEY_USERNAME="USER_NAME";
    public static String KEY_CALLNUM="CALL_NUM";
    public static String KEY_VERIFYCODE="VERIFYCODE";
    public static String KEY_USERID="USER_ID";

    public static void putStringValue(Context mContext, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringValue(Context mContext, String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        try{
            return prefs.getString(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void putIntValue(Context mContext, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntValue(Context mContext, String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        try{
            return prefs.getInt(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void putDoubleValue(Context mContext, String key, double value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, (float)value);
        editor.commit();
    }

    public static double getDoubleValue(Context mContext, String key, float defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        try{
            return (double)prefs.getFloat(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }
}
