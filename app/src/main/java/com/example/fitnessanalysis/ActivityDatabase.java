package com.example.fitnessanalysis;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MyActivity.class}, version = 1, exportSchema = false)
public abstract class ActivityDatabase extends RoomDatabase {
    public abstract ActivityDao activityDao();
    private static volatile ActivityDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ActivityDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ActivityDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ActivityDatabase.class, "activity_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
