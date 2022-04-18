package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.controller.dto.request.SearchPersonRequest;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.Graph;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
public class GraphServiceImpl implements IGraphService {
    private final VKClientImpl vkClient;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;

    private final Set<Integer> idsSet = new HashSet<>();

    private int depth;

    private int width;

    private ArrayList<ArrayList<ArrayList<Node>>> graph;

    public Graph build (SearchPersonRequest params) throws VKException {
        this.setWidth(params.getWidth());
        this.setDepth(params.getDepth() - 1);

        var userId = vkClient.getUserId();
        var ids = new ArrayList<Integer>();

        ids.add(userId);

        var usersData = vkClient.getUserData(ids);
        var node = new Node(0, userId);

        node.setUser(usersData.get(0));

        var graph = new ArrayList<ArrayList<ArrayList<Node>>>();
        var rootLevel = new ArrayList<ArrayList<Node>>();
        var root = new ArrayList<Node>();

        root.add(node);
        rootLevel.add(root);
        graph.add(rootLevel);

        for(int i = 1; i <= depth; i++){
            var listIds =  getParentIds(graph.get(i - 1));

            var level = getLevel(listIds);
            graph.add(level);
        }

        idsSet.clear();

        return new Graph(graph, width, depth);
    }

    private ArrayList<Integer> getParentIds (ArrayList<ArrayList<Node>> level){
        var parentIds = new ArrayList<Integer>();

        level.forEach(tuple -> {
            if(tuple == null)
                for(var i = 0; i < width; i++) parentIds.add(null);

            else
                parentIds.addAll(tuple.stream().map(node -> node != null ? node.getUserId() : null).toList());
        });

        return parentIds;
    }

    private ArrayList<Node> getChildrenNodes (Integer parentId) {
        if(parentId == null)
            return null;

        var tuple = new ArrayList<Node>();

        var childrenIds = getFriendsIds(parentId, width);

        if(childrenIds.size() == 0)
            return null;

        try{
            var users = vkClient.getUserData(childrenIds);

            var nodes = users.stream()
                    .map(user -> {
                        var userId = user.getId();

                        var wall = vkClient.getUserWall(userId);
                        var groups = vkClient.getUserGroups(userId);

                        user.setWall(wall);
                        user.setGroups(groups);

                        var node = new Node(depth, userId, parentId);

                        node.setUser(user);

                        return node;
                    })
                    .toList();

            var arrayNodes = new ArrayList<>(nodes);

            tuple.addAll(arrayNodes);
        }
        catch (VKException ex){
            return null;
        }

        var difference = width - tuple.size();

        for(var i = 0; i < difference; i++) tuple.add(null);

        return tuple;
    }

    private ArrayList<ArrayList<Node>> getLevel (ArrayList<Integer> ids) {
        var level = new ArrayList<ArrayList<Node>>();

        ids.forEach(parentId -> {
            var children = getChildrenNodes(parentId);

            level.add(children);
        });

        var difference = ids.size() - level.size();

        for(var i = 0; i < difference; i++) level.add(null);

        return level;
    }

    private ArrayList<Integer> getFriendsIds (int userId, int count) {
        var response = vkClient.getFriendsIds(userId);

        var filteredIds = response
                .stream()
                .filter(friendId -> idsSet.stream().filter(friendId::equals).findFirst().orElse(-1) == -1)
                .toList();

        var result = filteredIds.stream().limit(count).toList();

        idsSet.addAll(result);

        return new ArrayList<>(result);
    }
}
