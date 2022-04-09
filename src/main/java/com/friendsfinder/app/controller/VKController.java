package com.friendsfinder.app.controller;

import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.SearchParams;
import com.friendsfinder.app.service.graph.GraphServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/vk")
@RequiredArgsConstructor
public class VKController {
    private final GraphServiceImpl graphService;

    @GetMapping("/graph")
    public ArrayList<ArrayList<ArrayList<Node>>> getGraph () throws VKException {
        return graphService.build(new SearchParams(2, 3));
    }
}
