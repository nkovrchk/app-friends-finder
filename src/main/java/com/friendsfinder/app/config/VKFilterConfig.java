package com.friendsfinder.app.config;

import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VKFilterConfig {

    private final SessionServiceImpl sessionService;

    private final VKClientImpl vkClient;

    @Bean
    public FilterRegistrationBean<SecurityServletFilter> filterRegistrationBean() {
        FilterRegistrationBean<SecurityServletFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SecurityServletFilter(sessionService, vkClient));
        registrationBean.addUrlPatterns("/api/v1/vk/*");

        return registrationBean;
    }
}
