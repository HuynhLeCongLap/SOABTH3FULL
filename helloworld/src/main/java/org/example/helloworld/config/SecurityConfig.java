package org.example.helloworld.config;

import org.example.helloworld.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public FilterRegistrationBean<JwtFilter> filterRegistrationBean() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);

        // Áp dụng filter cho tất cả request
        registrationBean.addUrlPatterns("/*");

        // Nhưng cho phép một số URL public (login + logout)
        registrationBean.setName("JwtFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
