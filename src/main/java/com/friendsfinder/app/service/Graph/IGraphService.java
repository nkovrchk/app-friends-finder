package com.friendsfinder.app.service.Graph;

/**
 * Сервис для работы с графом друзей
 */
public interface IGraphService {
    /**
     * Выполняет обновление данных во всех вершинах графа и перерасчитывает словоформы
     */
    void updateNodes ();

    /**
     * Выгружает граф из БД
     */
    void loadFromDatabase ();
}
