package com.friendsfinder.app.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModificationData {
    /**
     * Старая ширина графа
     */
    private final Integer oldWidth;

    /**
     * Старая ширина графа
     */
    private final Integer oldDepth;

    /**
     * Новая ширина графа
     */
    private final Integer newWidth;

    /**
     * Новая ширина графа
     */
    private final Integer newDepth;

    /**
     * Старый граф
     */
    private final ArrayList<ArrayList<ArrayList<Node>>> oldGraph;
}
