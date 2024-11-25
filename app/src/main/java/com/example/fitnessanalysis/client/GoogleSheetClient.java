package com.example.fitnessanalysis.client;

import android.util.Log;

import com.example.fitnessanalysis.data.ActivityData;
import com.example.fitnessanalysis.services.GoogleSheetsApiServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleSheetClient {

    private static final String BASE_URL = "https://script.google.com/macros/s/AKfycbylHWRqGKCOEBWvKekm19lBw1rMDRxuLjkjKkEoG-nuVeSt92COuRuPc6EeVgqIIVQV/exec/";
    private GoogleSheetsApiServices googleSheetsApiServices;


    //Initializing Retrofit
    public GoogleSheetClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleSheetsApiServices = retrofit.create(GoogleSheetsApiServices.class);
    }

    public void sendActivityData(ActivityData activityData) {
        Call<Void> call = googleSheetsApiServices.sendActivityData(activityData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.w("Success", "Data sent successfully");
                } else {
                    Log.w("Error", "Failed to send data");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
