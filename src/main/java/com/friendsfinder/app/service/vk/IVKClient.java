package com.friendsfinder.app.service.vk;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.service.vk.dto.VKAccessToken;
import com.friendsfinder.app.model.User;

import java.util.List;

/**
 * Клиент для работы с API Вконтакте
 */
public interface IVKClient {
    /**
     * Устанавливает токен доступа
     * @param token Access token
     */
    void setAccessToken (String token);

    /**
     * Получает токен социальной сети
     * @param code Код доступа
     * @return Токен
     */
    VKAccessToken retrieveToken(String code) throws BusinessException;

    /**
     * @param userId ID пользователя
     * @return Список ID друзей
     */
    List<Integer> getFriendsIds (int userId);

    /**
     * @param userIds Список идентификаторов
     * @return Данные пользователя
     * @throws VKException Не удалось получить данные пользователя
     */
    List<User> getUserData (List<Integer> userIds) throws VKException;

    /**
     * @param ownerId ID владельца стены
     * @return Список постов на стене
     */
    List<String> getUserWall (int ownerId);

    /**
     * @param userId ID пользователя
     * @return Список с информацией о группах пользователя
     */
    List<String> getUserGroups (int userId);
}
