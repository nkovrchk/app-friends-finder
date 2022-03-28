package com.friendsfinder.app.service.Session;

import com.friendsfinder.app.exception.JsonException;
import com.friendsfinder.app.model.SessionAttribute;
import com.friendsfinder.app.model.AccessToken;
import com.friendsfinder.app.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

    private final JsonUtils jsonUtils;
    private final Logger logger = Logger.getLogger(SessionServiceImpl.class.getName());

    public AccessToken getToken (HttpSession session) {
        var token = (String) session.getAttribute(SessionAttribute.TOKEN.toString());

        if(token == null)
            return null;

        try{
            var accessToken = jsonUtils.parse(token, AccessToken.class);

            logger.log(Level.INFO, "Успешно получен токен из сессии: " + accessToken.getToken());

            return accessToken;
        }
        catch (JsonException je){
            logger.log(Level.SEVERE, "Произошла ошибка при попытке получить токен из сессии");
        }

        return null;
    }

    public boolean isValidToken(AccessToken token) {
        if(token == null)
            return false;

        var today = new Date();
        var createdOn = token.getCreatedOn();
        var expiresIn = token.getExpiresIn() * 1000L;
        var expireDate = new Date(today.getTime() + expiresIn);

        return today.after(createdOn) && today.before(expireDate);
    }
}
