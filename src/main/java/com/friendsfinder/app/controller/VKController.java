package com.friendsfinder.app.controller;

import com.friendsfinder.app.controller.dto.request.SearchRequest;
import com.friendsfinder.app.controller.dto.response.NodeDto;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.mapper.GraphMapper;
import com.friendsfinder.app.service.graph.GraphServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vk")
@RequiredArgsConstructor
public class VKController {
    private final GraphServiceImpl graphService;

    private final GraphMapper graphMapper;

    @PostMapping("/graph")
    public NodeDto getGraph (@RequestBody SearchRequest searchRequest) throws VKException {
        var graph = graphService.build(searchRequest);

        return graphMapper.toNodeResponse(graph);
    }
}
