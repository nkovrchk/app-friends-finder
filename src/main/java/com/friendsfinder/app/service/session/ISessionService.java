package com.friendsfinder.app.service.session;

import com.friendsfinder.app.model.entity.Token;
import com.friendsfinder.app.service.vk.dto.VKAccessToken;

import javax.servlet.http.HttpSession;

/**
 * Сервис для работы с сессиями и токеном доступа
 */
public interface ISessionService {
    /**
     * Получает из сессии ID текущего пользователя
     * @return ID текущего пользователя
     */
    Integer getUserId();

    /**
     * Сохраняет полученный из ВК токен в БД и ID пользователя в сессии
     * @param accessToken Новый токен доступа
     */
    void setToken(VKAccessToken accessToken);

    /**
     * Получает действующий токен из БД. Если токен не актуальный, то удаляет из БД
     */
    Token getValidToken ();

    /**
     * Уничтожает текущую сессию пользователя и удаляет из БД связанный токен доступа
     */
    void logout ();
}
