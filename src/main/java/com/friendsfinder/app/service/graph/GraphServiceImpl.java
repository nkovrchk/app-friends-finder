package com.friendsfinder.app.service.graph;

import com.friendsfinder.app.controller.dto.request.SearchPersonRequest;
import com.friendsfinder.app.controller.dto.response.NodeDto;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.session.SessionServiceImpl;
import com.friendsfinder.app.service.vk.VKClientImpl;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
public class GraphServiceImpl implements IGraphService {
    private final VKClientImpl vkClient;

    private final GraphRepository graphRepository;

    private final SessionServiceImpl sessionService;

    private final Set<Integer> idsSet = new HashSet<>();

    private int depth;

    private int width;

    private ArrayList<ArrayList<ArrayList<Node>>> graph;

    public ArrayList<ArrayList<ArrayList<Node>>> build (SearchPersonRequest params) throws VKException {
        this.setWidth(params.getWidth());
        this.setDepth(params.getDepth() + 1);

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

        for(int i = 1; i <= width; i++){
            var graphIds = graph.get(i - 1);
            var listIds = new ArrayList<Integer>();

            graphIds.forEach(l -> {
                if(l == null){
                    for(var idx = 0; idx < width; idx++)
                        listIds.add(null);
                }
                else {
                    listIds.addAll(l.stream().map(n -> n != null ? n.getUserId() : null).toList());
                }
            });

            var level = getLevel(listIds, i);
            graph.add(level);
        }

        idsSet.clear();

        this.graph = graph;

        return graph;
    }

    public List<NodeDto> getChildren (int graphDepth, int levelIndex, int childIndex) {
        var pos = width * levelIndex + childIndex;
        var tuple = graph.get(graphDepth).get(pos);

        if(tuple == null)
            return null;

        var nodes = new ArrayList<NodeDto>();

        for(var i = 0; i < tuple.size(); i++){
            var node = tuple.get(i);

            if(node == null)
                continue;

            var nodeDto = NodeDto.getNodeDto(node);
            var children = graphDepth < depth ? getChildren(graphDepth + 1, pos, i) : null;

            nodeDto.setChildren(children);

            nodes.add(nodeDto);
        }

        return nodes;
    }

    public NodeDto traverse (){
        var root = NodeDto.getNodeDto(graph.get(0).get(0).get(0));

        var children = this.getChildren(1, 0, 0);

        root.setChildren(children);

        return root;
    }

    private ArrayList<ArrayList<Node>> getLevel (ArrayList<Integer> ids, int depth) {
        var result = new ArrayList<ArrayList<Node>>();

        ids.forEach(parentId -> {
            if(parentId == null){
                result.add(null);
            }
            else {
                try {
                    var friendsIds = getFriendsIds(parentId, width);

                    if(friendsIds.size() == 0)
                        return;

                    var users = vkClient.getUserData(friendsIds);

                    var nodes = users.stream()
                            .map(user -> {
                                var userId = user.getId();

                                var wall = vkClient.getUserWall(userId);
                                var groups = vkClient.getUserGroups(userId);

                                user.setWall(wall);
                                user.setGroups(groups);

                                var node = new Node(depth, userId, parentId);

                                node.setUser(user);

                                return node;
                            })
                            .toList();

                    var arrayNodes = new ArrayList<>(nodes);

                    var difference = width - arrayNodes.size();

                    for(var i = 0; i < difference; i++) arrayNodes.add(null);

                    result.add(new ArrayList<>(arrayNodes));
                } catch (VKException e) {
                    e.printStackTrace();
                }
            }
        });

        var difference = ids.size() - result.size();

        for(var i = 0; i < difference; i++) result.add(null);

        return result;
    }

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
