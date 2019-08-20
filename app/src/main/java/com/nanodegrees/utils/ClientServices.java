package com.nanodegrees.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientServices {

    public static IServices getServices() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.URI_BASE_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IServices.class);
    }

}
