package com.friendsfinder.app.service.node;

import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.User;
import com.friendsfinder.app.model.WordForms;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl {
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

        return node;
    }

    public void getWordForms (Node node){
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
}
