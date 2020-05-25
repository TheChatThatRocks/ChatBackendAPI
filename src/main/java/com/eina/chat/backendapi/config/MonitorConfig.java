package com.eina.chat.backendapi.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
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
    MeterRegistryCustomizer<PrometheusMeterRegistry> metricsAPI() {
        return registry -> registry.config().commonTags("application", appName);
    }
}
