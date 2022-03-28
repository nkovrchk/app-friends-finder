package com.friendsfinder.app.model.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.Node;
import com.friendsfinder.app.utils.JsonUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;

@Converter(autoApply = true)
public class GraphConverter implements AttributeConverter<ArrayList<ArrayList<ArrayList<Node>>>, String> {

    private final JsonUtils jsonUtils = new JsonUtils();

    @Override
    public String convertToDatabaseColumn(ArrayList<ArrayList<ArrayList<Node>>> attribute) {
        try {
            return jsonUtils.stringify(attribute);
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<ArrayList<ArrayList<Node>>> convertToEntityAttribute(String dbData) {
        try {
            TypeReference<ArrayList<ArrayList<ArrayList<Node>>>> typeRef = new TypeReference<>() {};
            return jsonUtils.parse(dbData, typeRef);
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return null;
    }
}
