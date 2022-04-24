package com.friendsfinder.app.service.morphology;

import java.util.List;

public interface IMorphologyService {
    String formatSentence (String sentence);

    List<String> splitSentence (String sentence);
}
