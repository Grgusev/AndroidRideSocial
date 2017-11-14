package com.pjtech.android.ridesocial.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pjtech.android.ridesocial.RideSocialApp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by android on 6/15/17.
 */

public class ParserJsonTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    LatLng origin;
    LatLng dest;
    boolean lineType;

    public ParserJsonTask(LatLng org, LatLng destination, boolean type)
    {
        super();
        origin = org;
        dest = destination;
        lineType = type;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionJsonParser parser = new DirectionJsonParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();
        String distance = "";
        String duration = "";

        if (result == null) return;

        if (result.size() < 1) {
            Toast.makeText(RideSocialApp.mContext, "No Points", Toast.LENGTH_SHORT).show();
            return;
        }

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            points.add(origin);
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    distance = (String) point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    duration = (String) point.get("duration");
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
            points.add(dest);

            // Adding all the points in the route to LineOptions

            lineOptions.addAll(points);
            lineOptions.width(5);
            lineOptions.color(Color.BLUE);
        }

        // Drawing polyline in the Google Map for the i-th route
        MapUtils.mPolyLines.add(MapUtils.mMap.addPolyline(lineOptions));
    }
}
