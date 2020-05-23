package com.eina.chat.backendapi.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {

    @Value("${app.name}:")
    private String appName;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsAPI() {
        return registry -> registry.config().commonTags("application", appName);
    }

    private static int healthToCode(HealthEndpoint ep) {
        Status status = ep.health().getStatus();
        return status.equals(Status.UP) ? 1 : 0;
    }
}
