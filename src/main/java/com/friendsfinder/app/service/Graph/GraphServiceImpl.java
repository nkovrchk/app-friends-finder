package com.friendsfinder.app.service.Graph;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.SearchParams;
import com.friendsfinder.app.model.UserData;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.Session.SessionServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@Data
public class GraphServiceImpl implements IGraphService {
    private final VKClient vkClient;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;

    private final Set<Integer> idsSet = new HashSet<>();

    private SearchParams params;

    public void updateNodes() {

    }

    public void loadFromDatabase() {

    }

    public void build (SearchParams params) throws JsonException {
        this.setParams(params);

        var userId = vkClient.getUserId();
        var ids = new ArrayList<Integer>();
        ids.add(userId);
        var root = vkClient.getUserData(ids);

        var node = new Node(0, userId);
        var user = new UserData(root.get(0));
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
    private ArrayList<Integer> getFriendsIds (int userId, int count) throws BusinessException {
        var flag = true;
        var calcOffset = count;
        var result = new ArrayList<Integer>();

        while(flag){
            var friendsIds = vkClient.getFriendsIds(userId, count, calcOffset);

            var resultCount = friendsIds.size();

            var filteredIds = friendsIds
                    .stream()
                    .filter(friendId -> idsSet
                            .stream()
                            .filter(friendId::equals)
                            .findFirst()
                            .orElse(-1)
                            .equals(friendId))
                    .toList();

            var preTotalSize = result.size() + filteredIds.size();

            if(preTotalSize < count){
                result.addAll(filteredIds);
                calcOffset += count;
            }
            else {
                var difference = preTotalSize - count;

                result.addAll(filteredIds.stream().limit(difference).toList());
                flag = false;
            }
        }

        return result;
    }

    /**
     * Получает уровень с нодами для дерева
     * 1. Получаем из запроса ширину уровня
     * 2. Для каждого родительского id получаем id друзей
     * 3. Если кол-во id == 0, значит пропускаем итерацию
     * 4. Получаем информацию о друзьях и формируем ноды
     * 5. Добавялем ноды в список - это все друзья одного пользователя
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
                            var node = new Node(depth, user.getId(), parentId);

                            node.setUser(user);

                            return node;
                        })
                        .toList();

                result.add(new ArrayList<>(nodes));
            } catch (BusinessException | JsonException e) {
                e.printStackTrace();
            }
        });

        return result;
    }
}
