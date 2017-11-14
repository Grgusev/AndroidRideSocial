package com.pjtech.android.ridesocial.ui.interfaces;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.ui.adapters.PaymentRequestAdapter;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;
import com.pjtech.android.ridesocial.utils.PaymentUtils;

/**
 * Created by android on 6/13/17.
 */

public class PaymentRequestManager {
    HomeFragment mFragment;
    View rootView;

    EditText mTotalFare;
    RadioGroup mMethodSelect;
    ListView mUserCustomList;
    TextView mPaymentStatus;

    PaymentRequestAdapter mAdapter = null;

    public PaymentRequestManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mUserCustomList = (ListView)rootView.findViewById(R.id.request_list);
        mMethodSelect = (RadioGroup)rootView.findViewById(R.id.method_select);
        mTotalFare = (EditText)rootView.findViewById(R.id.total_fare);
        mPaymentStatus = (TextView) rootView.findViewById(R.id.payment_status);

        mAdapter = new PaymentRequestAdapter(mFragment.getActivity());
        mUserCustomList.setAdapter(mAdapter);

        mAdapter.setClickListener(new PaymentRequestAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position, double amount) {
                PaymentUtils.sendPaymentRequest(GlobalData.userInfo.userId, GlobalData.rideInfo.routerID, GlobalData.rideInfo.rideInfos.get(position).userId,
                        amount, amount, 0, new PaymentUtils.OnFinishedPaymentListener() {
                    @Override
                    public void onFinishedPayment(int status) {
                        showFinishedStatus(status);
                    }
                });
            }
        });

        rootView.findViewById(R.id.btn_next_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectID = mMethodSelect.getCheckedRadioButtonId();

                if (selectID == R.id.equal_spilit)
                {
                    if (!TextUtils.isEmpty(mTotalFare.getText().toString()))
                    {
                        double total_amount = Double.parseDouble(mTotalFare.getText().toString());
                        if (total_amount > 0)
                        {
                            double fare = total_amount / GlobalData.rideInfo.rideInfos.size();

                            PaymentUtils.sendPaymentRequest(GlobalData.userInfo.userId, GlobalData.rideInfo.routerID, "", fare, total_amount, 1, new PaymentUtils.OnFinishedPaymentListener() {
                                @Override
                                public void onFinishedPayment(int status) {
                                    showFinishedStatus(status);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void paymentRequestSetup()
    {
        if (GlobalData.paymentMethods.isEmpty())
        {
            mPaymentStatus.setText(RideSocialApp.mContext.getString(R.string.format_payment_statua, GlobalData.userInfo.userName));
            mPaymentStatus.setVisibility(View.VISIBLE);
        }
        else
        {
            mPaymentStatus.setVisibility(View.GONE);
        }
    }

    public void showFinishedStatus(int status)
    {
        if (status == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.MyAlertDialogStyle);
            builder.setTitle("Payment Request");
            builder.setMessage(RideSocialApp.getContext().getString(R.string.Payment_request_success));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
        else if (status == 1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.MyAlertDialogStyle);
            builder.setTitle("Payment Request");
            builder.setMessage(RideSocialApp.getContext().getString(R.string.connect_err));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getContext(), R.style.MyAlertDialogStyle);
            builder.setTitle("Payment Request");
            builder.setMessage(RideSocialApp.getContext().getString(R.string.Payment_request_Failed));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }
}
