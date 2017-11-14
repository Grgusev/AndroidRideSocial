package com.pjtech.android.ridesocial.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by android on 6/18/17.
 */

public class FirebaseInstanceIDService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
