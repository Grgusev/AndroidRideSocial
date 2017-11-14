package com.pjtech.android.ridesocial.ui.interfaces;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.RideSocialApp;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.ui.activities.HomeActivity;
import com.pjtech.android.ridesocial.ui.fragments.HomeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 6/13/17.
 */

public class PaymentRequestRecManager {
    HomeFragment mFragment;
    View rootView;

    TextView mTotalFare;
    TextView mRequestFare;

    long totalFare;
    long amountFare;

    String payment_request_id;

    public void setTotalFare(float fares)
    {
        mTotalFare.setText(mFragment.getActivity().getString(R.string.total_fare) + fares + "$");
    }

    public void setRequestFare(float fares)
    {
        mRequestFare.setText(mFragment.getActivity().getString(R.string.payment_request) + fares + "$");
    }

    public PaymentRequestRecManager(View rootView, HomeFragment fragment)
    {
        this.rootView = rootView;
        mFragment = fragment;

        mTotalFare = (TextView)rootView.findViewById(R.id.total_fare_req);
        mRequestFare = (TextView)rootView.findViewById(R.id.request_fare_req);

        rootView.findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPaidReceive();
            }
        });
    }

    public void setData(Bundle data) {
        totalFare = data.getLong("total_fare");
        amountFare = data.getLong("requested_amount");
        payment_request_id = data.getString("payment_request_id");

        setTotalFare(totalFare);
        setRequestFare(amountFare);
    }

    public void sendPaidReceive()
    {
        mFragment.dlg_progress.show();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<NormalResponse> call = apiService.sendPaidMessage(GlobalData.userInfo.userId, GlobalData.rideInfo.routerID,
                GlobalData.hostingID, amountFare, payment_request_id);

        call.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {
                mFragment.dlg_progress.dismiss();

                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    return;
                }
                if (response.body().status.equals("1")) {
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                // Log error here since request failed
                mFragment.dlg_progress.show();
            }
        });
    }
}
