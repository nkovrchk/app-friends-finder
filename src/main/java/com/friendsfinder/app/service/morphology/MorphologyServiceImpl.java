package com.friendsfinder.app.service.morphology;

import org.springframework.stereotype.Service;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdkFactory;

import java.util.List;

@Service
public class MorphologyServiceImpl implements IMorphologyService {

    private final JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
    private final String regex = "[,.!;:?\\s]+";

    public String formatSentence(String sentence){
        return sentence.replaceAll(regex, "");
    }

    public List<String> splitSentence (String sentence){
        return List.of(sentence.split(" "));
    }

}
