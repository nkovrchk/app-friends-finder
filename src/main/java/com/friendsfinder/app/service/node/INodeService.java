package com.friendsfinder.app.service.node;

import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.User;

public interface INodeService {
    /**
     * Создает вершину графа из данных пользователя
     * @param user Данные пользователя
     * @param parentId ID родительской вершины
     * @param depth Глубина вершины
     * @return Новая вершина графа
     */
    Node createNode(User user, Integer parentId, Integer depth);

    /**
     * Обновляет словоформы в вершине, используя данные пользователя
     * @param node Вершина графа
     */
    void updateWordForms (Node node);

    /**
     * Обновляет данные и словоформы внутри вершины
     * @param node Вершина графа
     */
    void updateNodeData (Node node);

}
