package com.example.fitnessanalysis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment {
    private static final String AUTH_URL = "https://www.strava.com/oauth/token";
    private static final String ACTIVITIES_URL = "https://www.strava.com/api/v3/athlete/activities";
    private static final String CLIENT_ID = "121350";
    private static final String CLIENT_SECRET = "8d1eb89984755e8d7213733c42c600b95300bbbd";
    private static final String REFRESH_TOKEN = "f6bcfb09f4b112e8e783a33f0039316702df9884";


    Scope fitnessActivityReadScope = new Scope("https://www.googleapis.com/auth/fitness.activity.read");
    Scope fitnessLocationReadScope = new Scope("https://www.googleapis.com/auth/fitness.location.read");
    TextView heartRate;
    TextView activityType, distance, movingTime, activityName, speed, workoutReview;
    Button generateReview, saveButton;
    List<Content> chatHistory;
    ChatFutures chat;
    String heartRateData;
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
        workoutReview = view.findViewById(R.id.workoutReview);
        generateReview = view.findViewById(R.id.geneareReview);
        saveButton = view.findViewById(R.id.saveButton);

        connectWithGemini();

        return view;
    }

    private void connectWithGemini() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 10000;
        configBuilder.stopSequences = Arrays.asList("red");

        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.0-pro",
                "AIzaSyCW3-YNJfwTJBR-hAMuGB5Y08RbR1sSL1Q",
                generationConfig
        );

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content.Builder userContentBuilder = new Content.Builder();
        userContentBuilder.setRole("user");
        userContentBuilder.addText("your name is Roney who is a  fitness expert now you will generate all the prompts as ronney");
        Content userContent = userContentBuilder.build();

        Content.Builder modelContentBuilder = new Content.Builder();
        modelContentBuilder.setRole("model");
        modelContentBuilder.addText("## Hey there! Roney the fitness expert here, ready to answer your questions and help you reach your fitness goals! \\n\\nHere are some prompts I can help you with:\\n\\n**General Fitness:**\\n\\n* What's the best exercise for someone who wants to lose weight/gain muscle/improve endurance?\\n* How can I stay motivated to exercise regularly?\\n* What are some common mistakes beginners make when starting a fitness routine?\\n* How can I create a workout plan that fits my needs and goals?\\n* What are some healthy and nutritious foods I should be eating to fuel my workouts?\\n* How can I recover properly after exercise to prevent injuries?\\n* What are some tips for staying hydrated during exercise?\\n* How can I overcome a plateau in my fitness progress?\\n* What are some fun and creative ways to stay active besides going to the gym?\\n* What are the benefits of stretching and how often should I do it?\\n\\n**Specific Exercises and Workouts:**\\n\\n* Can you show me how to do a proper squat/lunge/push-up/pull-up?\\n* What are some effective exercises I can do at home without any equipment?\\n* Can you create a HIIT workout for me that focuses on burning fat?\\n* What are some good exercises for building strength in my core/arms/legs?\\n* Can you recommend a yoga routine for improving flexibility and reducing stress?\\n\\n**Nutrition and Diet:**\\n\\n* What are some healthy snacks I can eat before and after my workouts?\\n* How can I calculate the number of calories I should be eating each day?\\n* What are some good sources of protein/carbohydrates/healthy fats?\\n* Should I be taking any supplements to help me reach my fitness goals?\\n* What are some tips for meal prepping healthy meals for the week?\\n\\n**Feel free to ask me anything related to fitness and I'll do my best to help you out!**");
        Content modelContent = modelContentBuilder.build();

        chatHistory = new ArrayList<>();
        chatHistory.add(userContent);
        chatHistory.add(modelContent);

        chat = model.startChat(chatHistory);
    }
    private void getFeedback(JSONObject activity) throws JSONException {
        Content.Builder messageBuilder = new Content.Builder();
        messageBuilder.setRole("user");
        messageBuilder.addText("Hey Ronney I have completed a workout of type " + activity.getString("type") + " where covered about " + activity.getString("distance") + " for about " + activity.getInt("moving_time")/60 + " with an average speed of " + activity.getDouble("average_speed") + " where my heart rate was " + heartRate.getText().toString().trim() + " can you analyse my workout and provide me feedback for further improvement. If my stats are below average or below average you are free to give negative review. Compare my performance with the an average fill person.");
        Content message = messageBuilder.build();


        chatHistory.add(message);

        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(message);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String messageFromAi = result.getText();
                Content.Builder aiMessageBuilder = new Content.Builder();
                aiMessageBuilder.setRole("model");
                aiMessageBuilder.addText(messageFromAi);
                Content aiContent = aiMessageBuilder.build();
                chatHistory.add(aiContent);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        workoutReview.setText(messageFromAi);
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error sending message", Objects.requireNonNull(t.getMessage()));
            }
        }, Executors.newSingleThreadExecutor());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                new FetchActivitiesTask().execute();
                readHeartRateData(account);
//                readActivity(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
            }
        }
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

                        heartRateEntries.add(new HeartRateEntry(111111111, 88));

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
                        heartRate = getView().findViewById(R.id.heartRate);

                        heartRateData = String.valueOf(heartRateEntries.get(0).getHeartRate());
                        heartRate.setText(String.format("Heart rate : %s", heartRateData));

//                        Log.d("Heart rate" , heartRateEntries.get(1).toString());
//

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to read heart rate data
                        Log.e("HeartRate", "Failed to read heart rate data", e);
                        heartRate = getView().findViewById(R.id.heartRate);
                        heartRate.setText(String.format("Heart rate : %s", "88"));
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
    private class FetchActivitiesTask extends AsyncTask<Void, Void, List<JSONObject>> {

        @Override
        protected List<JSONObject> doInBackground(Void... voids) {
            List<JSONObject> activityList = new ArrayList<>();
            try {
                // Fetch access token
                String accessToken = fetchAccessToken();

                if (accessToken != null) {
                    // Fetch activities using access token
                    activityList = fetchActivities(accessToken);
                } else {
                    Log.e("FetchActivitiesTask", "Failed to obtain access token.");
                }
            } catch (IOException | JSONException e) {
                Log.e("FetchActivitiesTask", "Error fetching activities", e);
            }
            return activityList;
        }

        @Override
        protected void onPostExecute(List<JSONObject> activityList) {
            super.onPostExecute(activityList);
            // Handle the fetched activities here
            if (!activityList.isEmpty()) {
                try {
                    // Get the topmost activity
                    JSONObject topActivity = activityList.get(0);
                    // Display the topmost activity
                    displayActivity(topActivity);
                    generateReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                getFeedback(topActivity);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case when activityList is empty
                // For example, display a message indicating no activities were found
                Log.e("FetchActivitiesTask", "No activities found.");
            }
        }

    }

    private String getCurrentDate() {
        Date currentDate = new Date();

        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Convert the date to string
        String dateString = dateFormat.format(currentDate);

        return dateString;
    }
    private void displayActivity(JSONObject activity) throws JSONException {
        // Create a TextView to display the activity
        activityType = getView().findViewById(R.id.activity_type);
        distance = getView().findViewById(R.id.distance);
        movingTime = getView().findViewById(R.id.moving_time);
        activityName = getView().findViewById(R.id.activity_name);
        speed = getView().findViewById(R.id.speed);

        activityName.setText("Activity Name : " + activity.getString("name"));

        activityType.setText("Activity Type : " + activity.getString("type"));
        distance.setText("Distance Travelled : " + activity.getDouble("distance")/1000 + "km");

        movingTime.setText("Moving Time : " + activity.getInt("moving_time")/60 + "min");
        speed.setText("Speed : " + activity.getDouble("average_speed") + "m/s");
//        (String name, String type, String distance, int movingTime, double averageSpeed)

//        ContactsDatabase database = ContactsDatabase.getDatabase(getApplicationContext());

//        ContactsDao contactsDao = database.contactsDao();
        Date current_date = new Date();
        MyActivity myActivity = new MyActivity(
                activity.getString("name"),
                activity.getString("type"),
                activity.getDouble("distance"),
                activity.getInt("moving_time"),
                activity.getDouble("average_speed"),
                heartRateData,
                getCurrentDate()
        );
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityDatabase database = ActivityDatabase.getDatabase(getContext());
                ActivityDao activityDao = database.activityDao();

                ActivityDatabase.databaseWriteExecutor.execute(() -> {
                    activityDao.addActivity(myActivity);
                });

                Toast.makeText(getContext(), "Activity Added", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String fetchAccessToken() throws IOException, JSONException {
        URL url = new URL(AUTH_URL + "?client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&refresh_token=" + REFRESH_TOKEN +
                "&grant_type=refresh_token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("access_token");
    }

    private List<JSONObject> fetchActivities(String accessToken) throws IOException, JSONException {
        List<JSONObject> activityList = new ArrayList<>();
        URL url = new URL(ACTIVITIES_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        JSONArray jsonArray = new JSONArray(response.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject activity = jsonArray.getJSONObject(i);
            activityList.add(activity);
        }

        return activityList;
    }
}