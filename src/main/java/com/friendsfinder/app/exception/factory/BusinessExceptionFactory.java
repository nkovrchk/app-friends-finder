package com.friendsfinder.app.exception.factory;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.base.ExceptionCode;

public class BusinessExceptionFactory {
    public static BusinessException failedToRetrieveToken(String code){
        return new BusinessException("Не удалось получить токен авторизации, code = " + code, ExceptionCode.B001);
    }

    public static BusinessException failedToGetIds(int userId){
        return new BusinessException("Не удалось получить идентификаторы друзей для пользователя ID = " + userId, ExceptionCode.B003);
    }
}
