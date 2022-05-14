package com.friendsfinder.app.service.modification;

import com.friendsfinder.app.controller.dto.response.NodeDto;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.UserGraph;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.graph.GraphServiceImpl;
import com.friendsfinder.app.service.match.MatchServiceImpl;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import com.friendsfinder.app.service.node.NodeServiceImpl;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModificationServiceImpl implements IModificationService {
    private final VKClientImpl vkClient;

    private final MorphologyServiceImpl morphologyService;

    private final GraphRepository graphRepository;

    private final NodeServiceImpl nodeService;

    private final MatchServiceImpl matchService;

    private final SessionServiceImpl sessionService;

    private final MapperUtils mapperUtils;

    private ArrayList<ArrayList<ArrayList<Node>>> oldGraph;

    private UserGraph newGraph;

    private final ArrayList<MatchData> matches = new ArrayList<>();

    private Integer oldW;

    private Integer oldN;

    private Integer newW;

    private Integer newN;

    ArrayList<ArrayList<String>> keyWords;

    ArrayList<Integer> uniqueIds;


    public UserGraph proceed(ArrayList<ArrayList<ArrayList<Node>>> oldGraph, Integer oldW, Integer oldN, Integer newW, Integer newN, ArrayList<ArrayList<String>> keyWords){
        this.oldW = oldW;
        this.oldN = oldN;
        this.newW = newW;
        this.newN = newN;
        this.keyWords = keyWords;

        if(newN >= oldN || newW >= oldW)
            return processOldGraph(oldGraph);

        this.oldGraph = oldGraph;

        var newGraph = initializeCopy();

        for(var i = 1; i < oldGraph.size(); i++){
            ArrayList<ArrayList<Node>> newLevel = null;

            if(i == 1)
                newLevel = spliceFirstLevel();

            if(i == 2)
                newLevel = spliceSecondLevel();

            if(i == 3)
                newLevel = spliceThirdLevel();

            if(i == 4)
                newLevel = spliceForthLevel();

            newGraph.add(newLevel);
        }
        var graph = new UserGraph();

        graph.setGraph(newGraph);
        graph.setWidth(newW);
        graph.setDepth(newN);

        this.newGraph = graph;

        processMatching();

        graph.setMatchData(mapperUtils.mapMatchData(matches, 5));

        matches.clear();

        return graph;
    }

    private void growGraph () {

    }

    private ArrayList<ArrayList<ArrayList<Node>>> growWidth () {
        var newGraph = initializeCopy();

        for(var i = 1; i < oldGraph.size(); i++) {
            ArrayList<ArrayList<Node>> newLevel = null;

            if(i == 1)
                newLevel = growFirstLevel();

            newGraph.add(newLevel);
        }

        return newGraph;
    }

    private ArrayList<ArrayList<ArrayList<Node>>> initializeCopy (){
        var copy = new ArrayList<ArrayList<ArrayList<Node>>>();
        var rootLevel = new ArrayList<ArrayList<Node>>();

        rootLevel.add(oldGraph.get(0).get(0));
        copy.add(rootLevel);

        return copy;
    }

    public ArrayList<ArrayList<ArrayList<Node>>> spliceGraph (ArrayList<ArrayList<ArrayList<Node>>> graph, Integer count){
        return graph.stream().limit(count).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<ArrayList<Node>> spliceLevel(ArrayList<ArrayList<Node>> level, Integer depth) {
        // Разница между новой и старой шириной
        var diff = oldN - oldW;
        // Кол-во массивов нод справа
        var tuplesToDelete = (int) Math.pow(oldW, depth - 2) * diff;
        var oldSize = level.size();

        // После удаления массивов нод справа
        var slicedLevel = level.stream().limit(oldSize - tuplesToDelete).toList();
        var blocks = slicedLevel.size() / oldW;
        var blockTuples = new ArrayList<ArrayList<Node>>();

        for(var i = 0; i < blocks; i++){
            var subList = new ArrayList<>(slicedLevel.subList(i * oldW, (i + 1) * oldW - diff));

            blockTuples.addAll(subList);
        }

        var newLevel = blockTuples.stream().map(tuple -> {
            if(tuple == null) return null;

            return tuple.stream().limit(newW).collect(Collectors.toCollection(ArrayList::new));
        });

        return newLevel.collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ArrayList<Node>> limitNodes (ArrayList<ArrayList<Node>> nodes, Integer limit){
        return nodes.stream()
                .limit(limit)
                .map(tuple -> tuple != null ? tuple.stream().limit(limit).collect(Collectors.toCollection(ArrayList::new)) : null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ArrayList<Node>> spliceFirstLevel (){
        var tuples = oldGraph.get(1).get(0);
        var newLevel = new ArrayList<ArrayList<Node>>();

        newLevel.add(tuples.stream().limit(newW).collect(Collectors.toCollection(ArrayList::new)));

        return newLevel;
    }

    private ArrayList<ArrayList<Node>> spliceSecondLevel () {
        var oldLevel = oldGraph.get(2);

        return limitNodes(oldLevel, newW);
    }

    private ArrayList<ArrayList<Node>> spliceThirdLevel (){
        var level = oldGraph.get(3);

        var blocks = new ArrayList<ArrayList<ArrayList<Node>>>();

        for (var i = 0; i < newW; i++){
            var tuples = new ArrayList<>(level.subList(i * oldW, (i + 1) * oldW));
            blocks.add(tuples);
        }

        return blocks.stream()
                .map(block -> limitNodes(block, newW))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ArrayList<Node>> spliceForthLevel (){
        var level = oldGraph.get(4);

        var step = (int) Math.pow(oldW, 2);

        var newLevel = new ArrayList<ArrayList<Node>>();

        for (var i = 0; i < newW; i++){
            var tuples = new ArrayList<>(level.subList(i * step, (i + 1) * step));
            var nodes = extractNodesFromBlock(tuples, oldW - newW);

            newLevel.addAll(nodes);
        }

        return newLevel;
    }

    private ArrayList<ArrayList<Node>> extractNodesFromBlock (ArrayList<ArrayList<Node>> block, Integer diff){
        var limit = block.size() - ((long) diff * oldW);
        var limitedBlock = block.stream().limit(limit).collect(Collectors.toCollection(ArrayList::new));

        var extractedNodes = new ArrayList<ArrayList<ArrayList<Node>>>();

        for(var i = 0; i < newW; i++){
            var tuples = new ArrayList<>(limitedBlock.subList(i * oldW, i * oldW + newW));

            extractedNodes.add(tuples);
        }

        var result = extractedNodes.stream()
                .map(nodes -> limitNodes(nodes, newW))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));


        return result;
    }

    private void processMatching (){
        recursiveCalc(1, 0, 0);
    }

    private UserGraph processOldGraph (ArrayList<ArrayList<ArrayList<Node>>> oldGraph){
        var graph = new UserGraph();

        graph.setGraph(oldGraph);
        graph.setWidth(newW);
        graph.setDepth(newN);

        this.newGraph = graph;

        processMatching();

        graph.setMatchData(mapperUtils.mapMatchData(matches, 5));

        return graph;
    }

    private void recursiveCalc (int graphDepth, int levelIndex, int childIndex) {
        var width = newW;
        var depth = newN;

        var pos = width * levelIndex + childIndex;
        var tuple = newGraph.getGraph().get(graphDepth).get(pos);

        if(tuple == null)
            return;

        for(var i = 0; i < tuple.size(); i++){
            var node = tuple.get(i);

            if(node == null)
                continue;

            var match = matchService.getMatchData(1.0 / graphDepth, node, keyWords);
            match.setUserId(node.getUserId());

            matches.add(match);

            if(graphDepth < depth)
                recursiveCalc(graphDepth + 1, pos, i);
        }
    }

    private ArrayList<ArrayList<Node>> growFirstLevel (){
        var rootId = oldGraph.get(0).get(0).get(0).getUserId();

        var rightNodes = getNodes(rootId, oldW - newW, 1);

        var nodes = oldGraph.get(1).get(0);

        nodes.addAll(rightNodes);

        var newLevel = new ArrayList<ArrayList<Node>>();

        newLevel.add(nodes);

        return newLevel;
    }

    private void growSecondLevel (){
        var prevNodes = oldGraph.get(1).get(0);
        var oldLevel = oldGraph.get(2);
        var newLevel = new ArrayList<ArrayList<Node>>();
    }

    private ArrayList<Node> getNodes (Integer parentId, Integer count, Integer depth) {
        var response = vkClient.getFriendsIds(parentId);

        var childrenIds = response
                .stream()
                .filter(friendId -> uniqueIds.stream().filter(friendId::equals).findFirst().orElse(-1) == -1)
                .limit(count)
                .toList();

        uniqueIds.addAll(childrenIds);

        try {
            var users = vkClient.getUserData(childrenIds);

            var nodes = users.stream()
                    .map(user -> {
                        var node = nodeService.createNode(user, parentId, depth);

                        nodeService.getWordForms(node);

                        var match = matchService.getMatchData(1.0 / depth, node, keyWords);

                        matches.add(match);

                        return node;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            var difference = count - nodes.size();

            for(var i = 0; i < difference; i++) nodes.add(null);

            return nodes;
        } catch (VKException ex) {
            return null;
        }
    }
}
