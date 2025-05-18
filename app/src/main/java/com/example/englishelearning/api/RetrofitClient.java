package com.example.englishelearning.api;

import com.example.englishelearning.RasaApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://55b4-2a09-bac5-d46d-16c8-00-245-c2.ngrok-free.app";
    private static RasaApi rasaApi;
    public static RasaApi getRasaApi() {
        if (rasaApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            rasaApi = retrofit.create(RasaApi.class);
        }
        return rasaApi;
    }
}
