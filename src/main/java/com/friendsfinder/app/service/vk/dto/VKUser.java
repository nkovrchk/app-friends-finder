package com.friendsfinder.app.service.vk.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VKUser {
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

    public VKUser (JsonNode jsonUser){
        var id = jsonUser.get("id").asInt();
        var firstName = jsonUser.get("first_name").asText();
        var lastName = jsonUser.get("last_name").asText();

        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);

        var about = jsonUser.has("about") ? jsonUser.get("about").asText() : "";
        var interests = jsonUser.has("interests") ? jsonUser.get("interests").asText() : "";
        var city = jsonUser.has("city") ? jsonUser.get("city").get("title").asText() : "";
        var photo = jsonUser.has("photo_200") ? jsonUser.get("photo_200").asText() : "";

        this.setAbout(about);
        this.setInterests(interests);
        this.setCity(city);
        this.setPhoto(photo);

        if(!jsonUser.has("career")){
            this.setCareer("");
        }
        else {
            var res = new ArrayList<String>();
            var careers = jsonUser.get("career");

            for(JsonNode career : careers) {
                var company = career.get("company");

                res.add(company != null ? company.asText() : "");
            }

            this.setCareer(String.join(", ", res));
        }
    }
}