package com.pjtech.android.ridesocial.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.ui.activities.AccountActivity;
import com.pjtech.android.ridesocial.ui.activities.SignUpActivity;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;

/**
 * Created by android on 6/15/17.
 */

public class LogoutDialog extends Dialog {

    public LogoutDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LogoutDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected LogoutDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.logout_dialog);

        this.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putStringValue(AccountActivity.mInstance, PreferenceUtils.KEY_VERIFYCODE, "");
                Intent intent = new Intent(RideSocialApp.mContext, SignUpActivity.class);
                AccountActivity.mInstance.startActivity(intent);
                dismiss();
                AccountActivity.mInstance.finish();
            }
        });
    }
}
