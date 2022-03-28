package com.friendsfinder.app.service.Session;

import com.friendsfinder.app.model.AccessToken;

import javax.servlet.http.HttpSession;

/**
 * Сервис для работы с сессиями и токеном доступа
 */
public interface ISessionService {
    /**
     * Получает токен социальной сети из сессии
     * @param session Текущая сессия
     * @return Access token
     */
    AccessToken getToken(HttpSession session);

    /**
     * Проверяет является ли токен валидным и не истекшим
     * @param token Access token
     * @return Флаг
     */
    boolean isValidToken (AccessToken token);
}
