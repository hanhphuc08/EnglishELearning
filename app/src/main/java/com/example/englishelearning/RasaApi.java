package com.example.englishelearning;

import com.example.englishelearning.model.RasaRequest;
import com.example.englishelearning.model.RasaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RasaApi {
    @POST("/webhooks/rest/webhook")
    Call<List<RasaResponse>> sendMessage(@Body RasaRequest request);
}
