package com.friendsfinder.app.exception.factory;

import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class VKExceptionFactory {
    public static VKException failedToReadJson(String message){
        return new VKException(message, ExceptionCode.V001);
    }

    public static VKException responseHasErrors(String error){
        return new VKException(error, ExceptionCode.V002);
    }
}
