package com.pjtech.android.ridesocial.ui.interfaces;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

/**
 * Created by android on 6/13/17.
 */

public class PaymentReceiveManager {
    HomeFragment mFragment;
    View rootView;

    TextView mTotalFare;
    TextView mRequestFare;
    TextView mReceiveFare;

    long totalFare;
    long request_mount;
    long receive_mount;

    public void setTotalFare(float fares)
    {
        mTotalFare.setText(mFragment.getActivity().getString(R.string.total_fare) + fares + "$");
    }

    public void setRequestFare(float fares)
    {
        mRequestFare.setText(mFragment.getActivity().getString(R.string.payment_request) + fares + "$");
    }

    public void setReceiveFare(float fares)
    {
        mReceiveFare.setText(mFragment.getActivity().getString(R.string.payment_receive) + fares + "$");
    }

    public PaymentReceiveManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mTotalFare = (TextView)rootView.findViewById(R.id.total_fare_receive);
        mRequestFare = (TextView)rootView.findViewById(R.id.request_fare_receive);
        mReceiveFare = (TextView)rootView.findViewById(R.id.receive_fare_receive);

        rootView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setData(Bundle data) {
        totalFare = data.getLong("total_fare");
        request_mount = data.getLong("requested_amount");
        receive_mount = data.getLong("payment_request_id");

        setTotalFare(totalFare);
        setRequestFare(request_mount);
        setReceiveFare(receive_mount);
    }
}
