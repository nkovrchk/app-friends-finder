package com.friendsfinder.app.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    /**
     * Идентификатор пользователя.
     */
    private int id;

    /**
     * Имя.
     */
    private String firstName;

    /**
     * Фамилия.
     */
    private String lastName;

    /**
     * Содержимое поля «Интересы» из профиля.
     */
    private String interests;

    /**
     * Содержимое поля «О себе» из профиля.
     */
    private String about;

    /**
     * Информация о городе, указанном на странице пользователя в разделе «Контакты».
     */
    private String city;

    /**
     * Информация о карьере пользователя.
     */
    private String career;

    private List<String> wall;

    private List<String> groups;

    private String photo;

    public static User parseJson (JsonNode json){
        var user = new User();

        var id = json.get("id").asInt();
        var firstName = json.get("firstName").asText();
        var lastName = json.get("lastName").asText();

        var interests = json.get("interests").asText();
        var about = json.get("about").asText();
        var photo = json.get("photo").asText();
        var city = json.get("city").asText();
        var career = json.get("career").asText();

        var wall = json.get("wall");

        var posts = new ArrayList<String>();

        if(wall != null && wall.isArray()){
            for(JsonNode post : wall){
                var parsedPost = post.asText();

                posts.add(parsedPost);
            }
        }

        var groups = json.get("groups");

        var titles = new ArrayList<String>();

        if(groups != null && groups.isArray()){
            for(JsonNode group : groups){
                var title = group.asText();

                titles.add(title);
            }
        }

        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setInterests(interests);
        user.setAbout(about);
        user.setPhoto(photo);
        user.setCareer(career);
        user.setCity(city);

        user.setWall(posts);
        user.setGroups(titles);

        return user;
    }

    public static User parseResponse (JsonNode response){
        var user = new User();

        var id = response.get("id").asInt();
        var firstName = response.get("first_name").asText();
        var lastName = response.get("last_name").asText();

        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        var about = response.has("about") ? response.get("about").asText() : "";
        var interests = response.has("interests") ? response.get("interests").asText() : "";
        var city = response.has("city") ? response.get("city").get("title").asText() : "";
        var photo = response.has("photo_200") ? response.get("photo_200").asText() : "";

        user.setAbout(about);
        user.setInterests(interests);
        user.setCity(city);
        user.setPhoto(photo);

        if(!response.has("career")){
            user.setCareer("");
        }
        else {
            var res = new ArrayList<String>();
            var careers = response.get("career");

            for(JsonNode career : careers) {
                var company = career.get("company");

                res.add(company != null ? company.asText() : "");
            }

            user.setCareer(String.join(", ", res));
        }

        return user;
    }
}