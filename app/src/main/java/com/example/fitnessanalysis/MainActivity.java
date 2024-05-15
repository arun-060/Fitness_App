package com.example.fitnessanalysis;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 1;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new Information(this));
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            Fragment fragment = null;

            if (menuItem.getItemId() == R.id.navbar_dashboard) {
                fragment = new Information(this);
            }
            if (menuItem.getItemId() == R.id.navbar_fitBot) {
                fragment = new FitBot();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();


            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
    }

}