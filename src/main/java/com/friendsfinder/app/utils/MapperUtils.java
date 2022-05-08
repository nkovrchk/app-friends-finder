package com.friendsfinder.app.utils;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Component
public record MapperUtils(MorphologyServiceImpl morphologyService) {

    public ArrayList<ArrayList<String>> mapKeyWords(ArrayList<String> keyWords) {
        return new ArrayList<>(keyWords.stream().map(word -> {
            var mappedWord = new ArrayList<String>();

            mappedWord.add(word);
            mappedWord.add(morphologyService.getWordForm(word));

            return mappedWord;
        }).toList());
    }

    public HashMap<Integer, MatchData> mapMatchData(ArrayList<MatchData> matchData, Integer limit) {
        var matchMap = new HashMap<Integer, MatchData>();

        matchData.sort(Comparator.comparing(MatchData::getTotal).reversed());
        matchData.stream().filter(data -> data.getTotal() > 0).limit(limit).forEach(match -> matchMap.put(match.getUserId(), match));

        return matchMap;
    }
}
