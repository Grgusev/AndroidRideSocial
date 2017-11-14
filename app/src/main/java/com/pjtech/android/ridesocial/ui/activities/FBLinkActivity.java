package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.model.VerifyResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.FacebookUtils;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FBLinkActivity extends BaseActivity {

    Context mContext = null;
    RadioGroup mEnableFB    = null;
    Button mNext = null;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblink);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContext = this;

        FacebookUtils.init();

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //back-end fb userid submit
                        dlg_progress.show();
                        String fbUserId = loginResult.getAccessToken().getUserId();

                        String userID = PreferenceUtils.getStringValue(FBLinkActivity.this, PreferenceUtils.KEY_USERID, "");

                        ApiInterface apiService =
                                ApiClient.getClient().create(ApiInterface.class);
                        Call<NormalResponse> call = apiService.updateFacebookUserId(userID, fbUserId);

                        call.enqueue(new Callback<NormalResponse>() {
                            @Override
                            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                                dlg_progress.dismiss();
                                if (!response.isSuccessful()) return;

                                if (response.body().status.equals("0")) {
                                    //sign up
                                    startProfileManagerActivity();
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
                    public void onCancel() {
                        Toast.makeText(mContext, mContext.getText(R.string.fb_cancel), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        mEnableFB   = (RadioGroup)this.findViewById(R.id.enable_facebook);
        mNext   = (Button)this.findViewById(R.id.btn_next);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId    = mEnableFB.getCheckedRadioButtonId();

                if (selectId == R.id.yes) {
                    FacebookUtils.loginFB(FBLinkActivity.this);
                }
                else {
                    startProfileManagerActivity();
                }
            }
        });
    }

    private void startProfileManagerActivity()
    {
        Intent intent = new Intent();
        intent.setClass(FBLinkActivity.this, ProfileManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
}
