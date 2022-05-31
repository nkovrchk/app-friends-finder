package com.friendsfinder.app.utils;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.service.morphology.MorphologyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MappingUtils {

    @Value("${match.limit}")
    private Integer matchLimit;

    private final MorphologyServiceImpl morphologyService;

    public ArrayList<ArrayList<String>> mapKeyWords(ArrayList<String> keyWords) {
        return keyWords.stream().map(word -> {
            var mappedWord = new ArrayList<String>();

            mappedWord.add(word);
            mappedWord.add(morphologyService.getWordForm(word));

            return mappedWord;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public HashMap<Integer, MatchData> mapMatchData(ArrayList<MatchData> matchData) {
        var matchMap = new HashMap<Integer, MatchData>();

        matchData.sort(Comparator.comparing(MatchData::getTotal).reversed());
        matchData.stream().filter(data -> data.getTotal() > 0).limit(matchLimit).forEach(match -> matchMap.put(match.getUserId(), match));

        return matchMap;
    }
}
