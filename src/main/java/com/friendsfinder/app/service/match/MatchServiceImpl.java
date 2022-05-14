package com.friendsfinder.app.service.match;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.WordForms;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchServiceImpl {
    private ArrayList<ArrayList<String>> keyWords;

    public MatchData getMatchData(Double kDepth, Node node, ArrayList<ArrayList<String>> keyWords) {
        this.keyWords = keyWords;

        var forms = node.getWordForms();
        var words = List.copyOf(keyWords);

        var infoList = new ArrayList<>(words);
        var infoSection = getMatchSection(1.75, forms.getInfo(), infoList);

        var wallList = new ArrayList<>(infoList.stream().filter(word -> !infoSection.getW().contains(word.get(0))).toList());
        var wallSection = getMatchSection(1.5, forms.getWall(), wallList);

        var groupList = new ArrayList<>(wallList.stream().filter(word -> !wallSection.getW().contains(word.get(0))).toList());
        var groupSection = getMatchSection(1.25, forms.getGroups(), groupList);

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

    public MatchData.MatchSection getMatchSection(Double kSection, List<String> forms, ArrayList<ArrayList<String>> words) {
        var section = new MatchData.MatchSection();
        var filteredWords = words.stream().filter(word -> forms.contains(word.get(1))).toList();
        var total = filteredWords.stream().map(word -> 1.0 / (1 + keyWords.indexOf(word))).reduce(0.0, Double::sum);

        section.setW(new ArrayList<>(filteredWords.stream().map(word -> word.get(0)).toList()));
        section.setR(kSection * total);

        return section;
    }

}
