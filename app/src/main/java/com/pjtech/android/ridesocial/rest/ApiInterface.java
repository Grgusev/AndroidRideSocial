package com.pjtech.android.ridesocial.rest;

import com.pjtech.android.ridesocial.model.HostingResponse;
import com.pjtech.android.ridesocial.model.NormalResponse;
import com.pjtech.android.ridesocial.model.PaymentMethodResoponse;
import com.pjtech.android.ridesocial.model.PaymentStatusResponse;
import com.pjtech.android.ridesocial.model.PhotoUploadResponse;
import com.pjtech.android.ridesocial.model.RouterResponse;
import com.pjtech.android.ridesocial.model.UserInfo;
import com.pjtech.android.ridesocial.model.UserLoginResponse;
import com.pjtech.android.ridesocial.model.UserSignupResponse;
import com.pjtech.android.ridesocial.model.VerifyResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {

    /**
     * API to get detailed category information by categoryId
     * @param Login UserName
     * @param Cell Number
     * @param Verified Code
     * @return UserLoginResponse Object
     */
    @POST("api/user/login?")
    Call<UserLoginResponse> login(@Query("name") String userName, @Query("cell_number") String cellNum, @Query("verify_code") String verifyCode);

    /**
     * API to get detailed category information by categoryId
     * @param Login UserName
     * @param Cell Number
     * @return UserLoginResponse Object
     */
    @POST("api/user/signup?")
    Call<UserSignupResponse> signup(@Query("name") String userName, @Query("cell_number") String cellNum);

    /**
     * API to get detailed category information by categoryId
     * @param Login UserName
     * @param Cell Number
     * @param Verified Code
     * @return UserLoginResponse Object
     */
    @POST("api/user/verifyPhone?")
    Call<VerifyResponse> verifyPhoneCode(@Query("name") String userName, @Query("cell_number") String cellNum,
                                         @Query("verify_code") String verifyCode, @Query("onesignal_id") String playerID);

    /**
     * API to get detailed category information by categoryId
     * @param Login UserName
     * @param Cell Number
     * @return UserLoginResponse Object
     */
    @Multipart
    @POST("api/user/memberphoto?")
    Call<PhotoUploadResponse> uploadPhoto(@Query("name") String userName, @Query("cell_number") String cellNum, @Part MultipartBody.Part file);

    /**
     * API to get detailed category information by categoryId
     * @param Login UserName
     * @param Cell Number
     * @return UserLoginResponse Object
     */
    @POST("api/user/get?")
    Call<UserInfo> getUserInfo(@Query("name") String userName, @Query("cell_number") String cellNum);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param FacebookUserID
     * @return UserLoginResponse Object
     */
    @POST("api/user/updateFacebook?")
    Call<NormalResponse> updateFacebookUserId(@Query("user_id") String userID, @Query("facebook_url") String fbUserID);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param Cell Number
     * @return UserLoginResponse Object
     */
    @POST("api/user/updateUber?")
    Call<NormalResponse> updateUberChecked(@Query("user_id") String userID, @Query("uber") int uber, @Query("lyft") int lyft);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @return UserLoginResponse Object
     */
    @POST("api/user/logout?")
    Call<NormalResponse> logout(@Query("user_id") String userID);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @return UserLoginResponse Object
     */
    @POST("api/user/removeUser?")
    Call<NormalResponse> removeUser(@Query("user_id") String userID, @Query("verify_code") String verifycode);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param Name
     * @param Origin Point
     * @param Destination Point
     * @param Custom Time
     * @return RouterResponse Object
     */
    @POST("api/router/submitPath?")
    Call<RouterResponse> submitTravelPath(@Query("user_id") String userID, @Query("name") String name,
                                          @Query("origin") String origin, @Query("destination") String destination, @Query("custom_time") long custom);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/router/cancelPath?")
    Call<RouterResponse> cancelTravelPath(@Query("user_id") String userID, @Query("request_id") String request_id);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/router/calcRouter?")
    Call<RouterResponse> submitLastCalcRouter(@Query("user_id") String userID, @Query("request_id") String request_id);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/router/calcRouter?")
    Call<RouterResponse> cancelRouter(@Query("user_id") String userID, @Query("router_id") String router_id, @Query("request_id") String request_id);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/router/selectHosting?")
    Call<HostingResponse> selectHosting(@Query("user_id") String userID, @Query("router_id") String router_id,
                                        @Query("host_id") String host_id, @Query("request_id") String request_id);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/payment/request?")
    Call<NormalResponse> sendPaymentRequest(@Query("user_id") String userID, @Query("router_id") String router_id,
                                       @Query("pay_user_id") String pay_user_id, @Query("requested_amount") double requested_amount,
                                            @Query("all") int all, @Query("total_amount") double total_amount);

    /**
     * API to get detailed category information by categoryId
     * @param UserID
     * @param RequestID
     * @return RouterResponse Object
     */
    @POST("api/payment/paid?")
    Call<NormalResponse> sendPaidMessage(@Query("user_id") String userID, @Query("router_id") String router_id,
                                            @Query("host_user_id") String host_user_id, @Query("paid_amount") long paid_amount,
                                         @Query("payment_request_id") String payment_request_id);

    @POST("api/payment/received?")
    Call<NormalResponse> sendPaymentReceived(@Query("user_id") String userID, @Query("router_id") String router_id,
                                         @Query("host_user_id") String host_user_id, @Query("received_amount") long received_amount,
                                         @Query("payment_request_id") String payment_request_id);

    @POST("api/history/submitRate?")
    Call<NormalResponse> submitRate(@Query("user_id") String userID, @Query("router_id") String router_id,
                                             @Query("rate") long rate);

    @POST("api/history/get?")
    Call<NormalResponse> getHistory(@Query("user_id") String userID);

    @POST("api/payment/getMethod?")
    Call<PaymentMethodResoponse> getPaymentMethod(@Query("user_id") String userID);

    @POST("api/payment/saveMethod?")
    Call<PaymentStatusResponse> savePaymentMethod(@Query("user_id") String userID, @Query("method_id") String method_id,
                                                  @Query("method") String method, @Query("username") String username,
                                                  @Query("email") String email, @Query("activated") String activated);

    @POST("api/payment/removeMethod?")
    Call<PaymentStatusResponse> removePaymentMethod(@Query("user_id") String userID, @Query("method_id") String method_id);
}
