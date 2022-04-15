package com.friendsfinder.app.exception;

import com.friendsfinder.app.exception.base.BaseException;
import com.friendsfinder.app.exception.base.ExceptionCode;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Используется внутри клиента ВК в случае когда в ответе возвращается ошибка или не удалось сформировать результат
 */
public class VKException extends BaseException {
    public VKException(String message, ExceptionCode code){
        super(code, message);

        Logger logger = Logger.getLogger(VKException.class.getName());
        logger.log(Level.SEVERE, message);
    }
}
