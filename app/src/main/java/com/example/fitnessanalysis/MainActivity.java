package com.example.fitnessanalysis;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 1;
    BottomNavigationView bottomNavigationView;
    Information information = new Information(this);
    FitBot fitBot = new FitBot(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            if (menuItem.getItemId() == R.id.dashboard_logo) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.dashboard, information)
                        .commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.fitbot) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fitBot_fragment, fitBot)
                        .commit();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
    }

}