package com.friendsfinder.app.exception.base;

public class BaseException extends Exception{
    private final ExceptionCode code;

    public BaseException(ExceptionCode code, String message){
        super(message);

        this.code = code;
    }

    public ExceptionDetails getDetails() {
        return new ExceptionDetails(this.code, this.getMessage());
    }
}
