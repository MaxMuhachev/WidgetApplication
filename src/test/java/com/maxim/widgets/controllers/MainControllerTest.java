package com.maxim.widgets.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void main() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",String.class)
            ).contains("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Widgets</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>Hello. For work with api click link: <a href=\"swagger-ui.html\">swagger-ui.html</a></p>\n" +
                "</body>\n" +
                "</html>"
            );
    }
}