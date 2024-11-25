package com.example.fitnessanalysis.data;

import com.google.gson.annotations.SerializedName;

public class ActivityData {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("distance")
    private String distance;

    @SerializedName("movingTime")
    private int movingTime;

    @SerializedName("averageSpeed")
    private double averageSpeed;

    @SerializedName("heartRate")
    private String heartRate;

    @SerializedName("date")
    private String time_stamp;

    public ActivityData(int id, String name, String distance, String type, int movingTime, double averageSpeed, String heartRate, String time_stamp) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.type = type;
        this.movingTime = movingTime;
        this.averageSpeed = averageSpeed;
        this.heartRate = heartRate;
        this.time_stamp = time_stamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
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

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
