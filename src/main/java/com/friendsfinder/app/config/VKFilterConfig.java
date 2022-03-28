package com.friendsfinder.app.config;

import com.friendsfinder.app.service.Session.SessionServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VKFilterConfig {

    private final SessionServiceImpl sessionService;

    private final VKClient vkClient;

    @Bean
    public FilterRegistrationBean<SecurityServletFilter> filterRegistrationBean() {
        FilterRegistrationBean<SecurityServletFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SecurityServletFilter(sessionService, vkClient));
        registrationBean.addUrlPatterns("/api/v1/vk/*");

        return registrationBean;
    }
}
