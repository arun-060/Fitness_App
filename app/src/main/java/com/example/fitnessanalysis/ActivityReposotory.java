package com.example.fitnessanalysis;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class ActivityRepository {

    public ActivityDao mActivity;
    public LiveData<List<MyActivity>> mAllActivity;

    public ActivityRepository(Application application) {
        ActivityDatabase db = ActivityDatabase.getDatabase(application);
        mActivity = db.activityDao();
    }

    LiveData<List<MyActivity>> getmAllActivity() {
        return mAllActivity;
    }

    void insert(MyActivity myActivity) {
        ActivityDatabase.databaseWriteExecutor.execute(()-> {
            mActivity.addActivity(myActivity);
        });
    }

//    void getMyActivity() {
//        ActivityDatabase.databaseWriteExecutor.execute(()-> {
//            mAllActivity = mActivity.getAllActivity();
//        });
//    }

}
