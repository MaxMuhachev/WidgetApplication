package com.maxim.widgets.controllers.api;

import com.maxim.widgets.models.Widget;
import com.maxim.widgets.cache.CacheManager;
import com.maxim.widgets.services.RateLimitService;
import com.maxim.widgets.services.WidgetService;
import com.maxim.widgets.utils.WidgetUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/widgets")
@Api(value = "widgets", tags = "Работа с виджетами")
public class WidgetController {

    @Autowired
    private WidgetService widgetService;

    private final CacheManager widgetCacheManager = CacheManager.getInstance();

    @PostMapping("")
    @ApiOperation(value = "Создать виджет")
    public Widget create(
            @RequestParam
                    Integer x,
            @RequestParam
                    Integer y,
                    Integer z,
            @RequestParam
                    Integer width,
            @RequestParam
                    Integer height,
            HttpServletResponse response
    ) {

        if (WidgetUtil.isNotNullOrZeroAttributes(x, y, width, height)) {
            return  widgetService.create(x, y, z, width, height, response);
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Some variable is null or equal 0.");
    }

    @PutMapping("")
    @ApiOperation(value = "Изменить виджет")
    public Widget update(
            @RequestParam
                    Long id,
            @RequestParam
                    Integer x,
            @RequestParam
                    Integer y,
                    Integer z,
            @RequestParam
                    Integer width,
            @RequestParam
                    Integer height,
            HttpServletResponse response
    ) {
        if (widgetCacheManager.get(id) != null) {
            return widgetService.update(id, x, y, z, width, height, response);
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Widget moy found");
    }

    @DeleteMapping("")
    @ApiOperation(value = "Удалить виджет")
    public String delete(
            Long widgetId,
            HttpServletResponse response
    ) {
        if (widgetCacheManager.get(widgetId) != null) {
            widgetService.delete(widgetId, response);
            return "Ok";
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Widget moy found");
    }

    @GetMapping("getById")
    @ApiOperation(value = "Получить виджет по Id")
    public Widget getById(
            Long id,
            HttpServletResponse response
    ) {
        if (CacheManager.getInstance().get(id) != null) {
            return widgetService.get(id, response);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Widget Not Found");
        }
    }

    @GetMapping("getAll")
    @ApiOperation(value = "Получить все виджеты")
    public List<Widget> getAll(
            @RequestParam(defaultValue = "1")
                Short page,
            @RequestParam(defaultValue = "10")
                Short size,
            HttpServletResponse response
    ) {
        if (page < 1) {page = 1;}
        if (size < 1) {size = 10;}
        if (size > 500) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Size of more then 500 forbidden!");
        }
        return widgetService.getAll(page, size, response);
    }

    @GetMapping("getAllByRange")
    @ApiOperation(value = "Фильтрация по диопазону")
    public List<Widget> getAllByRange(
            @RequestParam(defaultValue = "0")
                    Integer lowX,
            @RequestParam(defaultValue = "0")
                    Integer lowY,
            @RequestParam(defaultValue = "0")
                    Integer highX,
            @RequestParam(defaultValue = "0")
                    Integer highY,
            HttpServletResponse response
    ) {
        return widgetService.getAllByRange(lowX, lowY, highX, highY, response);
    }
}
