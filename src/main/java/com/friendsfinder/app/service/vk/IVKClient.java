package com.friendsfinder.app.service.vk;

import com.friendsfinder.app.exception.BusinessException;
import com.friendsfinder.app.exception.VKException;
import com.friendsfinder.app.model.AccessToken;
import com.friendsfinder.app.service.vk.dto.VKUser;

import java.util.List;

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
    List<VKUser> getUserData (List<Integer> userIds) throws VKException;

    /**
     * @param ownerId ID владельца стены
     * @return Список постов на стене
     * @throws VKException Не удалось получить стену пользователя
     */
    List<String> getUserWall (int ownerId) throws VKException;

    /**
     * @param userId ID пользователя
     * @return Список с информацией о группах пользователя
     * @throws VKException Не удалось получить информацию о группах пользователя
     */
    List<String> getUserGroups (int userId) throws VKException;
}
