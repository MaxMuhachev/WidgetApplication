package com.maxim.widgets.cache;

import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.WidgetRateLimit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RateLimitCacheManager {
    private static RateLimitCacheManager instance;
    private static final Object monitor = new Object();
    private Map<SectionOfRateLimit, WidgetRateLimit> cache = Collections.synchronizedMap(new LinkedHashMap<>());

    public WidgetRateLimit put(SectionOfRateLimit cacheKey, WidgetRateLimit value) {
        return cache.put(cacheKey, value);
    }

    public WidgetRateLimit get(SectionOfRateLimit cacheKey) {
        return cache.get(cacheKey);
    }

    public Map<SectionOfRateLimit, WidgetRateLimit> getMap() {
        return cache;
    }

    public static RateLimitCacheManager getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new RateLimitCacheManager();
                }
            }
        }
        return instance;
    }
}
