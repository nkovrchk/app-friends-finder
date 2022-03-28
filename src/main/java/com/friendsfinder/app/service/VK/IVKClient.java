package com.friendsfinder.app.service.VK;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.model.AccessToken;

/**
 * Клиент для работы с API Вконтакте
 */
public interface IVKClient {
    /**
     * Устанавливает токен доступа
     * @param token Access token
     */
    void setAccessToken (AccessToken token);

    /**
     * Получает токен социальной сети
     * @param code Код доступа
     * @return Токен
     */
    AccessToken retrieveToken(String code) throws BusinessException;
}
