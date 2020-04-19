package com.maxim.widgets.services;

import com.maxim.widgets.cache.CacheManager;
import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.Widget;
import com.maxim.widgets.models.WidgetRateLimit;
import com.maxim.widgets.utils.WidgetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WidgetService {
    private final CacheManager cacheManager = CacheManager.getInstance();

    @Autowired
    private RateLimitService rateLimitService;

    public void seHeaderRateLimit(HttpServletResponse response, SectionOfRateLimit section) {
        WidgetRateLimit widgetRateLimit = rateLimitService.getByName(section);
        if (widgetRateLimit == null) {
            widgetRateLimit = rateLimitService.getByName(SectionOfRateLimit.ALL);
        }
        widgetRateLimit.nextEvent();
        response.addHeader("X-RateLimit-Limit", String.valueOf(widgetRateLimit.getMaxRate()));
        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(widgetRateLimit.getCountOfRemaining()));
        response.addHeader("X-Rate-Limit-Reset", widgetRateLimit.getTimeWhenReset().toString());
        if (!widgetRateLimit.allowNextEvent()) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    public Widget create(
            Integer x,
            Integer y,
            Integer z,
            Integer width,
            Integer height,
            HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.CREATE_WIDGET);
        Widget widget = new Widget(x, y, z, width, height);
        Map<Long, Widget> secondPartCache = new LinkedHashMap<>();
        secondPartCache.put(widget.getId(), widget);
        boolean insertInc = false;
        // Copy two part of map
        for (Map.Entry<Long, Widget> map : cacheManager.getSetOfMap()) {
            if (map.getValue().getZ().equals(z) || map.getValue().getZ() > z) {
                if (!insertInc && map.getValue().getZ() != null && widget.getZ().equals(map.getValue().getZ())) {
                    insertInc = true;
                }
                if (insertInc) {
                    map.getValue().setZ(map.getValue().getZ() + 1);
                }
                secondPartCache.put(map.getKey(), map.getValue());
            }
        }
        // Put two part
        secondPartCache.forEach(
                (key, value) -> {
                    cacheManager.clear(key);
                    cacheManager.put(key, value);
                }
        );
        return widget;
    }

    public Widget update(
            Long widgetKey,
            Integer x,
            Integer y,
            Integer z,
            Integer width,
            Integer height,
            HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.UPDATE_WIDGET);
        if (WidgetUtil.isNotNullOrZeroAttributes(x, y, width, height)) {
            return cacheManager.put(
                    widgetKey,
                    cacheManager.get(widgetKey)
                                .update(widgetKey, x, y, z, width, height)
            );
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Some variable is null or equal 0.");
        }
    }

    public void delete(
           Long widgetKey,
           HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.DELETE_WIDGET);
        cacheManager.clear(widgetKey);
    }

    public Widget get(
            Long cacheKey,
            HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.GET_BY_WIDGET_ID);
        return cacheManager.get(cacheKey);
    }

    public List<Widget> getAll(
            Short page,
            Short size,
            HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.GET_ALL_WIDGETS);
        return cacheManager.getAllPageValues(page, size);
    }

    public List<Widget> getAllByRange(
            Integer lowX,
            Integer lowY,
            Integer highX,
            Integer highY,
            HttpServletResponse response
    ) {
        seHeaderRateLimit(response, SectionOfRateLimit.GET_ALL_WIDGETS_BY_RANGE);
        return cacheManager.getAllValues().stream()
                .filter(widget -> {
                    Integer heightWidget = widget.getHeight()/2;
                    Integer widthWidget = widget.getWidth()/2;
                    return widget.getY() + heightWidget <= highY && widget.getX() + widthWidget <= highX
                            && widget.getY() - heightWidget >= lowY && widget.getX() - widthWidget >= lowX;
                })
                .collect(Collectors.toList());
    }
}
