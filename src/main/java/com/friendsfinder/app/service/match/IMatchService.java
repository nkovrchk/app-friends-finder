package com.friendsfinder.app.service.match;

import com.friendsfinder.app.model.MatchData;
import com.friendsfinder.app.model.Node;

import java.util.ArrayList;

public interface IMatchService {
    /**
     * Рассчитывает весовой коэффициент для вершины
     * @param kDepth Коэффициент глубины
     * @param node Вершина графа
     * @param keyWords Ключевые слова
     * @return Рассчитанный коэффициент
     */
    MatchData getMatchData (Double kDepth, Node node, ArrayList<ArrayList<String>> keyWords);
}
