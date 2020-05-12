package com.eina.chat.backendapi.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // This is not for websocket authorization, and this should most likely not be altered.
        http.httpBasic().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/ws").permitAll()
                .anyRequest().permitAll();
        // Actuator endpoints TODO: change it
                /*
                // Allow access to the home page (/) all other
                .authorizeRequests().antMatchers("/").permitAll()
                // Restrict access to the actuators endpoint to the ADMIN role.
                .antMatchers("/status").hasRole("ADMIN")
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                // Allow access to static resource only ADMIN role
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).hasRole("ADMIN")
                // All other requests need to be authenticated
                .anyRequest().denyAll();

                 */
    }
}
