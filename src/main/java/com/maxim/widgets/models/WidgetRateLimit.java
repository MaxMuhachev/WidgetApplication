package com.maxim.widgets.models;

import com.maxim.widgets.models.contract.RateLimiterInterface;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class WidgetRateLimit implements RateLimiterInterface {
    private long lastTimerReset = System.currentTimeMillis();

    private long max = 0;

    private long period = 0;

    private long counter = 0;

    public WidgetRateLimit(long count, long maxEvents) {
        this.max = maxEvents;
        resetTimer();
    }

    public Long getMaxRate() {
        return max;
    }

    public void setLimit(long count, long maxEvents) {
        this.max = maxEvents;
        resetTimer();
    }

    public boolean allowNextEvent() {
        resetTimer();
        return counter <= max;
    }

    public long getCountOfRemaining() {
        return  max - counter;
    }

    public void nextEvent() {
        counter++;
    }

    public long getCounter() {
        return counter;
    }

    public Date getTimeWhenReset() {
        return new Date(lastTimerReset + period);
    }

    public String getTimeWhenResetString() {
        return new Date(lastTimerReset + period).toString();
    }

    private void resetTimer() {
        long now = System.currentTimeMillis();
        if ((now - lastTimerReset) > period) {
            lastTimerReset = now;
            counter = 0;
            period = TimeUnit.MINUTES.toMillis(1);
        }
    }
}