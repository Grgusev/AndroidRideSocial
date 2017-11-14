package com.pjtech.android.ridesocial.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PaymentStatusResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterVemeoActivity extends BaseActivity {
    @BindView(R.id.username)
    EditText userName;

    @BindView(R.id.edt_password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vemeo);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_next)
    public void nextClick()
    {
        if (TextUtils.isEmpty(userName.getText().toString()))
        {
            userName.setError(this.getString(R.string.email_error));
            return;
        }

        if (TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError(this.getString(R.string.password_error));
        }

        dlg_progress.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentStatusResponse> call = apiService.savePaymentMethod(GlobalData.userInfo.userId, "0", "venmo",
                GlobalData.userInfo.userName, userName.getText().toString(), password.getText().toString());

        call.enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {

                dlg_progress.dismiss();
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    finish();
                    return;
                }
            }

            @Override
            public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                // Log error here since request failed
                dlg_progress.dismiss();
                Toast.makeText(RegisterVemeoActivity.this, RegisterVemeoActivity.this.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_skip)
    public void skipClick()
    {
        finish();
    }
}
