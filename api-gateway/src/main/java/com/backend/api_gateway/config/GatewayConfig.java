package com.backend.api_gateway.config;

import com.backend.api_gateway.filter.JwtGatewayFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class GatewayConfig {

    @Bean
    public FilterRegistrationBean<JwtGatewayFilter> jwtFilterRegistration(JwtGatewayFilter jwtGatewayFilter) {
        FilterRegistrationBean<JwtGatewayFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtGatewayFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
