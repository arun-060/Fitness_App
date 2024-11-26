package com.example.fitnessanalysis.services;

import androidx.activity.ViewTreeOnBackPressedDispatcherOwner;

import com.example.fitnessanalysis.data.ActivityData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GoogleSheetsApiServices {

    @Headers("Content-Type: application/json")
    @POST("https://script.google.com/macros/s/AKfycbwv6UoYWBS_rw9HwfK4l_cCV-yaQ1-O1ENuJzrpLiE1RGEgo7zBMTqBOmSLeZ87hSo/exec")
    public abstract Call<Void> sendActivityData(@Body ActivityData activityData);
}
