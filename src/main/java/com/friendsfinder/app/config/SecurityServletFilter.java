package com.friendsfinder.app.config;

import com.friendsfinder.app.service.Session.SessionServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class SecurityServletFilter extends HttpFilter {

    private final SessionServiceImpl sessionService;

    private final VKClient vkClient;

    private final Logger logger = Logger.getLogger(SecurityServletFilter.class.getName());

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        var session = request.getSession();

        logger.log(Level.INFO, "Обработка запроса — " + request.getMethod() + ": " + request.getRequestURI());

        var token = sessionService.getToken(session);
        var isValidToken = sessionService.isValidToken(token);

        if(token == null || !isValidToken){
            logger.log(Level.SEVERE, "Not authorized");
            response.setStatus(401);
            return;
        }

        vkClient.setAccessToken(token);

        chain.doFilter(request, response);
    }
}
