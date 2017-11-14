package com.pjtech.android.ridesocial;

import android.widget.Toast;

import com.pjtech.android.ridesocial.firebase.FirebaseManager;
import com.pjtech.android.ridesocial.model.PaymentMethodResoponse;
import com.pjtech.android.ridesocial.model.PhoneContact;
import com.pjtech.android.ridesocial.model.RideInfo;
import com.pjtech.android.ridesocial.model.RouterResponse;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android on 6/13/17.
 */

public class GlobalData {
    public static String APPLINK = "https://play.google.com";
    public static UserInfo userInfo = null;
    public static RideInfo rideInfo = null;
    public static ArrayList<RideInfo> rideHistory = new ArrayList<>();
    public static String requestId = "";
    public static String playerID  = "";
    public static String hostingID = "";
    public static ArrayList<PhoneContact> phoneContacts;
    public static FirebaseManager mFirebaseManager;
    public static Map paymentMethods;

    public static String getName(String id)
    {
        if (id == null || id == "") return "";

        for (int i = 0; i < rideInfo.rideInfos.size(); i ++)
        {
            if (rideInfo.rideInfos.get(i).userId.equals(id))
                return rideInfo.rideInfos.get(i).username;
        }
        return "";
    }

    public interface UpdatePaymentListener
    {
        public void finish();
    }

    public static void updatePaymentMethod(final UpdatePaymentListener updateListener, final UpdatePaymentListener failListener) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentMethodResoponse> call = apiService.getPaymentMethod(GlobalData.userInfo.userId);

        call.enqueue(new retrofit2.Callback<PaymentMethodResoponse>() {
            @Override
            public void onResponse(Call<PaymentMethodResoponse> call, Response<PaymentMethodResoponse> response) {
                if (!response.isSuccessful()) {
                    failListener.finish();
                    return;
                }

                if (response.body().status.equals("0")) {
                    paymentMethods = response.body().methods;
                }
                updateListener.finish();
            }

            @Override
            public void onFailure(Call<PaymentMethodResoponse> call, Throwable t) {
                // Log error here since request failed
                failListener.finish();
            }
        });
    }
}
