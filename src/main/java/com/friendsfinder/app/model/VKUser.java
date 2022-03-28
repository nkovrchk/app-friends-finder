package com.friendsfinder.app.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.ArrayList;

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

        this.setAbout(about);
        this.setInterests(interests);
        this.setCity(city);

        if(!jsonUser.has("career")){
            this.setCareer("");
        }
        else {
            var res = new ArrayList<String>();
            var careers = jsonUser.get("career");

            for(JsonNode career : careers) {
                res.add(career.get("company").asText());
            }

            this.setCareer(String.join(", ", res));
        }
    }
}