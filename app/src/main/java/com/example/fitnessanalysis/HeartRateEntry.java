package com.example.fitnessanalysis;

public class HeartRateEntry {
    private long timestamp;
    private float heartRate;

    public HeartRateEntry(long timestamp, float heartRate) {
        this.timestamp = timestamp;
        this.heartRate = heartRate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getHeartRate() {
        return heartRate;
    }
}