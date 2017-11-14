package com.pjtech.android.ridesocial.utils;

import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 6/23/17.
 */

public class PaymentUtils {
    public interface OnFinishedPaymentListener
    {
        void onFinishedPayment(int status);
    }

    public static void sendPaymentRequest(String user_id, String router_id, String pay_user_id, double amount, double total_amount, int all, final OnFinishedPaymentListener listener)
    {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<NormalResponse> call = apiService.sendPaymentRequest(user_id, router_id, pay_user_id, amount, all, total_amount);

        call.enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(Call<NormalResponse> call, Response<NormalResponse> response) {

                if (!response.isSuccessful()) {
                    listener.onFinishedPayment(-1);
                    return;
                }

                if (response.body().status.equals("0")) {
                    listener.onFinishedPayment(0);
                    return;
                }

                if (response.body().status.equals("1")) {
                    listener.onFinishedPayment(1);
                }
            }

            @Override
            public void onFailure(Call<NormalResponse> call, Throwable t) {
                // Log error here since request failed
                listener.onFinishedPayment(-2);
            }
        });
    }
}
