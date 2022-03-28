package com.friendsfinder.app.exception;

import com.friendsfinder.app.exception.base.BaseException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class BusinessException extends BaseException {
    public BusinessException(String message, ExceptionCode code){
        super(code, message);
    }
}
