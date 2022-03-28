package com.friendsfinder.app.config;

import com.friendsfinder.app.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class ExceptionHelper {
    private static final Logger logger = Logger.getLogger(ExceptionHelper.class.getName());

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<Object> handleBusinessException (BusinessException exception){
        logger.log(Level.SEVERE, exception.getMessage());
        return new ResponseEntity<>(exception.getDetails(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<Object> handleIOException (IOException exception) {
        logger.log(Level.SEVERE, exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
