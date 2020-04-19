package com.maxim.widgets.controllers.api;

import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.WidgetRateLimit;
import com.maxim.widgets.services.RateLimitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rateLimit")
@Api(value = "restriction", tags = "Работа с ограниченями количества запросов")
public class RateLimitController {

    @Autowired
    private RateLimitService rateLimitService;

    @PutMapping("")
    @ApiOperation(value = "Изменить ограничение количества запросов")
    public WidgetRateLimit updateRestriction(
            @RequestParam(defaultValue = "1000")
                    Integer restriction,
            @RequestParam
                    SectionOfRateLimit targetSectionString
    ) {
        return rateLimitService.updateRestriction(restriction, targetSectionString);
    }

    @GetMapping("")
    @ApiOperation(value = "Получить все ограничения запросов")
    public Map<SectionOfRateLimit, WidgetRateLimit> getAll() {
        return rateLimitService.getAll();
    }
}
