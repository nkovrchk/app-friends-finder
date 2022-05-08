package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.controller.dto.request.SearchRequest;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.*;
import com.friendsfinder.app.model.entity.Graph;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.utils.MapperUtils;
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

    private final MapperUtils mapperUtils;

    @Setter
    private int depth;

    @Setter
    private int width;

    private final Set<Integer> uniqueIds = new HashSet<>();

    private final ArrayList<MatchData> matches = new ArrayList<>();

    @Setter
    private ArrayList<ArrayList<String>> keyWords = new ArrayList<>();

    public UserGraph build(SearchRequest params) throws VKException {
        var graph = new ArrayList<ArrayList<ArrayList<Node>>>();

        this.setKeyWords(mapperUtils.mapKeyWords(params.getKeyWords()));

        graph.add(getRoot());

        this.setWidth(params.getWidth());
        this.setDepth(params.getDepth() - 1);

        for (int i = 1; i <= depth; i++) {
            var listIds = getParentIds(graph.get(i - 1));

            var level = getLevel(listIds, i);
            graph.add(level);
        }

        var userGraph = new UserGraph();

        userGraph.setGraph(graph);
        userGraph.setWidth(width);
        userGraph.setDepth(depth);
        userGraph.setMatchData(mapperUtils.mapMatchData(matches, 5));
        userGraph.setUniqueIds(uniqueIds);

        clearData();
        saveToDb(userGraph);

        return userGraph;
    }

    private void clearData() {
        uniqueIds.clear();
        keyWords.clear();
        matches.clear();

        this.setWidth(0);
        this.setDepth(0);
    }

    private Map<Integer, MatchData> mapMatchData(List<MatchData> matches) {
        var matchMap = new HashMap<Integer, MatchData>();

        matches.sort(Comparator.comparing(MatchData::getTotal).reversed());
        matches.stream().limit(5).forEach(match -> matchMap.put(match.getUserId(), match));

        return matchMap;
    }

    private ArrayList<ArrayList<Node>> getRoot() throws VKException {
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
        var graph = new Graph();

        graph.setUserId(sessionService.getUserId());
        graph.setNodes(userGraph.getGraph());
        graph.setUniqueIds(userGraph.getUniqueIds().stream().toList());

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
        if (parentId == null)
            return null;

        var tuple = new ArrayList<Node>();

        var childrenIds = getFriendsIds(parentId, width);

        if (childrenIds.size() == 0)
            return null;

        try {
            var users = vkClient.getUserData(childrenIds);

            var nodes = users.stream()
                    .map(user -> {
                        var node = createNode(user, depth);

                        refreshWordForms(node);

                        var match = getMatchData(1.0 / depth, node.getWordForms());

                        match.setUserId(user.getId());

                        matches.add(match);

                        return node;
                    })
                    .toList();

            var arrayNodes = new ArrayList<>(nodes);

            tuple.addAll(arrayNodes);
        } catch (VKException ex) {
            return null;
        }

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

    private MatchData getMatchData(Double kDepth, WordForms forms) {
        var words = List.copyOf(keyWords);

        var infoList = new ArrayList<>(words);
        var infoSection = getMatchSection(1.75, forms.getInfo(), infoList);

        var wallList = new ArrayList<>(infoList.stream().filter(word -> !infoSection.getW().contains(word.get(0))).toList());
        var wallSection = getMatchSection(1.5, forms.getWall(), wallList);

        var groupList = new ArrayList<>(wallList.stream().filter(word -> !wallSection.getW().contains(word.get(0))).toList());
        var groupSection = getMatchSection(1.25, forms.getGroups(), groupList);

        var matchData = new MatchData();
        var matchTotal = kDepth * (infoSection.getR() + wallSection.getR() + groupSection.getR());

        matchData.setInfo(infoSection);
        matchData.setWall(wallSection);
        matchData.setGroups(groupSection);
        matchData.setTotal(matchTotal);

        return matchData;
    }

    private MatchData.MatchSection getMatchSection(Double kSection, List<String> forms, ArrayList<ArrayList<String>> words) {
        var section = new MatchData.MatchSection();
        var filteredWords = words.stream().filter(word -> forms.contains(word.get(1))).toList();
        var total = filteredWords.stream().map(word -> 1.0 / (1 + keyWords.indexOf(word))).reduce(0.0, Double::sum);

        section.setW(new ArrayList<>(filteredWords.stream().map(word -> word.get(0)).toList()));
        section.setR(kSection * total);

        return section;
    }
}
