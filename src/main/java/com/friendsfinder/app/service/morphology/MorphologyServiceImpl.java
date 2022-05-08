package com.friendsfinder.app.service.morphology;

import org.springframework.stereotype.Service;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdkFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class MorphologyServiceImpl implements IMorphologyService {

    private final JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();

    public String formatSentence(String sentence){
        return sentence
                .replaceAll("[,.!;:?()]+", "")
                .replaceAll("\\n+", " ")
                .replaceAll("\s(а|абы|аж|ан|благо|буде|будто|вроде|да|дабы|даже|едва|ежели|если|же|затем|зато|и|ибо|или|итак|кабы|как|когда|коли|коль|ли|либо|лишь|нежели|но|пока|покамест|покуда|поскольку|притом|причем|пускай|пусть|раз|разве|ровно|сиречь|словно|так|также|тоже|только|точно|хоть|хотя|чем|чисто|что|чтоб|чтобы|чуть|якобы|без|в|до|для|за|из|к|на|над|о|об|от|по|под|перед|при|про|с|у|через|из-за|из-под|не|ни|бы|бывало|уж|почти|хотя бы|неужели|дай|знай|давай|ну|дескать|мол|ведь|ну и|как будто|пожалуй|авось|просто|именно|чуть не|едва ли не|что ли)\s", " ")
                .replaceAll("\\s+", " ");
    }

    public List<String> splitSentence (String sentence){
        return List.of(sentence.split(" "));
    }

    public String getWordForm (String word){
        var forms = jMorfSdk.getStringInitialForm(word);

        return forms.size() > 0 ? forms.get(0) : word;
    }

    public List<String> getWordForms (List<String> words) {
        return words.stream().map(this::getWordForm).toList();
    }

    public List<String> processText (List<String> words) {
        var streams = words.stream().map(word -> {
            var formatted = formatSentence(word);
            var sentence = splitSentence(formatted);

            return getWordForms(sentence);
        });

        return streams.flatMap(List::stream).toList();
    }
}
