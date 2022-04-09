package com.friendsfinder.app.service.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.exception.factory.BusinessExceptionFactory;
import com.friendsfinder.app.exception.factory.VKExceptionFactory;
import com.friendsfinder.app.model.*;
import com.friendsfinder.app.service.vk.dto.VKAuthResponse;
import com.friendsfinder.app.service.vk.dto.VKFriendsIdsResponse;
import com.friendsfinder.app.service.vk.dto.VKUser;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @Getter
    private int userId;

    private final Logger logger = Logger.getLogger(VKClientImpl.class.getName());
    private final JsonUtils jsonUtils;

    public String getAuthUrl (){
        return this.authUri +
                "?client_id=" + this.appId +
                "&redirect_uri=" + this.redirectUri +
                "&scope=friends,email,groups";
    }

    public void setAccessToken (AccessToken token) {
        this.accessToken = token.getToken();
        this.userId = token.getUserId();
    }

    public AccessToken retrieveToken(String code) throws BusinessException {
        var authUrl = accessTokenUri +
                "?client_id=" + this.appId +
                "&client_secret=" + this.clientSecret +
                "&redirect_uri=" + this.redirectUri +
                "&code=" + code;

        try {
            var response = jsonUtils.readValueFromUrl(authUrl, VKAuthResponse.class);
            var authToken = new AccessToken(new Date(), response.expires_in, response.access_token, response.user_id);

            logger.log(Level.INFO, "Успешно получен токен пользователя: " + this.accessToken);

            return authToken;
        }
        catch (NullPointerException | JsonException err){
            throw BusinessExceptionFactory.failedToRetrieveToken(code);
        }
    }

    private JsonNode read (String url) throws VKException {
        try{
            var json = jsonUtils.readJsonFromUrl(url);

            if(json.has("error")){
                var error = json.get("error").get("error_msg").asText();

                throw VKExceptionFactory.responseHasErrors(error);
            }

            return json.get("response");
        }
        catch (JsonException e){
            throw VKExceptionFactory.failedToReadJson(e.getMessage());
        }
    }

    public List<VKUser> getUserData (List<Integer> userIds) throws VKException {
        var result = new ArrayList<VKUser>();
        var ids = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        var url = this.apiMethodUri + "/users.get" +
                "?v=" + this.version +
                "&user_ids=" + ids +
                "&access_token=" + this.accessToken +
                "&fields=about,career,interests,city";


        var items = this.read(url);

        if(!items.isArray())
            return result;

        for(JsonNode item : items){
            if(item.has("deactivated") || (item.has("is_closed") && item.get("is_closed").asBoolean())){
                logger.log(Level.INFO, "Пользователь " + item.get("id") + " заблокирован или скрыл профиль");
                continue;
            }

            var vkUser = new VKUser(item);

            result.add(vkUser);
        }

        return result;
    }

    public List<Integer> getFriendsIds (int userId) {
        var result = new ArrayList<Integer>();
        var url = this.apiMethodUri + "/friends.get" +
                "?v=" + this.version +
                "&user_id=" + userId +
                "&access_token=" + this.accessToken +
                "&order=hints";

        try{
            var response = jsonUtils.readValueFromUrl(url, VKFriendsIdsResponse.class);

            result.addAll(response.getResponse().getItems());
            return result;
        }
        catch (JsonException e){
            logger.log(Level.SEVERE, "Не удалось получить ID друзей для пользователя с ID = " + userId);
            return result;
        }
    }

    public List<String> getUserWall (int ownerId) throws VKException {
        var url = this.apiMethodUri + "/wall.get" +
                "?v=" + this.version +
                "&owner_id=" + ownerId +
                "&count=10" +
                "&access_token=" + this.accessToken;

        var json = this.read(url);
        var results = new ArrayList<String>();
        var items = json.get("items");

        for(var node : items){
            var post = node.get("text").asText();

            if(!post.isEmpty()) results.add(post);

            if(node.has("copy_history")){
                var copyHistory = node.get("copy_history");

                for(var copy : copyHistory){
                    var repost = copy.get("text").asText();
                    if(!repost.isEmpty()) results.add(repost);
                }
            }
        }

        return results;
    }

    public List<String> getUserGroups (int userId) throws VKException {
        var url = this.apiMethodUri + "/groups.get" +
                "?v=" + this.version +
                "&user_id=" + userId +
                "&access_token=" + this.accessToken +
                "&extended=1" +
                "&count=10" +
                "&fields=activity,description";

        var json = this.read(url);
        var result = new ArrayList<String>();
        var items = json.get("items");

        for(var node : items) {
            var name = node.get("name").asText();

            result.add(name);

            if (node.has("description")) {
                var description = node.get("description").asText();
                if (!description.isEmpty()) result.add(description.replace("\n", " "));
            }

            if (node.has("activity")) {
                var activity = node.get("activity").asText();
                if (!activity.isEmpty()) result.add(activity);
            }
        }

        return result;
    }
}
