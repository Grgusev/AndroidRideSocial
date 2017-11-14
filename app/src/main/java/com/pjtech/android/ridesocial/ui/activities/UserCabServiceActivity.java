package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.UberUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCabServiceActivity extends BaseActivity {

    CheckBox mUberCheck;
    CheckBox mLyftCheck;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cab_service);
        mContext = this;

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        mUberCheck = (CheckBox)this.findViewById(R.id.uber_check);
        mLyftCheck = (CheckBox)this.findViewById(R.id.lyft_check);

        this.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUberLinks();
            }
        });
    }

    private void saveUberLinks() {
        dlg_progress.show();

        final int uberChecked = (mUberCheck.isChecked())?1:0;
        final int lyftChecked = (mLyftCheck.isChecked())?1:0;

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<NormalResponse> call = apiService.updateUberChecked(GlobalData.userInfo.userId, uberChecked, lyftChecked);

        call.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                dlg_progress.dismiss();
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    Toast.makeText(mContext, mContext.getText(R.string.saved), Toast.LENGTH_SHORT).show();
                    GlobalData.userInfo.uberEnable = uberChecked;
                    GlobalData.userInfo.lyftEnable = lyftChecked;
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                }
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
            mUberCheck.setVisibility(View.VISIBLE);
            if (GlobalData.userInfo.uberEnable == 1)
            {
                mUberCheck.setChecked(true);
            }
            else mUberCheck.setChecked(false);
        }
        else
        {
            mUberCheck.setVisibility(View.GONE);
            mUberCheck.setChecked(false);
        }

        if (UberUtils.installedPackage(UberUtils.lyftPackageName))
        {
            mLyftCheck.setVisibility(View.VISIBLE);
            if (GlobalData.userInfo.lyftEnable == 1)
            {
                mLyftCheck.setChecked(true);
            }
            else mLyftCheck.setChecked(false);
        }
        else
        {
            mLyftCheck.setVisibility(View.GONE);
            mLyftCheck.setChecked(false);
        }
    }
}
