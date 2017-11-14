package com.pjtech.android.ridesocial.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


    /**
     * backend server url
     */
//    public static final String BASE_URL = "http://192.168.0.21:81/";
    public static final String BASE_URL = "http://52.15.128.36/";

    private static Retrofit retrofit = null;

    /**
     * Function to get retrofit instance
     * @return retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
