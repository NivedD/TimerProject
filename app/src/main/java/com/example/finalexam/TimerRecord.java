package com.example.finalexam;

// In TimerRecord.java
public class TimerRecord {
    private long duration;
    private String endTime;

    public TimerRecord(long duration, String endTime) {
        this.duration = duration;
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getEndTime() {
        return endTime;
    }
}
