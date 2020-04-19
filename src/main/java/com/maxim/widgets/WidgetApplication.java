package com.maxim.widgets;

import com.maxim.widgets.cache.RateLimitCacheManager;
import com.maxim.widgets.enums.SectionOfRateLimit;
import com.maxim.widgets.models.WidgetRateLimit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class WidgetApplication {

	public static void main(String[] args) {
		RateLimitCacheManager rateLimitCacheManager = RateLimitCacheManager.getInstance();
		rateLimitCacheManager.put(SectionOfRateLimit.ALL, new WidgetRateLimit(0, 1000));
		SpringApplication.run(WidgetApplication.class, args);
	}

}
