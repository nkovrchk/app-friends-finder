package com.friendsfinder.app.service.extension;

import com.friendsfinder.app.model.GraphData;
import com.friendsfinder.app.model.ModificationData;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.node.NodeServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtensionServiceImpl implements IExtensionService {
    private final GraphRepository graphRepository;

    private final VKClientImpl vkClient;

    private final NodeServiceImpl nodeService;

    @Getter
    private ModificationData modificationData;



    @Getter
    private GraphData graphData;

    public void extend (GraphData graphData, ModificationData modificationData){
        this.graphData = graphData;
        this.modificationData = modificationData;

    }

    private ArrayList<ArrayList<Node>> extendFirstLevel (){
        var oldGraph = modificationData.getOldGraph();
        var oldWidth = modificationData.getOldWidth();
        var newWidth = modificationData.getNewWidth();
        var oldDepth = modificationData.getOldDepth();

        var rootId = oldGraph.get(0).get(0).get(0).getUserId();

        var rightNodes = getNodes(rootId, oldWidth - newWidth, 1);

        var nodes = oldGraph.get(1).get(0);

        nodes.addAll(rightNodes);

        var newLevel = new ArrayList<ArrayList<Node>>();

        newLevel.add(nodes);

        return newLevel;
    }

    private ArrayList<Node> getNodes (Integer parentId, Integer count, Integer depth) {
        var response = vkClient.getFriendsIds(parentId);
        var uniqueIds = getGraphData().getUniqueIds();

        var childrenIds = response
                .stream()
                .filter(friendId -> uniqueIds.stream().filter(friendId::equals).findFirst().orElse(-1) == -1)
                .limit(count)
                .toList();

        graphData.addAllIds(childrenIds);

        var users = vkClient.getUserData(childrenIds);

        var nodes = users.stream()
                .map(user -> nodeService.createNode(user, parentId, depth))
                .collect(Collectors.toCollection(ArrayList::new));

        var difference = count - nodes.size();

        for(var i = 0; i < difference; i++) nodes.add(null);

        return nodes;
    }
}
