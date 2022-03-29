package com.friendsfinder.app.service.VK;

import com.fasterxml.jackson.databind.JsonNode;
import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.exception.factory.BusinessExceptionFactory;
import com.friendsfinder.app.model.*;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VKClient implements IVKClient {
    private final String appId = "8044534";
    private final String clientSecret = "KSEH55wukhHmHMyAgKSl";

    private final String redirectUri = "http://localhost:8081/api/v1/auth/token";
    private final String authUri = "https://oauth.vk.com/authorize";
    private final String accessTokenUri = "https://oauth.vk.com/access_token";
    private final String apiMethodUri = "https://api.vk.com/method";
    private final String version = "5.131";
    private final String scope = "friends,email,groups";

    private String accessToken;
    @Getter
    private int userId;

    private final Logger logger = Logger.getLogger(VKClient.class.getName());
    private final JsonUtils jsonUtils;

    public String getAuthUrl (){
        return this.authUri +
                "?client_id=" + this.appId +
                "&redirect_uri=" + this.redirectUri +
                "&scope=" + this.scope;
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

    public ArrayList<VKUser> getUserData (ArrayList<Integer> userIds) throws JsonException {
        var result = new ArrayList<VKUser>();
        var ids = userIds.stream().map(String::valueOf).reduce(String::concat).stream().collect(Collectors.joining(","));

        var url = this.apiMethodUri + "/users.get" +
                "?v=" + this.version +
                "&user_ids=" + ids +
                "&access_token=" + this.accessToken +
                "&fields=about,career,interests,city";


        var json = jsonUtils.readJsonFromUrl(url);

        if (json.has("error"))
            return result;

        var items = json.get("response");

        if(!items.isArray())
            return result;

        for(JsonNode item : items){
            if(item.has("deactivated") || (item.has("is_closed") && item.get("is_closed").asBoolean()))
                continue;

            var vkUser = new VKUser(item);

            result.add(vkUser);
        }

        return result;
    }

    public ArrayList<Integer> getFriendsIds (int userId, int count, int offset) throws BusinessException {
        var url = this.apiMethodUri + "/friends.get" +
                "?v=" + this.version +
                "&user_id=" + userId +
                "&count=" + count +
                "&offset=" + offset +
                "&access_token=" + this.accessToken +
                "&order=hints";

        try{
            var response = jsonUtils.readValueFromUrl(url, VKFriendsIdsResponse.class);

            return response.getResponse().getItems();
        }
        catch (JsonException e){
            throw BusinessExceptionFactory.failedToGetIds(userId);
        }

    }
}
