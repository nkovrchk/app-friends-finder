package com.friendsfinder.app.controller;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SessionServiceImpl sessionService;

    private final VKClientImpl vkClient;

    private final String clientUrl = "http://localhost:8080";

    private void redirect(HttpServletResponse response, String url) {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", url);
        response.setHeader("Connection", "close");
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) {
        var token = sessionService.getValidToken();

        if (token == null) {
            redirect(response, vkClient.getAuthUrl());
            return;
        }

        vkClient.setAccessToken(token.getAccessToken());
        redirect(response, clientUrl + "/form");
    }

    @GetMapping("/token")
    public void getToken(@RequestParam(name = "code") String code, HttpServletResponse response) throws BusinessException {
        var accessToken = vkClient.retrieveToken(code);

        sessionService.setToken(accessToken);

        redirect(response, clientUrl + "/form");
    }

    @PostMapping("/check-token")
    public ResponseEntity<Object> checkToken(HttpSession session) {
        var isValid = false;

        if (!session.isNew())
            isValid = sessionService.getValidToken() != null;

        return new ResponseEntity<>(isValid ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        var deleteCookie = new Cookie("SESSION", "");

        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);

        sessionService.logout();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
