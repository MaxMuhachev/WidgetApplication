package com.maxim.widgets.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.maxim.widgets.WidgetApplication;
import com.maxim.widgets.cache.CacheManager;
import com.maxim.widgets.cache.RateLimitCacheManager;
import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.Widget;
import com.maxim.widgets.models.WidgetRateLimit;
import com.maxim.widgets.services.WidgetService;
import com.maxim.widgets.testdatafactories.WidgetTestDataFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {WidgetApplication.class})
@AutoConfigureMockMvc
public class WidgetControllerIntegrationTest {

    @Autowired
    private WidgetController widgetController;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    public MockMvc mockMvc;

    private final String BASE_URL = "/api/widgets";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RateLimitCacheManager rateLimitCacheManager = RateLimitCacheManager.getInstance();
        rateLimitCacheManager.put(SectionOfRateLimit.ALL, new WidgetRateLimit(0, 1000));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void create() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        this.mockMvc.perform(post(BASE_URL)
                .param("x", validWidget.getX().toString())
                .param("y", validWidget.getY().toString())
                .param("z", validWidget.getZ().toString())
                .param("width", validWidget.getHeight().toString())
                .param("height", validWidget.getWidth().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.x").value(validWidget.getX()))
                .andExpect(jsonPath("$.y").value(validWidget.getY()))
                .andExpect(jsonPath("$.z").value(validWidget.getZ()))
                .andExpect(jsonPath("$.width").value(validWidget.getWidth()))
                .andExpect(jsonPath("$.height").value(validWidget.getHeight()));
        Widget validWidgetZIndexLess = WidgetTestDataFactory.getValidZIndexLess();
        this.mockMvc.perform(post(BASE_URL)
                .param("x", validWidgetZIndexLess.getX().toString())
                .param("y", validWidgetZIndexLess.getY().toString())
                .param("z", validWidgetZIndexLess.getZ().toString())
                .param("width", validWidgetZIndexLess.getWidth().toString())
                .param("height", validWidgetZIndexLess.getHeight().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.x").value(validWidgetZIndexLess.getX()))
                .andExpect(jsonPath("$.y").value(validWidgetZIndexLess.getY()))
                .andExpect(jsonPath("$.z").value(validWidgetZIndexLess.getZ()))
                .andExpect(jsonPath("$.width").value(validWidgetZIndexLess.getWidth()))
                .andExpect(jsonPath("$.height").value(validWidgetZIndexLess.getHeight()));

        this.mockMvc.perform(get(BASE_URL + "/getAll")
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    public void getByWidgetId() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        CacheManager.getInstance().put(validWidget.getId(), validWidget);
        this.mockMvc.perform(get(BASE_URL + "/getById")
                .param("id", validWidget.getId().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.x").value(validWidget.getX()))
                .andExpect(jsonPath("$.y").value(validWidget.getY()))
                .andExpect(jsonPath("$.z").value(validWidget.getZ()))
                .andExpect(jsonPath("$.width").value(validWidget.getWidth()))
                .andExpect(jsonPath("$.height").value(validWidget.getHeight()));
    }

    @Test
    public void getByWidgetIdNotFound() throws Exception {
        this.mockMvc.perform(get(BASE_URL + "/getById")
                .param("id", "1")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getAllByRangeWidgetsFoundTwo() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        Widget validWidgetZIndexLess = WidgetTestDataFactory.getValidZIndexLess();
        Widget validWidgetValidZIndexHundredXY = WidgetTestDataFactory.getValidZIndexHundredXY();

        CacheManager manager = CacheManager.getInstance();
        manager.clearAll();
        manager.put(validWidget.getId(), validWidget);
        manager.put(validWidgetZIndexLess.getId(), validWidgetZIndexLess);
        manager.put(validWidgetValidZIndexHundredXY.getId(), validWidgetValidZIndexHundredXY);

        Map<Long, Widget> expectedMap = new HashMap<>();
        expectedMap.put(validWidget.getId(), validWidget);
        expectedMap.put(validWidgetZIndexLess.getId(), validWidgetZIndexLess);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonExpected = ow.writeValueAsString(expectedMap.values());

        this.mockMvc.perform(get(BASE_URL + "/getAllByRange")
                .param("lowX", "0")
                .param("lowY", "0")
                .param("highX", "100")
                .param("highY", "150")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonExpected));
    }

    @Test
    public void getAllByRangeWidgetsFoundZero() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        Widget validWidgetZIndexLess = WidgetTestDataFactory.getValidZIndexLess();
        Widget validWidgetValidZIndexHundredXY = WidgetTestDataFactory.getValidZIndexHundredXY();

        CacheManager manager = CacheManager.getInstance();
        manager.put(validWidget.getId(), validWidget);
        manager.put(validWidgetZIndexLess.getId(), validWidgetZIndexLess);
        manager.put(validWidgetValidZIndexHundredXY.getId(), validWidgetValidZIndexHundredXY);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonExpected = ow.writeValueAsString(new ArrayList<>());

        this.mockMvc.perform(get(BASE_URL + "/getAllByRange")
                .param("lowX", "0")
                .param("lowY", "0")
                .param("highX", "-100")
                .param("highY", "-150")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonExpected));
    }
}
