package com.friendsfinder.app.service.match;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.model.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements IMatchService {

    @Value("${match.k-about}")
    private Double kAbout;

    @Value("${match.k-posts}")
    private Double kPosts;

    @Value("${match.k-groups}")
    private Double kGroups;
    private ArrayList<ArrayList<String>> keyWords;

    public MatchData getMatchData(Double kDepth, Node node, ArrayList<ArrayList<String>> keyWords) {
        this.keyWords = keyWords;

        var forms = node.getWordForms();
        var words = List.copyOf(keyWords);

        var infoList = new ArrayList<>(words);
        var infoSection = getMatchSection(kAbout, forms.getInfo(), infoList);

        var wallList = infoList.stream().filter(word -> !infoSection.getW().contains(word.get(0))).collect(Collectors.toCollection(ArrayList::new));
        var wallSection = getMatchSection(kPosts, forms.getWall(), wallList);

        var groupList = wallList.stream().filter(word -> !wallSection.getW().contains(word.get(0))).collect(Collectors.toCollection(ArrayList::new));
        var groupSection = getMatchSection(kGroups, forms.getGroups(), groupList);

        var matchData = new MatchData();
        var matchTotal = kDepth * (infoSection.getR() + wallSection.getR() + groupSection.getR());

        matchData.setUserId(node.getUserId());
        matchData.setInfo(infoSection);
        matchData.setWall(wallSection);
        matchData.setGroups(groupSection);
        matchData.setTotal(matchTotal);

        this.keyWords = null;

        return matchData;
    }

    private MatchData.MatchSection getMatchSection(Double kSection, List<String> forms, ArrayList<ArrayList<String>> words) {
        var section = new MatchData.MatchSection();
        var filteredWords = words.stream().filter(word -> forms.contains(word.get(1))).toList();
        var total = filteredWords.stream().map(word -> 1.0 / (1 + keyWords.indexOf(word))).reduce(0.0, Double::sum);

        section.setW(filteredWords.stream().map(word -> word.get(0)).collect(Collectors.toCollection(ArrayList::new)));
        section.setR(kSection * total);

        return section;
    }
}
