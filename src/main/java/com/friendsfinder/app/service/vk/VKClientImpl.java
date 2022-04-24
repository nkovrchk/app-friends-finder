package com.friendsfinder.app.service.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.exception.factory.BusinessExceptionFactory;
import com.friendsfinder.app.exception.factory.VKExceptionFactory;
import com.friendsfinder.app.model.User;
import com.friendsfinder.app.service.vk.dto.VKAccessToken;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VKClientImpl implements IVKClient {
    private final String appId = "8044534";
    private final String clientSecret = "KSEH55wukhHmHMyAgKSl";
    private final String redirectUri = "http://localhost:8081/auth/token";
    private final String authUri = "https://oauth.vk.com/authorize";
    private final String accessTokenUri = "https://oauth.vk.com/access_token";
    private final String apiMethodUri = "https://api.vk.com/method";
    private final String version = "5.131";

    private String accessToken;

    private final Logger logger = Logger.getLogger(VKClientImpl.class.getName());
    private final JsonUtils jsonUtils;

    public String getAuthUrl() {
        return this.authUri +
                "?client_id=" + this.appId +
                "&redirect_uri=" + this.redirectUri +
                "&scope=friends,email,groups";
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    private JsonNode getData(String url) throws VKException {
        var triesLeft = 4;
        JsonNode response = null;

        while(triesLeft > 0){
            try {
                var json = jsonUtils.readJsonFromUrl(url);

                 if (json.has("error")) {
                    var error = json.get("error");
                    var errorCode = error.get("error_code").asInt();
                    var errorMessage = error.get("error_msg").asText();

                    if (errorCode == 6) {
                        if (triesLeft > 1) {
                            triesLeft--;
                            logger.log(Level.WARNING, "Request timeout, tries left: " + triesLeft);
                            TimeUnit.SECONDS.sleep(1);

                            continue;
                        }
                        else {
                            throw VKExceptionFactory.requestTimeout(url);
                        }
                    }
                    else {
                        throw VKExceptionFactory.responseHasErrors(url, errorMessage);
                    }
                }
                response = json;

                triesLeft = 0;
            } catch (IOException | InterruptedException e) {
                throw VKExceptionFactory.failedToReadJson(e.getMessage());
            }
        }

        return response;
    }


    public VKAccessToken retrieveToken(String code) throws BusinessException {
        var authUrl = accessTokenUri +
                "?client_id=" + this.appId +
                "&client_secret=" + this.clientSecret +
                "&redirect_uri=" + this.redirectUri +
                "&code=" + code;

        try {
            var response = getData(authUrl);

            var createdOn = new Date();
            var expiresIn = response.get("expires_in").asInt();
            var accessToken = response.get("access_token").asText();
            var userId = response.get("user_id").asInt();

            var authToken = new VKAccessToken(createdOn, expiresIn, accessToken, userId);

            logger.log(Level.INFO, String.format("Успешно получен токен пользователя ВК: %s", this.accessToken));

            return authToken;
        } catch (VKException err) {
            throw BusinessExceptionFactory.failedToRetrieveToken(code);
        }
    }

    public List<User> getUserData(List<Integer> userIds) throws VKException {
        var result = new ArrayList<User>();
        var ids = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        var url = apiMethodUri + "/users.get" +
                "?v=" + version +
                "&user_ids=" + ids +
                "&access_token=" + accessToken +
                "&fields=about,career,interests,city,photo_200";


        var items = getData(url).get("response");

        if (!items.isArray())
            return result;

        for (JsonNode item : items) {
            var hasAccess = item.has("can_access_closed") && item.get("can_access_closed").asBoolean();
            var isDeactivated = item.has("deactivated") && item.get("deactivated").asBoolean();

            if (isDeactivated || !hasAccess) {
                logger.log(Level.WARNING, String.format("Пользователь ID: %s скрыл или заблокировал профиль", item.get("id")));
                continue;
            }

            var user = User.parseResponse(item);

            result.add(user);
        }

        return result;
    }

    public List<Integer> getFriendsIds(int userId) {
        var result = new ArrayList<Integer>();
        var url = this.apiMethodUri + "/friends.get" +
                "?v=" + this.version +
                "&user_id=" + userId +
                "&access_token=" + this.accessToken +
                "&order=hints";

        try {
            var items = getData(url).get("response").get("items");

            items.forEach(r -> result.add(r.asInt()));

            return result;
        } catch (VKException e) {
            logger.log(Level.SEVERE, String.format("Не удалось получить ID друзей для пользователя %s", userId));
            return result;
        }
    }

    public List<String> getUserWall(int ownerId) {
        var wall = new ArrayList<String>();
        var url = this.apiMethodUri + "/wall.get" +
                "?v=" + this.version +
                "&owner_id=" + ownerId +
                "&count=10" +
                "&access_token=" + this.accessToken;

        try {
            var json = this.getData(url);
            var items = json.get("response").get("items");

            for (var node : items) {
                var post = node.get("text").asText();

                if (!post.isEmpty()) wall.add(post);

                if (node.has("copy_history")) {
                    var copyHistory = node.get("copy_history");

                    for (var copy : copyHistory) {
                        var repost = copy.get("text").asText();
                        if (!repost.isEmpty()) wall.add(repost);
                    }
                }
            }
        } catch (VKException e) {
            return wall;
        }

        return wall;
    }

    public List<String> getUserGroups(int userId) {
        var groups = new ArrayList<String>();
        var url = this.apiMethodUri + "/groups.get" +
                "?v=" + this.version +
                "&user_id=" + userId +
                "&access_token=" + this.accessToken +
                "&extended=1" +
                "&count=10" +
                "&fields=activity,description";

        try {
            var json = this.getData(url);
            var items = json.get("response").get("items");

            for (var node : items) {
                var name = node.get("name").asText();

                groups.add(name);

                if (node.has("description")) {
                    var description = node.get("description").asText();
                    if (!description.isEmpty()) groups.add(description.replace("\n", " "));
                }

                if (node.has("activity")) {
                    var activity = node.get("activity").asText();
                    if (!activity.isEmpty()) groups.add(activity);
                }
            }
        } catch (VKException e) {
            return groups;
        }

        return groups;
    }
}
