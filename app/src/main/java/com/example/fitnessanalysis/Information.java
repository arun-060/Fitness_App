package com.example.fitnessanalysis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment {

    Scope fitnessActivityReadScope = new Scope("https://www.googleapis.com/auth/fitness.activity.read");
    Scope fitnessLocationReadScope = new Scope("https://www.googleapis.com/auth/fitness.location.read");
    TextView heartRate;
    int RC_SIGN_IN = 1;
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;

    private String mParam1;
    private String mParam2;

    public Information(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }
    public static Information newInstance(String param1, String param2) {
        Information fragment = new Information(new Activity());
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (ContextCompat.checkSelfPermission(this.activity, android.Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission" , "permission already granted");
        }
        else {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ACTIVITY_RECOGNITION);
        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(fitnessActivityReadScope, fitnessLocationReadScope)
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this.activity, googleSignInOptions);
        long startTime = System.currentTimeMillis();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d("SignIn", "Starting Sign-In at: " + startTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                readStepData(account);
                readHeartRateData(account);
//                readActivity(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
            }
        }
    }

    private void readStepData(GoogleSignInAccount account) {
        Fitness.getHistoryClient(this.activity, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        long totalSteps = 0;
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                totalSteps += dp.getValue(field).asInt();
                                Log.d("count", String.valueOf(totalSteps));
                            }
                        }
                        Log.d("count", String.valueOf(totalSteps));
                    }
                });
    }
    private void readHeartRateData(GoogleSignInAccount account) {
        long endTime = System.currentTimeMillis(); // Set the end time to the current time

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        DataReadRequest request = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this.activity, account)
                .readData(request)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<HeartRateEntry> heartRateEntries = new ArrayList<>();

                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                for (Field field : dataPoint.getDataType().getFields()) {
                                    Value value = dataPoint.getValue(field);
                                    float heartRate = value.asFloat();  // Extract heart rate value
                                    long timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS);
                                    // Create HeartRateEntry object and add it to the list
                                    heartRateEntries.add(new HeartRateEntry(timestamp, heartRate));
                                }
                            }
                        }

                        // Sort the heart rate entries based on timestamp in descending order
                        heartRateEntries.sort(new Comparator<HeartRateEntry>() {
                            @Override
                            public int compare(HeartRateEntry entry1, HeartRateEntry entry2) {
                                // Sort in descending order
                                return Long.compare(entry2.getTimestamp(), entry1.getTimestamp());
                            }
                        });

                        // Log or utilize the sorted heart rate entries as required
                        for (HeartRateEntry entry : heartRateEntries) {
                            Log.d("HeartRateData", "Heart Rate: " + entry.getHeartRate() + ", Timestamp: " + getFormattedDateTime(entry.getTimestamp()));
                        }
                        heartRate = getView().findViewById(R.id.heartRate);
                        heartRate.setText("Heart rate : " + heartRateEntries.get(0).getHeartRate());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to read heart rate data
                        Log.e("HeartRate", "Failed to read heart rate data", e);
                    }
                });
    }
    private String getFormattedDateTime(long timestamp) {
        // Create a SimpleDateFormat instance with the desired date-time format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        // Convert the timestamp to a Date object
        Date date = new Date(timestamp);

        // Format the Date object to a string
        return sdf.format(date);
    }
}