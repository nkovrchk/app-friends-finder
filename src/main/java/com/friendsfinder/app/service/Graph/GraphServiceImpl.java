package com.friendsfinder.app.service.Graph;

import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.model.SearchParams;
import com.friendsfinder.app.model.UserData;
import com.friendsfinder.app.model.entity.Graph;
import com.friendsfinder.app.repository.GraphRepository;
import com.friendsfinder.app.service.Session.SessionServiceImpl;
import com.friendsfinder.app.service.VK.VKClient;
import lombok.Data;
import org.springframework.stereotype.Service;

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
        var root = vkClient.getUserData(new int[]{userId});

        var node = new Node(0, userId);
        var user = new UserData(root.get(0));
    }
}
