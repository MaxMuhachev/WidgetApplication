package com.maxim.widgets.controllers.api;

import com.maxim.widgets.WidgetApplication;
import com.maxim.widgets.cache.RateLimitCacheManager;
import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.WidgetRateLimit;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {WidgetApplication.class})
@AutoConfigureMockMvc
public class RateLimitControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api/rateLimit";

    private final WidgetRateLimit rateLimitAll = new WidgetRateLimit(0, 1000);

    @Before
    public void setup() {
        RateLimitCacheManager.getInstance().put(SectionOfRateLimit.ALL, rateLimitAll);
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }


    @Test
    public void getAll() throws Exception {
        this.mockMvc.perform(get(BASE_URL)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ALL.counter").value(rateLimitAll.getCounter()))
                .andExpect(jsonPath("$.ALL.maxRate").value(rateLimitAll.getMaxRate()))
                .andExpect(jsonPath("$.ALL.timeWhenReset").value(rateLimitAll.getTimeWhenReset()))
                .andExpect(jsonPath("$.ALL.timeWhenResetString").value(rateLimitAll.getTimeWhenResetString()));
    }

    @Test
    public void updateRestriction() throws Exception {
        this.mockMvc.perform(put(BASE_URL)
                .param("restriction", "1")
                .param("targetSectionString", SectionOfRateLimit.CREATE_WIDGET.toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.counter").value(0))
                .andExpect(jsonPath("$.maxRate").value(1));


        this.mockMvc.perform(put(BASE_URL)
                .param("restriction", "70")
                .param("targetSectionString", SectionOfRateLimit.DELETE_WIDGET.toString())
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.counter").value(0))
                .andExpect(jsonPath("$.maxRate").value(70));
    }
}