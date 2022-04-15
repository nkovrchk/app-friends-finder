package com.friendsfinder.app.exception.factory;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class JsonExceptionFactory {
    public static JsonException failedToStringifyObject(){
        return new JsonException("Не удалось преобразовать объект в JSON", ExceptionCode.J001);
    }

    public static JsonException failedToParseJson(){
        return new JsonException("Не удалось преобразовать JSON в объект", ExceptionCode.J002);
    }
}
