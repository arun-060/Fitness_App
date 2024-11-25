package com.example.fitnessanalysis.services;

import androidx.activity.ViewTreeOnBackPressedDispatcherOwner;

import com.example.fitnessanalysis.data.ActivityData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GoogleSheetsApiServices {

    @POST("https://script.google.com/macros/s/AKfycbylHWRqGKCOEBWvKekm19lBw1rMDRxuLjkjKkEoG-nuVeSt92COuRuPc6EeVgqIIVQV/exec/")
    public abstract Call<Void> sendActivityData(@Body ActivityData activityData);
}
