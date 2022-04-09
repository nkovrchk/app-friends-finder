package com.friendsfinder.app.exception.base;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseException extends Exception{
    private final ExceptionCode code;

    public BaseException(String name, ExceptionCode code, String message){
        super(message);

        this.code = code;

        Logger logger = Logger.getLogger(name);
        logger.log(Level.SEVERE, name + ": " + message);
    }

    public ExceptionDetails getDetails() {
        return new ExceptionDetails(this.code, this.getMessage());
    }
}
