package com.friendsfinder.app.service.node;

import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.User;
import com.friendsfinder.app.model.WordForms;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements INodeService {
    private final VKClientImpl vkClient;

    private final MorphologyServiceImpl morphologyService;

    public Node createNode(User user, Integer parentId, Integer depth) {
        var userId = user.getId();

        var wall = vkClient.getUserWall(userId);
        var groups = vkClient.getUserGroups(userId);

        user.setWall(wall);
        user.setGroups(groups);

        var node = new Node(depth, userId);

        node.setUser(user);
        node.setParentId(parentId);

        updateWordForms(node);

        return node;
    }

    public void updateWordForms(Node node){
        var wall = node.getUser().getWall();
        var groups = node.getUser().getGroups();

        var info = new ArrayList<String>();

        var about = node.getUser().getAbout();
        var city = node.getUser().getCity();
        var career = node.getUser().getCareer();
        var interests = node.getUser().getInterests();

        info.add(about);
        info.add(city);
        info.add(career);
        info.add(interests);

        var wallWords = morphologyService.processText(wall);
        var groupWords = morphologyService.processText(groups);
        var infoWords = morphologyService.processText(info);

        var wordForms = new WordForms();

        wordForms.setWall(wallWords);
        wordForms.setGroups(groupWords);
        wordForms.setInfo(infoWords);

        node.setWordForms(wordForms);
    }

    public void updateNodeData (Node node){
        var userId = node.getUserId();

        var wall = vkClient.getUserWall(userId);
        var groups = vkClient.getUserGroups(userId);
        var users = vkClient.getUserData(List.of(userId));

        if(users.size() > 0){
            var user = users.get(0);

            node.setUser(user);
        }

        node.getUser().setWall(wall);
        node.getUser().setGroups(groups);
    }
}
