package com.pjtech.android.ridesocial.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import static com.pjtech.android.ridesocial.utils.MapUtils.downloadUrl;

/**
 * Created by android on 6/15/17.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

    LatLng origin;
    LatLng dest;
    boolean lineType;

    public DownloadTask(LatLng org, LatLng destination, boolean type)
    {
        super();
        origin = org;
        dest = destination;
        lineType = type;
    }

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserJsonTask parserTask = new ParserJsonTask(origin, dest, lineType);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);
    }
}
