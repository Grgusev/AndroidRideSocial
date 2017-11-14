package com.pjtech.android.ridesocial.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.ui.dialog.MyProgressDialog;
import com.pjtech.android.ridesocial.utils.DeviceUtil;

/**
 * Created by android on 6/7/17.
 */

public class BaseActivity extends FragmentActivity {

    // UI
    public MyProgressDialog dlg_progress;
    // Data
    public boolean isErrorOccured = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dlg_progress = new MyProgressDialog(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_left, R.anim.out_left);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!DeviceUtil.isNetworkAvailable(this)) {
            isErrorOccured = true;
            ErrorNetworkActivity.OpenMe();
        }
    }

    public void myBack() {
        finish();
        overridePendingTransition(R.anim.in_right, R.anim.out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dlg_progress != null)
            dlg_progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        myBack();
    }
}
