package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.model.UserLoginResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.DeviceUtil;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoActivity extends BaseActivity implements View.OnClickListener {

    //Android View Control
    Context mContext = null;
    RelativeLayout logo  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContext = this;

        //control init
        logo    = (RelativeLayout)this.findViewById(R.id.logo);

        //control event
        logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(logo))
        {
            loginCheck();
        }
    }

    private void loginCheck()
    {
        if (!DeviceUtil.isNetworkAvailable(this))
        {
            Toast.makeText(this, this.getText(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
            return;
        }

        String username = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_USERNAME, "");
        String verify = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_VERIFYCODE, "");
        String cellNum = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_CALLNUM, "");

        if (TextUtils.isEmpty(username))
        {
            startSignUpActivity();
            return;
        }
        else if (TextUtils.isEmpty(verify))
        {
            startSignUpActivity();
            return;
        }
        else if (TextUtils.isEmpty(cellNum))
        {
            startSignUpActivity();
            return;
        }

        dlg_progress.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<UserLoginResponse> call = apiService.login(username, cellNum, verify);

        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {

                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    startHomeActivity();
                    return;
                }

                dlg_progress.dismiss();
                if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.no_existing_name), Toast.LENGTH_SHORT).show();
                    startSignUpActivity();
                }
                else if (response.body().status.equals("2")) {
                    Toast.makeText(mContext, mContext.getText(R.string.invalid_verify), Toast.LENGTH_SHORT).show();
                    startSignUpActivity();
                }
                else if (response.body().status.equals("3")) {
                    Toast.makeText(mContext, mContext.getText(R.string.noactive_account), Toast.LENGTH_SHORT).show();
                    startSignUpActivity();
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                // Log error here since request failed
                dlg_progress.dismiss();
                Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startHomeActivity()
    {
        String username = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_USERNAME, "");
        String cellNum = PreferenceUtils.getStringValue(this, PreferenceUtils.KEY_CALLNUM, "");

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<UserInfo> call = apiService.getUserInfo(username, cellNum);

        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                dlg_progress.dismiss();
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    GlobalData.userInfo = response.body();

                    Intent intent = new Intent(LogoActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.no_existing_name), Toast.LENGTH_SHORT).show();
                    startSignUpActivity();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                // Log error here since request failed
                dlg_progress.dismiss();
                Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(LogoActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
