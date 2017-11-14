package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.FacebookUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFaceBookActivity extends BaseActivity {

    private Context mContext;

    Button linkButton;
    TextView fbMessage;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_face_book);
        mContext = this;

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        initFacebook();

        linkButton = (Button)this.findViewById(R.id.btn_link);
        fbMessage = (TextView)this.findViewById(R.id.fb_message);

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() == null)
                {
                    FacebookUtils.loginFB(UserFaceBookActivity.this);
                }
                else
                {
                    FacebookUtils.delink(UserFaceBookActivity.this);
                    linkButton.setText(R.string.link);
                    fbMessage.setText(R.string.fb_link_help);

                    //submit empty userid to backend

                }
            }
        });

        if (AccessToken.getCurrentAccessToken() == null)
        {
            linkButton.setText(R.string.link);
            fbMessage.setText(R.string.fb_link_help);
        }
        else
        {
            linkButton.setText(R.string.fb_no_link);

            Profile profile = Profile.getCurrentProfile();
            String fbMeseg = String.format(mContext.getString(R.string.fb_link_help_no_mesg), profile.getName());
            fbMessage.setText(fbMeseg);
        }
    }

    private void initFacebook() {
        FacebookUtils.init();

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken token = loginResult.getAccessToken();

                        if (token != null)
                        {
                            //back-end userid submit
                            String userid = loginResult.getAccessToken().getUserId();
                            GlobalData.userInfo.facebookUrl = userid;

                            ApiInterface apiService =
                                    ApiClient.getClient().create(ApiInterface.class);
                            Call<NormalResponse> call = apiService.updateFacebookUserId(GlobalData.userInfo.userId, userid);

                            call.enqueue(new Callback<NormalResponse>() {
                                @Override
                                public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                                    dlg_progress.dismiss();
                                    if (!response.isSuccessful()) return;

                                    if (response.body().status.equals("0")) {
                                        //sign up
                                        if(Profile.getCurrentProfile() == null) {
                                            mProfileTracker = new ProfileTracker() {
                                                @Override
                                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                                    // profile2 is the new profile
                                                    linkButton.setText(R.string.fb_no_link);
                                                    String fbMeseg = String.format(mContext.getString(R.string.fb_link_help_no_mesg), profile2.getName());
                                                    fbMessage.setText(fbMeseg);
                                                    mProfileTracker.stopTracking();
                                                }
                                            };
                                        }
                                        else {
                                            linkButton.setText(R.string.fb_no_link);
                                            Profile profile = Profile.getCurrentProfile();
                                            String fbMeseg = String.format(mContext.getString(R.string.fb_link_help_no_mesg), profile.getName());
                                            fbMessage.setText(fbMeseg);
                                        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
