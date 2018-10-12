package com.example.test;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetroService {

    static final String DOMAIN_URL = "";

    @GET("view")
    Call<TempHumi> getTempHumi();
}
