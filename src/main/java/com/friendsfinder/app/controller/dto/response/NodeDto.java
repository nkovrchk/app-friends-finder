package com.friendsfinder.app.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.friendsfinder.app.model.Node;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeDto {
    private String name;

    private NodeDtoAttributes attributes;

    private List<NodeDto> children;
    @Data
    public static class NodeDtoAttributes {
        private String id;

        private boolean isMatch;

        private boolean isLinked;

        private String photo;

    }

    public static NodeDto getNodeDto (Node node) {
        var attributes = new NodeDto.NodeDtoAttributes();

        attributes.setId(Integer.toString(node.getUserId()));
        attributes.setPhoto(node.getUser().getPhoto());

        var nodeDto = new NodeDto();
        var name = String.format("%s %s", node.getUser().getFirstName(), node.getUser().getLastName()).trim();

        nodeDto.setName(name);
        nodeDto.setAttributes(attributes);

        return nodeDto;
    }



}
