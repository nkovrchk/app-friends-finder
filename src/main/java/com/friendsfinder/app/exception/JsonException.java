package com.friendsfinder.app.exception;

import com.friendsfinder.app.exception.base.BaseException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class JsonException extends BaseException {
    public JsonException(String message, ExceptionCode code){
        super(JsonException.class.getName(), code, message);
    }
}
