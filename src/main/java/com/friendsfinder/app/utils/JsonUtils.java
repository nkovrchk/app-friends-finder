package com.friendsfinder.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.exception.factory.JsonExceptionFactory;

import java.io.*;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public JsonUtils () {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String stringify (Object value) throws JsonException {
        try{
            return this.objectMapper.writeValueAsString(value);
        }
        catch (JsonProcessingException e){
            throw JsonExceptionFactory.failedToStringifyObject();
        }
    }

    public <T> T parse (String json, Class<T> dataType) throws JsonException {
        try{
            return this.objectMapper.readValue(json, dataType);
        }
        catch(JsonProcessingException ex){
            throw JsonExceptionFactory.failedToParseJson();
        }

    }

    public <T> T parse (String json, TypeReference<T> typeRef) throws JsonException {
        try{
            return this.objectMapper.readValue(json, typeRef);
        }
        catch(JsonProcessingException ex){
            throw JsonExceptionFactory.failedToParseJson();
        }
    }

    public JsonNode readJsonFromUrl (String url) throws IOException {
        return objectMapper.readTree(new URL(url));
    }
}
