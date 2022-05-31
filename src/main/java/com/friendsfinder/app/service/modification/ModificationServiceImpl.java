package com.friendsfinder.app.service.modification;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.model.ModificationData;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.UserGraph;
import com.friendsfinder.app.service.match.MatchServiceImpl;
import com.friendsfinder.app.service.splice.SpliceServiceImpl;
import com.friendsfinder.app.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ModificationServiceImpl implements IModificationService {

    private final MatchServiceImpl matchService;

    private final SpliceServiceImpl spliceService;

    private final MappingUtils mappingUtils;

    private UserGraph newGraph;

    private final ArrayList<MatchData> matches = new ArrayList<>();


    private Integer newW;

    private Integer newN;

    ArrayList<ArrayList<String>> keyWords;


    public UserGraph proceed(ArrayList<ArrayList<ArrayList<Node>>> oldGraph, Integer oldW, Integer oldN, Integer newW, Integer newN, ArrayList<ArrayList<String>> keyWords){
        this.newW = newW;
        this.newN = newN;
        this.keyWords = keyWords;

        if(newN >= oldN || newW >= oldW)
            return processOldGraph(oldGraph);

        var newGraph = spliceService.splice(new ModificationData(oldW, oldN, newW, newN, oldGraph));

        var graph = new UserGraph();

        graph.setGraph(newGraph);
        graph.setWidth(newW);
        graph.setDepth(newN);

        this.newGraph = graph;

        processMatching();

        graph.setMatchData(mappingUtils.mapMatchData(matches));

        matches.clear();

        return graph;
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

        graph.setMatchData(mappingUtils.mapMatchData(matches));

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
}
