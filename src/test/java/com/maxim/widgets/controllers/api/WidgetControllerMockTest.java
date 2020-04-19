package com.maxim.widgets.controllers.api;

import com.maxim.widgets.WidgetApplication;
import com.maxim.widgets.cache.CacheManager;
import com.maxim.widgets.models.Widget;
import com.maxim.widgets.services.WidgetService;
import com.maxim.widgets.testdatafactories.WidgetTestDataFactory;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {WidgetApplication.class})
@AutoConfigureMockMvc
class WidgetControllerMockTest {
    @Autowired
    private WidgetController widgetController;
    @MockBean
    private WidgetService widgetService;
    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api/widgets";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(widgetController)
                .alwaysDo(print())
                .build();
    }


    @Test
    void create() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        Mockito.when(widgetService.create(
                any(Integer.class),
                any(Integer.class),
                any(Integer.class),
                any(Integer.class),
                any(Integer.class),
                any(HttpServletResponse.class)
        )).thenReturn(validWidget);
        this.mockMvc.perform(post(BASE_URL)
                .param("x", validWidget.getX().toString())
                .param("y", validWidget.getY().toString())
                .param("z", validWidget.getZ().toString())
                .param("width", validWidget.getWidth().toString())
                .param("height", validWidget.getHeight().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validWidget.getId()))
                .andExpect(jsonPath("$.x").value(validWidget.getX()))
                .andExpect(jsonPath("$.y").value(validWidget.getY()))
                .andExpect(jsonPath("$.z").value(validWidget.getZ()))
                .andExpect(jsonPath("$.width").value(validWidget.getWidth()))
                .andExpect(jsonPath("$.height").value(validWidget.getHeight()));
    }

    @Test
    public void createError() throws Exception {
        this.mockMvc.perform(post(BASE_URL)
                .param("x", "0")
                .param("y", "1")
                .param("z", "1")
                .param("width", "10")
                .param("height", "10")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void update() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        CacheManager.getInstance().put(validWidget.getId(), validWidget);
        Mockito.when(
                widgetService.update(
                        any(Long.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(Integer.class),
                        any(HttpServletResponse.class)
                )
        ).thenReturn(validWidget);
        this.mockMvc.perform(put(BASE_URL)
                .param("id", validWidget.getId().toString())
                .param("x", validWidget.getX().toString())
                .param("y", validWidget.getY().toString())
                .param("z", validWidget.getZ().toString())
                .param("width", validWidget.getWidth().toString())
                .param("height", validWidget.getHeight().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validWidget.getId()))
                .andExpect(jsonPath("$.x").value(validWidget.getX()))
                .andExpect(jsonPath("$.y").value(validWidget.getY()))
                .andExpect(jsonPath("$.z").value(validWidget.getZ()))
                .andExpect(jsonPath("$.width").value(validWidget.getWidth()))
                .andExpect(jsonPath("$.height").value(validWidget.getHeight()));
    }

    @Test
    public void updateNotFound() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        this.mockMvc.perform(put(BASE_URL)
                .param("id", validWidget.getId().toString())
                .param("x", validWidget.getX().toString())
                .param("y", validWidget.getZ().toString())
                .param("z", validWidget.getZ().toString())
                .param("width", validWidget.getWidth().toString())
                .param("height", validWidget.getHeight().toString())
        ).andExpect(status().isNotFound());
    }

    @Test
    public void deleteWidget() throws Exception {
        Widget validWidget = WidgetTestDataFactory.getValid();
        CacheManager.getInstance().put(validWidget.getId(), validWidget);

        this.mockMvc.perform(delete(BASE_URL)
                .param("widgetId", validWidget.getId().toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(content().string("Ok"));
    }

    @Test
    public void deleteError() throws Exception {
        this.mockMvc.perform(delete(BASE_URL)
                .param("widgetId", "1")
        ).andExpect(status().isNotFound());
    }
}