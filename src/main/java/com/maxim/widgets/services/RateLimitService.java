package com.maxim.widgets.services;

import com.maxim.widgets.cache.RateLimitCacheManager;
import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.WidgetRateLimit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class RateLimitService {
    private final RateLimitCacheManager ratelimitCacheManager = RateLimitCacheManager.getInstance();

    public WidgetRateLimit updateRestriction(Integer limit, SectionOfRateLimit targetSection) {
        WidgetRateLimit rateLimit;
        try {
            rateLimit = targetSection == null ? ratelimitCacheManager.get(SectionOfRateLimit.ALL) : ratelimitCacheManager.get(targetSection);
            if (rateLimit != null) {
                rateLimit.setLimit(0, limit);
            } else {
                rateLimit = new WidgetRateLimit(0, limit);
                ratelimitCacheManager.put(targetSection, rateLimit);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Section is Not Found. " +
                    "Select some as `create`, `update`, `delete`, `getById`, `getAll`, `getAllByRange`"
            );
        }
        return rateLimit;
    }

    public Map<SectionOfRateLimit, WidgetRateLimit> getAll() {
        return ratelimitCacheManager.getMap();
    }

    public WidgetRateLimit getByName(SectionOfRateLimit sectionOfRateLimit) {
        return ratelimitCacheManager.get(sectionOfRateLimit);
    }
}
