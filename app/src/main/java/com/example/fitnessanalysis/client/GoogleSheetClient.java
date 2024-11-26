package com.example.fitnessanalysis.client;

import android.util.Log;

import com.example.fitnessanalysis.data.ActivityData;
import com.example.fitnessanalysis.services.GoogleSheetsApiServices;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleSheetClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://script.google.com/macros/s/AKfycbwv6UoYWBS_rw9HwfK4l_cCV-yaQ1-O1ENuJzrpLiE1RGEgo7zBMTqBOmSLeZ87hSo/exec/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
