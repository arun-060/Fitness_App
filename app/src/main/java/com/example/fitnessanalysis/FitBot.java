package com.example.fitnessanalysis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class FitBot extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private TextInputEditText userInputEditText;
    LinearLayout chatHistoryLayout;
    Activity activity;
    private GenerativeModel model;
    GenerationConfig config;
    List<Content> chatHistory;
    List<String> messages;
    ChatFutures chat;
    FloatingActionButton sendButton;


    private String mParam1;
    private String mParam2;

    public FitBot() {
    }

    public static FitBot newInstance(String param1, String param2) {
        FitBot fragment = new FitBot();
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fit_bot, container, false);
        userInputEditText = view.findViewById(R.id.user_message);
        sendButton = view.findViewById(R.id.send_btn);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        connectWithGemini();

        chatAdapter = new ChatAdapter(chatHistory);
        chatRecyclerView.setAdapter(chatAdapter );

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuestion = userInputEditText.getText().toString().trim();
                if (userQuestion.isEmpty()) {
                    return;
                }
                userInputEditText.setText("");
                appendMessage(userQuestion, "user");
                sendMessageToAi(userQuestion);
            }
        });

        return view;
    }

    private void connectWithGemini() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 5120;
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
//        modelContentBuilder.addText("## Hey there! Roney the fitness expert here, ready to answer your questions and help you reach your fitness goals!" +
//                "Here are some prompts I can help you with:" +
//                "**General Fitness:*** What's the best exercise for someone who wants to lose weight/gain muscle/improve endurance?" +
//                "* How can I stay motivated to exercise regularly?" +
//                "* What are some common mistakes beginners make when starting a fitness routine?" +
//                "* How can I create a workout plan that fits my needs and goals?" +
//                "* What are some healthy and nutritious foods I should be eating to fuel my workouts?" +
//                "* How can I recover properly after exercise to prevent injuries?" +
//                "* What are some tips for staying hydrated during exercise?" +
//                "* How can I overcome a plateau in my fitness progress?" +
//                "* What are some fun and creative ways to stay active besides going to the gym?" +
//                "* What are the benefits of stretching and how often should I do it?" +
//                "**Specific Exercises and Workouts:**" +
//                "* Can you show me how to do a proper squat/lunge/push-up/pull-up?" +
//                "* What are some effective exercises I can do at home without any equipment?" +
//                "* Can you create a HIIT workout for me that focuses on burning fat?" +
//                "* What are some good exercises for building strength in my core/arms/legs?" +
//                "* Can you recommend a yoga routine for improving flexibility and reducing stress?" +
//                "**Nutrition and Diet:**" +
//                "   * What are some healthy snacks I can eat before and after my workouts?" +
//                "   * How can I calculate the number of calories I should be eating each day?" +
//                "   * What are some good sources of protein/carbohydrates/healthy fats?" +
//                "   * Should I be taking any supplements to help me reach my fitness goals?" +
//                "   * What are some tips for meal prepping healthy meals for the week?" +
//                "   **Feel free to ask me anything related to fitness and I'll do my best to help you out!**");
//        Content modelContent = modelContentBuilder.build();
        modelContentBuilder.addText("Hey there! Roney the fitness expert here, ready to answer your questions and help you reach your fitness goals!\n" +
                "Here are some prompts I can help you with:\n\n" +
                "General Fitness:\n\n" +
                "What's the best exercise for someone who wants to lose weight/gain muscle/improve endurance?\n\n" +
                "How can I stay motivated to exercise regularly?\n\n" +
                "What are some common mistakes beginners make when starting a fitness routine?\n\n" +
                "How can I create a workout plan that fits my needs and goals?\n\n" +
                "What are some healthy and nutritious foods I should be eating to fuel my workouts?\n\n" +
                "How can I recover properly after exercise to prevent injuries?\n\n" +
                "What are some tips for staying hydrated during exercise?\n\n" +
                "How can I overcome a plateau in my fitness progress?\n\n" +
                "What are some fun and creative ways to stay active besides going to the gym?\n\n" +
                "What are the benefits of stretching and how often should I do it?\n\n" +
                "Specific Exercises and Workouts:\n\n" +
                "Can you show me how to do a proper squat/lunge/push-up/pull-up?\n\n" +
                "What are some effective exercises I can do at home without any equipment?\n\n" +
                "Can you create a HIIT workout for me that focuses on burning fat?\n\n" +
                "What are some good exercises for building strength in my core/arms/legs?\n\n" +
                "Can you recommend a yoga routine for improving flexibility and reducing stress?\n\n" +
                "Nutrition and Diet:\n\n" +
                "What are some healthy snacks I can eat before and after my workouts?\n\n" +
                "How can I calculate the number of calories I should be eating each day?\n\n" +
                "What are some good sources of protein/carbohydrates/healthy fats?\n\n" +
                "Should I be taking any supplements to help me reach my fitness goals?\n\n" +
                "What are some tips for meal prepping healthy meals for the week?\n\n" +
                "Feel free to ask me anything related to fitness and I'll do my best to help you out!");
        Content modelContent = modelContentBuilder.build();

        chatHistory = new ArrayList<>();
        messages = new ArrayList<>();
        messages.add("## Hey there! Roney the fitness expert here, ready to answer your questions and help you reach your fitness goals! \\n\\nHere are some prompts I can help you with:\\n\\n**General Fitness:**\\n\\n* What's the best exercise for someone who wants to lose weight/gain muscle/improve endurance?\\n* How can I stay motivated to exercise regularly?\\n* What are some common mistakes beginners make when starting a fitness routine?\\n* How can I create a workout plan that fits my needs and goals?\\n* What are some healthy and nutritious foods I should be eating to fuel my workouts?\\n* How can I recover properly after exercise to prevent injuries?\\n* What are some tips for staying hydrated during exercise?\\n* How can I overcome a plateau in my fitness progress?\\n* What are some fun and creative ways to stay active besides going to the gym?\\n* What are the benefits of stretching and how often should I do it?\\n\\n**Specific Exercises and Workouts:**\\n\\n* Can you show me how to do a proper squat/lunge/push-up/pull-up?\\n* What are some effective exercises I can do at home without any equipment?\\n* Can you create a HIIT workout for me that focuses on burning fat?\\n* What are some good exercises for building strength in my core/arms/legs?\\n* Can you recommend a yoga routine for improving flexibility and reducing stress?\\n\\n**Nutrition and Diet:**\\n\\n* What are some healthy snacks I can eat before and after my workouts?\\n* How can I calculate the number of calories I should be eating each day?\\n* What are some good sources of protein/carbohydrates/healthy fats?\\n* Should I be taking any supplements to help me reach my fitness goals?\\n* What are some tips for meal prepping healthy meals for the week?\\n\\n**Feel free to ask me anything related to fitness and I'll do my best to help you out!**");
        chatHistory.add(userContent);
        chatHistory.add(modelContent);


        chat = model.startChat(chatHistory);

    }
    private void sendMessageToAi(String userQuestion) {
        Content.Builder messageBuilder = new Content.Builder();
        messageBuilder.setRole("user");
        messageBuilder.addText(userQuestion);
        Content message = messageBuilder.build();


        chatHistory.add(message);
        messages.add(String.valueOf(message));

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
                        appendMessage(messageFromAi, "model");
                        messages.add(messageFromAi);
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error sending message", Objects.requireNonNull(t.getMessage()));
            }
        }, Executors.newSingleThreadExecutor());
    }
    @SuppressLint("ResourceType")
    private void appendMessage(String message, String role) {
        messages.add(message);
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.smoothScrollToPosition(messages.size());
    }

}