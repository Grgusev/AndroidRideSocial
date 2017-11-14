package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;
import com.pjtech.android.ridesocial.utils.UberUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CabServiceActivity extends BaseActivity {

    Context mContext    = null;
    Button mNextBtn     = null;
    TextView mSkipBtn     = null;

    CheckBox mUber  = null;
    CheckBox mLyft  = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_service);
        mContext = this;

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mNextBtn    = (Button)this.findViewById(R.id.btn_next);
        mSkipBtn    = (TextView)this.findViewById(R.id.btn_skip);

        mUber   = (CheckBox)this.findViewById(R.id.uber_check);
        mLyft   = (CheckBox)this.findViewById(R.id.lyft_check);

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTypeActivity();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUberLink();
            }
        });
    }

    private void saveUberLink()
    {
        int uberChecked = (mUber.isChecked())?1:0;
        int lyftChecked = (mLyft.isChecked())?1:0;
        String userID = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_USERID, "");

        dlg_progress.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<NormalResponse> call = apiService.updateUberChecked(userID, uberChecked, lyftChecked);

        call.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    //sign up
                    startTypeActivity();
                    return;
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                }
                dlg_progress.dismiss();
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                // Log error here since request failed
                dlg_progress.dismiss();
                Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UberUtils.installedPackage(UberUtils.uberPackageName))
        {
            mUber.setVisibility(View.VISIBLE);
            if (GlobalData.userInfo.uberEnable == 1)
            {
                mUber.setChecked(true);
            }
        }
        else
        {
            mUber.setVisibility(View.GONE);
            mUber.setChecked(false);
        }

        if (UberUtils.installedPackage(UberUtils.lyftPackageName))
        {
            mLyft.setVisibility(View.VISIBLE);
            if (GlobalData.userInfo.lyftEnable == 1)
            {
                mLyft.setChecked(true);
            }
        }
        else
        {
            mLyft.setVisibility(View.GONE);
            mLyft.setChecked(false);
        }
    }

    private void startTypeActivity()
    {
        Intent intent = new Intent(CabServiceActivity.this, TypeActivity.class);
        startActivity(intent);
        finish();
    }
}
