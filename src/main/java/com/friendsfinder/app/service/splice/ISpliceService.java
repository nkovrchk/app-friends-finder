package com.friendsfinder.app.service.splice;

import com.friendsfinder.app.model.ModificationData;
import com.friendsfinder.app.model.Node;

import java.util.ArrayList;

/**
 * Сервис для урезания вершин графа
 */
public interface ISpliceService {
    /**
     * Выполняет урезание вершин графа по заданным параметрам
     * @param modificationData Входные параметры
     * @return Урезанный граф
     */
    ArrayList<ArrayList<ArrayList<Node>>> splice (ModificationData modificationData);
}
