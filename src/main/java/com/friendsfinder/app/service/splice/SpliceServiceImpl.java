package com.friendsfinder.app.service.splice;

import com.friendsfinder.app.model.ModificationData;
import com.friendsfinder.app.model.Node;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpliceServiceImpl implements ISpliceService {
    @Getter
    private ModificationData modificationData;

    public ArrayList<ArrayList<ArrayList<Node>>> splice(ModificationData modificationData) {
        this.modificationData = modificationData;

        var oldGraph = modificationData.getOldGraph();
        var newGraph = new ArrayList<>(List.of(oldGraph.get(0)));

        for (var i = 1; i < oldGraph.size(); i++) {
            ArrayList<ArrayList<Node>> newLevel = switch (i) {
                case 1 -> spliceFirstLevel();
                case 2 -> spliceSecondLevel();
                case 3 -> spliceThirdLevel();
                case 4 -> spliceForthLevel();
                default -> null;
            };

            newGraph.add(newLevel);
        }

        this.modificationData = null;

        return newGraph;
    }

    private ArrayList<ArrayList<Node>> spliceFirstLevel() {
        var nodes = modificationData.getOldGraph().get(1).get(0);
        var newLevel = new ArrayList<ArrayList<Node>>();

        newLevel.add(nodes.stream().limit(modificationData.getNewWidth()).collect(Collectors.toCollection(ArrayList::new)));

        return newLevel;
    }

    private ArrayList<ArrayList<Node>> spliceSecondLevel() {
        var oldLevel = modificationData.getOldGraph().get(2);

        return limitNodes(oldLevel, modificationData.getNewWidth());
    }

    private ArrayList<ArrayList<Node>> spliceThirdLevel() {
        var oldWidth = modificationData.getOldWidth();
        var newWidth = modificationData.getNewWidth();
        var level = modificationData.getOldGraph().get(3);

        var blocks = new ArrayList<ArrayList<ArrayList<Node>>>();

        for (var i = 0; i < newWidth; i++) {
            var tuples = new ArrayList<>(level.subList(i * oldWidth, (i + 1) * oldWidth));
            blocks.add(tuples);
        }

        return blocks.stream()
                .map(block -> limitNodes(block, newWidth))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ArrayList<Node>> spliceForthLevel() {
        var newLevel = new ArrayList<ArrayList<Node>>();
        var level = modificationData.getOldGraph().get(4);

        var newWidth = modificationData.getNewWidth();
        var oldWidth = modificationData.getOldWidth();

        var step = (int) Math.pow(oldWidth, 2);

        for (var i = 0; i < newWidth; i++) {
            var tuples = new ArrayList<>(level.subList(i * step, (i + 1) * step));
            var nodes = extractNodes(tuples, oldWidth - newWidth);

            newLevel.addAll(nodes);
        }

        return newLevel;
    }

    private ArrayList<ArrayList<Node>> limitNodes(ArrayList<ArrayList<Node>> nodes, Integer limit) {
        return nodes.stream()
                .limit(limit)
                .map(tuple -> tuple != null ? tuple.stream().limit(limit).collect(Collectors.toCollection(ArrayList::new)) : null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<ArrayList<Node>> extractNodes(ArrayList<ArrayList<Node>> block, Integer diff) {
        var extractedNodes = new ArrayList<ArrayList<ArrayList<Node>>>();
        var newWidth = modificationData.getNewWidth();
        var oldWidth = modificationData.getOldWidth();

        var limit = block.size() - ((long) diff * oldWidth);
        var limitedBlock = block.stream().limit(limit).collect(Collectors.toCollection(ArrayList::new));

        for (var i = 0; i < newWidth; i++) {
            var tuples = new ArrayList<>(limitedBlock.subList(i * oldWidth, i * oldWidth + newWidth));

            extractedNodes.add(tuples);
        }

        return extractedNodes.stream()
                .map(nodes -> limitNodes(nodes, newWidth))
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
