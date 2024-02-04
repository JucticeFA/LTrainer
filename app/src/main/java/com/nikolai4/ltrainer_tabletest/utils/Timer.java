package com.nikolai4.ltrainer_tabletest.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Timer {
    private long timeStart = 0;
    private long timeStop = 0;

    public void start() {
        timeStart = Calendar.getInstance().getTimeInMillis();
    }

    public void stop() {
        timeStop = Calendar.getInstance().getTimeInMillis();
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeStop() {
        return timeStop;
    }

    public long totalTimeMillis() {
        return timeStop - timeStart;
    }

    public String totalTime() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
        Date date = new Date(totalTimeMillis());
        return format.format(date);
    }
}
