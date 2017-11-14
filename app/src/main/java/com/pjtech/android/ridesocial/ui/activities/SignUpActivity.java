package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.model.UserSignupResponse;
import com.pjtech.android.ridesocial.model.VerifyResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    //variable
    Context mContext    = null;

    //Control Variable
    EditText mUserName  = null;
    EditText mCellNum   = null;
    EditText mVerifyCo  = null;
    Button mVerifyBtn   = null;
    Button mSignup      = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContext    = this;

        //control init
        mUserName   = (EditText)this.findViewById(R.id.edt_user);
        mCellNum    = (EditText)this.findViewById(R.id.edt_callnumber);
        mVerifyCo   = (EditText)this.findViewById(R.id.edt_verify);
        mVerifyBtn  = (Button)this.findViewById(R.id.btn_verify);
        mSignup     = (Button)this.findViewById(R.id.btn_signup);

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verify code product
                String username     = mUserName.getText().toString();
                String cellNum      = mCellNum.getText().toString();

                if (TextUtils.isEmpty(username))
                {
                    mUserName.setError(mContext.getString(R.string.username_err));
                }
                else if (TextUtils.isEmpty(cellNum))
                {
                    mCellNum.setError(mContext.getString(R.string.cellnum_err));
                }
                else
                {
                    dlg_progress.show();
                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);
                    Call<UserSignupResponse> call = apiService.signup(username, cellNum);

                    call.enqueue(new Callback<UserSignupResponse>() {
                        @Override
                        public void onResponse(Call<UserSignupResponse> call, Response<UserSignupResponse> response) {
                            dlg_progress.dismiss();
                            if (!response.isSuccessful()) return;

                            if (response.body().status.equals("0")) {
                                mVerifyCo.requestFocus();
                            }
                            else if (response.body().status.equals("1")) {
                                Toast.makeText(mContext, mContext.getText(R.string.signup_err), Toast.LENGTH_SHORT).show();
                                mUserName.requestFocus();
                            }
                            else if (response.body().status.equals("2")) {
                                Toast.makeText(mContext, mContext.getText(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                                mUserName.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserSignupResponse> call, Throwable t) {
                            // Log error here since request failed
                            dlg_progress.dismiss();
                            Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sign up code
                String username     = mUserName.getText().toString();
                String cellNum      = mCellNum.getText().toString();
                String verifyCode   = mVerifyCo.getText().toString();

                if (TextUtils.isEmpty(username))
                {
                    mUserName.setError(mContext.getString(R.string.username_err));
                }
                else if (TextUtils.isEmpty(cellNum))
                {
                    mCellNum.setError(mContext.getString(R.string.cellnum_err));
                }
                else if (TextUtils.isEmpty(verifyCode))
                {
                    mVerifyCo.setError(mContext.getString(R.string.verifyinsert_err));
                }
                else
                {
                    dlg_progress.show();
                    ApiInterface apiService =
                            ApiClient.getClient().create(ApiInterface.class);
                    Call<VerifyResponse> call = apiService.verifyPhoneCode(username, cellNum, verifyCode, GlobalData.playerID);

                    call.enqueue(new Callback<VerifyResponse>() {
                        @Override
                        public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                            if (!response.isSuccessful()) return;

                            if (response.body().status.equals("0")) {
                                //sign up
                                PreferenceUtils.putStringValue(mContext, PreferenceUtils.KEY_USERID, response.body().userId);
                                startProfileRegisterActivity();
                                return;
                            }
                            else if (response.body().status.equals("1")) {
                                Toast.makeText(mContext, mContext.getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                                mUserName.requestFocus();
                            }
                            else if (response.body().status.equals("2")) {
                                Toast.makeText(mContext, mContext.getText(R.string.invalid_verify), Toast.LENGTH_SHORT).show();
                                mVerifyCo.requestFocus();
                            }
                            dlg_progress.dismiss();
                        }

                        @Override
                        public void onFailure(Call<VerifyResponse> call, Throwable t) {
                            // Log error here since request failed
                            dlg_progress.dismiss();
                            Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void startProfileRegisterActivity()
    {
        String username     = mUserName.getText().toString();
        String cellNum      = mCellNum.getText().toString();
        String verifyCode   = mVerifyCo.getText().toString();

        PreferenceUtils.putStringValue(mContext, PreferenceUtils.KEY_USERNAME, username);
        PreferenceUtils.putStringValue(mContext, PreferenceUtils.KEY_CALLNUM, cellNum);
        PreferenceUtils.putStringValue(mContext, PreferenceUtils.KEY_VERIFYCODE, verifyCode);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<UserInfo> call = apiService.getUserInfo(username, cellNum);

        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                dlg_progress.dismiss();
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    PreferenceUtils.putStringValue(mContext, PreferenceUtils.KEY_USERID, response.body().userId);
                    GlobalData.userInfo = response.body();

                    Intent intent = new Intent();
                    intent.setClass(SignUpActivity.this, FBLinkActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.no_existing_name), Toast.LENGTH_SHORT).show();
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
}
