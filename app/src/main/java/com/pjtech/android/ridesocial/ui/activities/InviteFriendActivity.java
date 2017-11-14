package com.pjtech.android.ridesocial.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.utils.SharingUtils;

public class InviteFriendActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        this.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtils.SharingToSocialMedia(InviteFriendActivity.this, "com.facebook.katana", "Facebook");
            }
        });

        this.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtils.SharingToSocialMedia(InviteFriendActivity.this, "com.twitter.android", "Twitter");
            }
        });

        this.findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtils.SharingToSocialMedia(InviteFriendActivity.this, "com.whatsapp", "Whatsapp");
            }
        });

        this.findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtils.SharingToSocialMedia(InviteFriendActivity.this, "com.instagram.android", "Instagram");
            }
        });

        this.findViewById(R.id.btn_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(InviteFriendActivity.this, SendInvitePhone.class);
                startActivity(intent);
            }
        });
    }
}
