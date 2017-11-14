package com.pjtech.android.ridesocial.ui.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pjtech.android.ridesocial.R;

public class HelpActivity extends BaseActivity {

    Context mContext;

    //Control
    Button helpButton;
    Button feedButton;

    LinearLayout mHelpSection;
    RelativeLayout mFeedSection;

    EditText mFeedText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mContext = this;

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBack();
            }
        });

        helpButton = (Button)this.findViewById(R.id.btn_help);
        feedButton = (Button)this.findViewById(R.id.btn_feedback);

        mHelpSection = (LinearLayout)this.findViewById(R.id.helpSection);
        mFeedSection = (RelativeLayout) this.findViewById(R.id.feedbackSection);

        mFeedText = (EditText)this.findViewById(R.id.feedback);
        sendButton = (Button)this.findViewById(R.id.send);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedButton.setBackgroundResource(R.drawable.bg_btn_right);
                helpButton.setBackgroundResource(R.drawable.bg_btn_left_fill);
                feedButton.setTextColor(Color.parseColor("#00a2e9"));
                helpButton.setTextColor(Color.parseColor("#FFFFFF"));
                mHelpSection.setVisibility(View.VISIBLE);
                mFeedSection.setVisibility(View.GONE);
            }
        });

        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedButton.setBackgroundResource(R.drawable.bg_btn_right_fill);
                helpButton.setBackgroundResource(R.drawable.bg_btn_left);
                feedButton.setTextColor(Color.parseColor("#FFFFFF"));
                helpButton.setTextColor(Color.parseColor("#00a2e9"));
                mHelpSection.setVisibility(View.GONE);
                mFeedSection.setVisibility(View.VISIBLE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackString = mFeedText.getText().toString();
                sendFeedback(feedbackString);
            }
        });
    }

    private void sendFeedback(String feedback)
    {
        // submit feedback
    }
}
