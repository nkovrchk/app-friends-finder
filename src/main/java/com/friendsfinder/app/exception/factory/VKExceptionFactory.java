package com.friendsfinder.app.exception.factory;

import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class VKExceptionFactory {
    public static VKException failedToReadJson(String message){
        return new VKException(message, ExceptionCode.V001);
    }

    public static VKException requestTimeout(String url) {
        return new VKException(String.format("Request timeout: %s", url), ExceptionCode.V002);
    }

    public static VKException responseHasErrors(String url, String error){
        return new VKException(String.format("Не удалось получить данные\n URL: %s\n Error: %s", url, error), ExceptionCode.V003);
    }
}
