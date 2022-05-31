package com.friendsfinder.app.service.build;

import com.friendsfinder.app.controller.dto.request.SearchRequest;
import com.friendsfinder.app.model.GraphModel;

public interface IBuildService {
    /**
     * Строит граф с указанной шириной и глубиной
     * @param params Параметра графа
     * @return Построенный граф
     */
    GraphModel build (SearchRequest params);
}
