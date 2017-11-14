package com.pjtech.android.ridesocial.utils;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.UserRideInfo;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by android on 6/14/17.
 */

public class MapUtils {
    public static GoogleMap mMap;

    public static GeoApiContext context;

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }

    }

    public static ArrayList<Marker> mRouterMarker = new ArrayList<>();
    public static ArrayList<Polyline> mPolyLines = new ArrayList<>();

    public static void clearRouterInfos()
    {
        for (int i = 0; i < mRouterMarker.size(); i ++)
        {
            mRouterMarker.get(i).remove();
        }

        for (int i = 0; i < mPolyLines.size(); i ++)
        {
            mPolyLines.get(i).remove();
        }

        mRouterMarker.clear();
        mPolyLines.clear();
    }

    public static void setRecentlyUsedMarker()
    {
        int count = PreferenceUtils.getIntValue(RideSocialApp.mContext, PreferenceUtils.KEY_RECENTLY_MARKER_COUNT, 0);

        for (int i = 0; i < count; i ++)
        {
            if (i < count - 6) continue;
            double longtitude = PreferenceUtils.getDoubleValue(RideSocialApp.mContext, PreferenceUtils.KEY_LONGITUDE + i, 0f);
            double latitude = PreferenceUtils.getDoubleValue(RideSocialApp.mContext, PreferenceUtils.KEY_LATITUDE + i, 0f);

            MapUtils.mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)));
        }
    }

    public static void addRecentlyUsedmarker(LatLng latLng)
    {
        int count = PreferenceUtils.getIntValue(RideSocialApp.mContext, PreferenceUtils.KEY_RECENTLY_MARKER_COUNT, 0);

        PreferenceUtils.putDoubleValue(RideSocialApp.mContext, PreferenceUtils.KEY_LATITUDE + count, latLng.latitude);
        PreferenceUtils.putDoubleValue(RideSocialApp.mContext, PreferenceUtils.KEY_LONGITUDE + count, latLng.longitude);

        count ++;
        PreferenceUtils.putIntValue(RideSocialApp.mContext, PreferenceUtils.KEY_RECENTLY_MARKER_COUNT, count);
    }

    public static void drawRouterInfos()
    {
        for (int i = 0; i < mRouterMarker.size(); i ++)
        {
            mRouterMarker.get(i).remove();
        }

        for (int i = 0; i < mPolyLines.size(); i ++)
        {
            mPolyLines.get(i).remove();
        }

        mRouterMarker.clear();
        mPolyLines.clear();

        if (GlobalData.rideInfo.meetUp != null)
        {
            mRouterMarker.add(addCustomMarker(GlobalData.rideInfo.meetUp, "Suggest Meetup", R.drawable.meetup));
        }
        if (GlobalData.rideInfo.dropOff != null)
        {
            mRouterMarker.add(addCustomMarker(GlobalData.rideInfo.dropOff, "Dropoff", R.drawable.dropoff));
            getRoadRouter(GlobalData.rideInfo.meetUp, GlobalData.rideInfo.dropOff);
        }

        MapUtils.addRecentlyUsedmarker(GlobalData.rideInfo.meetUp);
        MapUtils.addRecentlyUsedmarker(GlobalData.rideInfo.dropOff);

        for (int i = 0; i < GlobalData.rideInfo.rideInfos.size(); i ++)
        {
            UserRideInfo info = GlobalData.rideInfo.rideInfos.get(i);

            int distance = (int)(getDistance(info.orgPosition, GlobalData.rideInfo.meetUp) / 90);

            String title = RideSocialApp.mContext.getString(R.string.format_pickup, info.username);
            String walk_distance = RideSocialApp.mContext.getString(R.string.format_walk, distance);
            mRouterMarker.add(addCustomMarker(info.orgPosition, title + "\n" + walk_distance, R.drawable.start));

            distance = (int)(getDistance(info.destPosition, GlobalData.rideInfo.dropOff) / 90);
            title = RideSocialApp.mContext.getString(R.string.format_destination, info.username);
            walk_distance = RideSocialApp.mContext.getString(R.string.format_walk, distance);
            mRouterMarker.add(addCustomMarker(info.destPosition, title + "\n" + walk_distance, R.drawable.destination));

            drawDotline(info.orgPosition, GlobalData.rideInfo.meetUp);
            drawDotline(info.destPosition, GlobalData.rideInfo.dropOff);

        }
    }

    public static float getDistance(LatLng origin, LatLng destination)
    {
        if (origin == null || destination == null)
        {
            return 0;
        }

        Location loc1 = new Location("");
        loc1.setLatitude(origin.latitude);
        loc1.setLongitude(origin.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(destination.latitude);
        loc2.setLongitude(destination.longitude);

        float distanceInMeters = loc1.distanceTo(loc2);

        return distanceInMeters;
    }

    public static void drawDotline(LatLng origin, LatLng destination)
    {
        double difLat = destination.latitude - origin.latitude;
        double difLng = destination.longitude - origin.longitude;

        // retrieve current zoom level
        double zoom = mMap.getCameraPosition().zoom;

        // come up with distance between dotted lines that adjusts according to zoom levels
        double divLat = difLat / (zoom * 1.2);
        double divLng = difLng / (zoom * 1.2);
        LatLng tmpLatOri = origin;

        // add polyline to list
        for (int i = 0; i < (zoom * 1.2); i++) {
            LatLng loopLatLng = tmpLatOri;

            if (i > 0) {
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.40f), tmpLatOri.longitude + (divLng * 0.40f));
            }

            mPolyLines.add(mMap.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(Color.BLACK)
                    .width(4f)));
            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }
    }

    public static void animateMarker(final Marker marker, final LatLng endPosition) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(500); // duration 500 milisecond
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    public static Marker addCustomMarker(LatLng latLng, String title, int assetID)
    {
        LinearLayout tv = (LinearLayout) LayoutInflater.from(RideSocialApp.mContext).inflate(R.layout.map_popup, null);

        TextView name = (TextView)tv.findViewById(R.id.title);
        name.setText(title);

        if (title.equals(""))
        {
            name.setVisibility(View.GONE);
        }
        if (title.equals("Dropoff"))
        {
            name.setBackgroundResource(R.drawable.bg_btn_purple);
            name.setTextColor(Color.parseColor("#ffffff"));
        }
        else if (title.equals("Suggest Meetup"))
        {
            name.setBackgroundResource(R.drawable.bg_btn_green);
            name.setTextColor(Color.parseColor("#ffffff"));
        }

        ImageView icon = (ImageView)tv.findViewById(R.id.marker);
        icon.setImageResource(assetID);

        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.setDrawingCacheEnabled(true);
        tv.buildDrawingCache();
        Bitmap bm = tv.getDrawingCache();

        Marker marker = MapUtils.mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bm))
                .anchor(0.5f, 1f - icon.getHeight() / 2f / tv.getMeasuredHeight()));

        return marker;
    }

    public static void getRoadRouter(LatLng origin, LatLng dest)
    {
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask(origin, dest, true);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }
}
