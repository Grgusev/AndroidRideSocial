package com.pjtech.android.ridesocial.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.ui.dialog.DeleteAccountDialog;
import com.pjtech.android.ridesocial.ui.dialog.LogoutDialog;

public class AccountActivity extends BaseActivity {

    public static AccountActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mInstance = this;

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        this.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutDialog dlg = new LogoutDialog(AccountActivity.this);
                dlg.show();
            }
        });

        this.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccountDialog dlg = new DeleteAccountDialog(AccountActivity.this);
                dlg.show();
            }
        });
    }
}
