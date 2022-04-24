package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.controller.dto.request.SearchPersonRequest;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.UserGraph;
import com.friendsfinder.app.model.WordForms;
import com.friendsfinder.app.model.entity.Graph;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.model.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements IGraphService {
    private final VKClientImpl vkClient;

    private final MorphologyServiceImpl morphologyService;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;
    private final Set<Integer> uniqueIds = new HashSet<>();

    @Setter
    private int depth;

    @Setter
    private int width;

    public ArrayList<ArrayList<Node>> getRoot () throws VKException {
        var tuple = new ArrayList<Node>();
        var level = new ArrayList<ArrayList<Node>>();
        var ids = new ArrayList<Integer>();
        var userId = (int) sessionService.getUserId();

        uniqueIds.add(userId);
        ids.add(userId);

        var users = vkClient.getUserData(ids);
        var currentUser = users.get(0);
        var wall = vkClient.getUserWall(userId);
        var groups = vkClient.getUserGroups(userId);
        var node = new Node(0, userId);

        currentUser.setWall(wall);
        currentUser.setGroups(groups);
        node.setUser(currentUser);
        tuple.add(node);
        level.add(tuple);

        return level;
    }

    public UserGraph build (SearchPersonRequest params) throws VKException {
        var graph = new ArrayList<ArrayList<ArrayList<Node>>>();

        graph.add(getRoot());

        this.setWidth(params.getWidth());
        this.setDepth(params.getDepth() - 1);

        for(int i = 1; i <= depth; i++){
            var listIds =  getParentIds(graph.get(i - 1));

            var level = getLevel(listIds);
            graph.add(level);
        }

        var userGraph = new UserGraph(graph, width, depth);

        saveToDb(userGraph);

        uniqueIds.clear();

        return userGraph;
    }

    private void saveToDb (UserGraph graph){
        var userGraph = new Graph();

        userGraph.setUserId(sessionService.getUserId());
        userGraph.setNodes(graph.getGraph());
        userGraph.setUniqueIds(uniqueIds.stream().toList());

        graphRepository.save(userGraph);
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

    private void refreshWordForms (Node node){
        var wall = node.getUser().getWall();
        var groups  = node.getUser().getGroups();

        var wallWords = new ArrayList<String>();

        wall.forEach(post -> {
            var formattedPost = morphologyService.formatSentence(post);
            var words = morphologyService.splitSentence(formattedPost);

            wallWords.addAll(words);
        });

        var wordForms = new WordForms();

        wordForms.setWall(wallWords);

        node.setWordForms(wordForms);
    }

    private Node createNode(User user) {
        var userId = user.getId();

        var wall = vkClient.getUserWall(userId);
        var groups = vkClient.getUserGroups(userId);

        user.setWall(wall);
        user.setGroups(groups);

        var node = new Node(depth, userId);

        node.setUser(user);

        return node;
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
                        var node = createNode(user);

                        refreshWordForms(node);

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
                .filter(friendId -> uniqueIds.stream().filter(friendId::equals).findFirst().orElse(-1) == -1)
                .toList();

        var result = filteredIds.stream().limit(count).toList();

        uniqueIds.addAll(result);

        return new ArrayList<>(result);
    }
}
