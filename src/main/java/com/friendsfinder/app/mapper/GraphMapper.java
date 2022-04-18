package com.friendsfinder.app.mapper;

import com.friendsfinder.app.controller.dto.response.NodeDto;
import com.friendsfinder.app.model.Graph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GraphMapper {
    private Graph graph;

    public NodeDto toNodeResponse (Graph graph){
        this.graph = graph;

        var response = traverse();

        this.graph = null;

        return response;
    }

    private NodeDto traverse (){
        var root = NodeDto.getNodeDto(graph.getGraph().get(0).get(0).get(0));

        var children = getChildren(1, 0, 0);

        root.setChildren(children);

        return root;
    }

    public List<NodeDto> getChildren (int graphDepth, int levelIndex, int childIndex) {
        var width = graph.getWidth();
        var depth = graph.getDepth();

        var pos = width * levelIndex + childIndex;
        var tuple = graph.getGraph().get(graphDepth).get(pos);

        if(tuple == null)
            return null;

        var nodes = new ArrayList<NodeDto>();

        for(var i = 0; i < tuple.size(); i++){
            var node = tuple.get(i);

            if(node == null)
                continue;

            var nodeDto = NodeDto.getNodeDto(node);
            var children = graphDepth < depth ? getChildren(graphDepth + 1, pos, i) : null;

            nodeDto.setChildren(children);

            nodes.add(nodeDto);
        }

        return nodes;
    }

}
