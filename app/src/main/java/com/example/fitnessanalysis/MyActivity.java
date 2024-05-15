package com.example.fitnessanalysis;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "my_activity")
public class MyActivity {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "distance")
    private double distance;
    @ColumnInfo(name = "movingTime")
    private int movingTime;
    @ColumnInfo(name = "averageSpeed")
    private double averageSpeed;
    @ColumnInfo(name = "hearRate")
    private String heartRate;
    @ColumnInfo(name = "time_stamp")
    private String time_stamp;

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(int movingTime) {
        this.movingTime = movingTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public MyActivity(String name, String type, double distance, int movingTime, double averageSpeed, String heartRate, String time_stamp) {
        this.name = name;
        this.type = type;
        this.distance = distance;
        this.movingTime = movingTime;
        this.averageSpeed = averageSpeed;
        this.heartRate = heartRate;
        this.time_stamp = time_stamp;
    }
}
