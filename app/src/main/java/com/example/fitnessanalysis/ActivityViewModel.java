package com.example.fitnessanalysis;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ActivityViewModel extends AndroidViewModel {
    private ActivityRepository activityRepository;

    public ActivityViewModel(Application application) {
        super(application);
        activityRepository = new ActivityRepository(application);
    }


    public void insert(MyActivity activity) {
        activityRepository.insert(activity);
    }

    // Add more methods as needed, such as update, delete, etc.
}
