package com.maxim.widgets.models.contract;


public interface RateLimiterInterface {
    public void setLimit(long count, long maxEvents);

    public boolean allowNextEvent();

    public void nextEvent();

    public long getCounter();
}