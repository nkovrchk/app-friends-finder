package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.controller.dto.request.SearchRequest;
import com.friendsfinder.app.model.*;
import com.friendsfinder.app.model.entity.Graph;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.match.MatchServiceImpl;
import com.friendsfinder.app.service.modification.ModificationServiceImpl;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements IGraphService {
    private final VKClientImpl vkClient;

    private final MorphologyServiceImpl morphologyService;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;

    private final MappingUtils mappingUtils;

    private final ModificationServiceImpl modificationService;

    private final MatchServiceImpl matchService;

    private final Logger logger = Logger.getLogger(GraphServiceImpl.class.getName());

    @Setter
    private int depth;

    @Setter
    private int width;

    private final Set<Integer> uniqueIds = new HashSet<>();

    private final ArrayList<MatchData> matches = new ArrayList<>();

    @Setter
    private ArrayList<ArrayList<String>> keyWords = new ArrayList<>();

    public UserGraph getGraph(SearchRequest params) {
        UserGraph userGraph;

        var graph = graphRepository.findById(sessionService.getUserId());

        if (graph.isEmpty())
            userGraph = build(params);
        else {
            this.setKeyWords(mappingUtils.mapKeyWords(params.getKeyWords()));
            this.setWidth(params.getWidth());
            this.setDepth(params.getDepth());

            var dbGraph = graph.get();

            userGraph = dbGraph.getDepth() < this.depth || dbGraph.getWidth() < this.width ?
                    build(params) : modificationService.proceed(dbGraph.getNodes(), dbGraph.getWidth(), dbGraph.getDepth(), width, depth, keyWords);
        }

        return userGraph;
    }

    public UserGraph build(SearchRequest params) {
        var startDate = Instant.now();
        var graph = new ArrayList<ArrayList<ArrayList<Node>>>();

        this.setKeyWords(mappingUtils.mapKeyWords(params.getKeyWords()));

        graph.add(getRoot());

        this.setWidth(params.getWidth());
        this.setDepth(params.getDepth());

        for (int i = 1; i <= depth; i++) {
            var listIds = getParentIds(graph.get(i - 1));

            var level = getLevel(listIds, i);
            graph.add(level);
        }

        var endDate = Instant.now();

        logger.log(Level.INFO, String.format("???????? ???????????????? ???? %s s.", Duration.between(startDate, endDate).toMillis()));

        var userGraph = new UserGraph();

        userGraph.setGraph(graph);
        userGraph.setWidth(width);
        userGraph.setDepth(depth);
        userGraph.setMatchData(mappingUtils.mapMatchData(matches));
        userGraph.setUniqueIds(uniqueIds);

        saveToDb(userGraph);
        clearData();

        return userGraph;
    }

    private void clearData() {
        uniqueIds.clear();
        keyWords.clear();
        matches.clear();

        this.setWidth(0);
        this.setDepth(0);
    }

    private ArrayList<ArrayList<Node>> getRoot() {
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

    private void saveToDb(UserGraph userGraph) {
        Graph graph;

        var userId = sessionService.getUserId();
        var nodes = userGraph.getGraph();
        var uniqueIds = userGraph.getUniqueIds().stream().toList();
        var width = userGraph.getWidth();
        var depth = userGraph.getDepth();

        var existingGraph = graphRepository.findById(userId);

        if (existingGraph.isEmpty()) {
            graph = new Graph();

            graph.setUserId(userId);
        } else {
            graph = existingGraph.get();
        }

        graph.setNodes(nodes);
        graph.setUniqueIds(uniqueIds);
        graph.setWidth(width);
        graph.setDepth(depth);

        graphRepository.save(graph);
    }

    private ArrayList<Integer> getParentIds(ArrayList<ArrayList<Node>> level) {
        var parentIds = new ArrayList<Integer>();

        level.forEach(tuple -> {
            if (tuple == null)
                for (var i = 0; i < width; i++) parentIds.add(null);

            else
                parentIds.addAll(tuple.stream().map(node -> node != null ? node.getUserId() : null).toList());
        });

        return parentIds;
    }

    private void refreshWordForms(Node node) {
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

    private Node createNode(User user, int depth) {
        var userId = user.getId();

        var wall = vkClient.getUserWall(userId);
        var groups = vkClient.getUserGroups(userId);

        user.setWall(wall);
        user.setGroups(groups);

        var node = new Node(depth, userId);

        node.setUser(user);

        return node;
    }

    private ArrayList<Node> getChildrenNodes(Integer parentId, Integer depth) {
        if (parentId == null) return null;

        var childrenIds = getFriendsIds(parentId, width);

        if (childrenIds.size() == 0) return null;

        var users = vkClient.getUserData(childrenIds);

        if (users.size() == 0) return null;

        var nodes = users.stream()
                .map(user -> {
                    var node = createNode(user, depth);

                    node.setParentId(parentId);
                    refreshWordForms(node);

                    var match = matchService.getMatchData(1.0 / depth, node, keyWords);

                    match.setUserId(user.getId());
                    matches.add(match);

                    return node;
                })
                .toList();

        var tuple = new ArrayList<>(new ArrayList<>(nodes));

        var difference = width - tuple.size();

        for (var i = 0; i < difference; i++) tuple.add(null);

        return tuple;
    }

    private ArrayList<ArrayList<Node>> getLevel(ArrayList<Integer> ids, Integer depth) {
        var level = new ArrayList<ArrayList<Node>>();

        ids.forEach(parentId -> {
            var children = getChildrenNodes(parentId, depth);

            level.add(children);
        });

        var difference = ids.size() - level.size();

        for (var i = 0; i < difference; i++) level.add(null);

        return level;
    }

    private ArrayList<Integer> getFriendsIds(int userId, int count) {
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
