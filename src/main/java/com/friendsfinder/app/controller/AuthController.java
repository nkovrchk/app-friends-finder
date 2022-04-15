package com.friendsfinder.app.controller;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.enums.SessionAttribute;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SessionServiceImpl sessionService;

    private final VKClientImpl vkClient;

    private final JsonUtils jsonUtils;

    private final String clientUrl = "http://localhost:8080";

    private void redirect (HttpServletResponse response, String url) {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", url);
        response.setHeader("Connection", "close");
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response, HttpSession session){
        var token = sessionService.getToken(session);
        var isValidToken = sessionService.isValidToken(token);

        if(!isValidToken){
            redirect(response, vkClient.getAuthUrl());
            return;
        }

        vkClient.setAccessToken(token);
        redirect(response,clientUrl + "/vk");
    }

    @GetMapping("/token")
    public void getToken(@RequestParam(name = "code") String code, HttpServletResponse response, HttpSession session) throws BusinessException, JsonException {
        var token = vkClient.retrieveToken(code);
        var serializedToken = jsonUtils.stringify(token);

        session.setAttribute(SessionAttribute.TOKEN.toString(), serializedToken);

        vkClient.setAccessToken(token);

        redirect(response, clientUrl + "/vk");
    }

    @PostMapping("/check-token")
    public ResponseEntity<Object> checkToken (HttpSession session) {
        var token = sessionService.getToken(session);
        var isValid = sessionService.isValidToken(token);

        return new ResponseEntity<>(isValid ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

}