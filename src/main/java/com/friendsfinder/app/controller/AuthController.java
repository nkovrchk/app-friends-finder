package com.friendsfinder.app.controller;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.SessionAttribute;
import com.friendsfinder.app.service.Session.SessionServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final VKClient vkClient;
    private final SessionServiceImpl sessionService;
    private final JsonUtils jsonUtils;

    @GetMapping("/token")
    public void processCode(@RequestParam(name = "code") String code, HttpServletResponse response, HttpSession session) throws BusinessException, IOException, JsonException {
        var token = vkClient.retrieveToken(code);
        var serializedToken = jsonUtils.stringify(token);

        session.setAttribute(SessionAttribute.TOKEN.toString(), serializedToken);

        response.sendRedirect("/api/v1/auth/login");
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response, HttpSession session) throws IOException{
        var token = sessionService.getToken(session);

        if(token == null){
            response.sendRedirect(vkClient.getAuthUrl());
            return;
        }

        vkClient.setAccessToken(token);

        response.sendRedirect("/vk");
    }

    @PostMapping("/check-token")
    public ResponseEntity<Object> checkToken (HttpSession session) {
        var token = sessionService.getToken(session);
        var isValid = sessionService.isValidToken(token);

        return new ResponseEntity<>(isValid ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }
}
