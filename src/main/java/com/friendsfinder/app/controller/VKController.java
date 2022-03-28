package com.friendsfinder.app.controller;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.SearchParams;
import com.friendsfinder.app.service.Graph.GraphServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vk")
@RequiredArgsConstructor
public class VKController {
    private final GraphServiceImpl graphService;

    @GetMapping("/graph")
    public void getGraph () throws JsonException {
        graphService.build(new SearchParams(1, 3));
    }
}
