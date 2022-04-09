package com.friendsfinder.app.exception;

import com.friendsfinder.app.exception.base.BaseException;
import com.friendsfinder.app.exception.base.ExceptionCode;

/**
 * Используется внутри клиента ВК в случае когда в ответе возвращается ошибка или не удалось сформировать результат
 */
public class VKException extends BaseException {
    public VKException(String message, ExceptionCode code){
        super(VKException.class.getName(), code, message);
    }
}
