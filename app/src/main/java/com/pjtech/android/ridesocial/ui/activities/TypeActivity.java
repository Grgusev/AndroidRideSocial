package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeActivity extends BaseActivity {

    Context mContext;
    RadioGroup mDriveSelect;

    int driving = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        mContext = this;

        mDriveSelect = (RadioGroup)this.findViewById(R.id.drive_type);

        this.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDriveSelect.getCheckedRadioButtonId() == R.id.driving)
                    driving = 0;
                else
                    driving = 1;

                startHomeActivity();
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

                    Intent intent = new Intent(TypeActivity.this, HomeActivity.class);
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
        Intent intent = new Intent(TypeActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
