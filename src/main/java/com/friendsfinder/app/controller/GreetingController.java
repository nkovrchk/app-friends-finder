package com.friendsfinder.app.controller;

import com.friendsfinder.app.service.VKApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GreetingController{
    @Autowired
    private VKApiService vkApiService;

    private static final String template = "Hello, %s!";

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "friend") String name){
        return String.format(template, name);
    }

    @GetMapping("/todos")
    public ResponseEntity<String> getTodos(){
        return new ResponseEntity<>(vkApiService.getUserInfo(), HttpStatus.OK);
    }
}
