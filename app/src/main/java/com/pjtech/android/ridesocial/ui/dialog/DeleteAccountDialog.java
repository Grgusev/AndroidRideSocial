package com.pjtech.android.ridesocial.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.ui.activities.AccountActivity;
import com.pjtech.android.ridesocial.ui.activities.SignUpActivity;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 6/15/17.
 */

public class DeleteAccountDialog extends Dialog {
    public DeleteAccountDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DeleteAccountDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected DeleteAccountDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(final Context context)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.delete_account_dialog);

        this.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyProgressDialog dlg_progress = new MyProgressDialog(context);

                dlg_progress.show();
                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<NormalResponse> call = apiService.removeUser(GlobalData.userInfo.userId, GlobalData.userInfo.verifyCode);

                call.enqueue(new Callback<NormalResponse>() {
                    @Override
                    public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                        dlg_progress.dismiss();
                        if (!response.isSuccessful()) return;

                        if (response.body().status.equals("0")) {
                            PreferenceUtils.putStringValue(AccountActivity.mInstance, PreferenceUtils.KEY_USERNAME, "");
                            PreferenceUtils.putStringValue(AccountActivity.mInstance, PreferenceUtils.KEY_VERIFYCODE, "");
                            PreferenceUtils.putStringValue(AccountActivity.mInstance, PreferenceUtils.KEY_CALLNUM, "");

                            Intent intent = new Intent(AccountActivity.mInstance, SignUpActivity.class);
                            AccountActivity.mInstance.startActivity(intent);
                            dismiss();
                            AccountActivity.mInstance.finish();
                            return;
                        }
                        else if (response.body().status.equals("1")) {
                            Toast.makeText(context, context.getText(R.string.no_user), Toast.LENGTH_SHORT).show();
                        }
                        dlg_progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<NormalResponse> call, Throwable t) {
                        // Log error here since request failed
                        dlg_progress.dismiss();
                        Toast.makeText(context, context.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
