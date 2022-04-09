package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.exception.factory.BusinessExceptionFactory;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.SearchParams;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@Data
public class GraphServiceImpl implements IGraphService {
    private final VKClientImpl vkClient;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;

    private final Set<Integer> idsSet = new HashSet<>();

    private SearchParams params;

    public void updateNodes() {

    }

    public void loadFromDatabase() {

    }

    public ArrayList<ArrayList<ArrayList<Node>>> build (SearchParams params) throws VKException {
        this.setParams(params);

        var userId = vkClient.getUserId();
        var ids = new ArrayList<Integer>();
        ids.add(userId);
        var usersData = vkClient.getUserData(ids);

        var node = new Node(0, userId);
        node.setUser(usersData.get(0));

        var graph = new ArrayList<ArrayList<ArrayList<Node>>>();

        var rootLevel = new ArrayList<ArrayList<Node>>();

        var root = new ArrayList<Node>();

        root.add(node);
        rootLevel.add(root);
        graph.add(rootLevel);

        for(int i = 1; i <= params.getDepth(); i++){
            var graphIds = graph.get(i - 1);
            var listIds = new ArrayList<Integer>();

            graphIds.forEach(l -> listIds.addAll(l.stream().map(Node::getUserId).toList()));

            var level = getLevel(listIds, i);
            graph.add(level);
        }

        idsSet.clear();

        return graph;
    }

    /**
     * Получает уровень с нодами для дерева
     * 1. Получаем из запроса ширину уровня
     * 2. Для каждого родительского id получаем id друзей
     * 3. Если кол-во id == 0, значит пропускаем итерацию
     * 4. Получаем информацию о друзьях и формируем ноды
     * 5. Добавляем ноды в список - это все друзья одного пользователя
     */
    private ArrayList<ArrayList<Node>> getLevel (ArrayList<Integer> ids, int depth) {
        var count = this.getParams().getWidth();

        var result = new ArrayList<ArrayList<Node>>();

        ids.forEach(parentId -> {
            try {
                var friendsIds = getFriendsIds(parentId, count);

                if(friendsIds.size() == 0)
                    return;

                var users = vkClient.getUserData(friendsIds);
                var nodes = users.stream()
                        .map(user -> {
                            var userId = user.getId();

                            try {
                                var wall = vkClient.getUserWall(userId);
                                var groups = vkClient.getUserGroups(userId);

                                user.setWall(wall);
                                user.setGroups(groups);

                            } catch (VKException e) {
                                e.printStackTrace();
                            }

                            var node = new Node(depth, userId, parentId);

                            node.setUser(user);

                            return node;
                        })
                        .toList();

                result.add(new ArrayList<>(nodes));
            } catch (VKException e) {
                e.printStackTrace();
            }
        });

        return result;
    }

    /**
     * 1. Получить ids
     * 2. Сопоставить с сетом
     * 3. Отфильтровать дубликаты
     * 4. Проверяем результат
     * 4.1 если result + filtered < count тогда добавляем и ищем заново
     * 4.2 если result + filtered >= count тогда завершаем цикл и возвращаем result
     * 4.3 если после запроса кол-во ids < count, значит мы дошли до конца списка друзей
     * и не сможем получить новые данные, тогда flag = false и возвращаем то, что смогли получить
     */
    private ArrayList<Integer> getFriendsIds (int userId, int count) {
        var response = vkClient.getFriendsIds(userId);

        var filteredIds = response
                .stream()
                .filter(friendId -> idsSet.stream().filter(friendId::equals).findFirst().orElse(-1) == -1)
                .toList();

        var result = filteredIds.stream().limit(count).toList();

        // Добавляем новые ID в сет
        idsSet.addAll(result);

        // Записываем результат

        return new ArrayList<>(result);
    }
}
