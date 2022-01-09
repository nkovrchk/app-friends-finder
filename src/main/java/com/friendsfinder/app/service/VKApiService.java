package com.friendsfinder.app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VKApiService {
    private final RestTemplate client;

    public VKApiService (){
        this.client = new RestTemplate();
    }

    public String getUserInfo (){
        return this.client.getForEntity("https://jsonplaceholder.typicode.com/todos/1", String.class).getBody();
    }
}
