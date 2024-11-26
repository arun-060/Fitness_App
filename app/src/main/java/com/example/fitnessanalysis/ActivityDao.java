package com.example.fitnessanalysis;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addActivity(MyActivity myActivity);

//    @Query("SELECT * FROM my_activity")
//    LiveData<List<MyActivity>> getAllActivity();
}
