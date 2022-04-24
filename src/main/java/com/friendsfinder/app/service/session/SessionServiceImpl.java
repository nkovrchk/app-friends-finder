package com.friendsfinder.app.service.session;

import com.friendsfinder.app.model.entity.Token;
import com.friendsfinder.app.model.enums.SessionAttribute;
import com.friendsfinder.app.service.vk.VKClientImpl;
import com.friendsfinder.app.service.vk.dto.VKAccessToken;
import com.friendsfinder.app.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {
    private final Logger logger = Logger.getLogger(SessionServiceImpl.class.getName());

    private final HttpSession session;
    private final TokenRepository tokenRepository;

    private final VKClientImpl vkClient;

    public Integer getUserId (){
        return (Integer) session.getAttribute(SessionAttribute.USER_ID.name());
    }

    public void setToken (VKAccessToken accessToken) {
        var userId = accessToken.getUserId();
        var userToken = accessToken.getToken();
        var expiresIn = accessToken.getExpiresIn();
        var createdOn = accessToken.getCreatedOn();

        var existingToken = tokenRepository.findById(userId);

        if(existingToken.isPresent()){
            var tokenRecord = existingToken.get();

            tokenRecord.setAccessToken(userToken);
            tokenRecord.setCreationDate(createdOn);

            tokenRepository.save(tokenRecord);
        }
        else {
            var newToken = new Token();

            newToken.setUserId(userId);
            newToken.setExpiresIn(expiresIn);
            newToken.setAccessToken(userToken);
            newToken.setCreationDate(createdOn);

            tokenRepository.save(newToken);
        }

        session.setAttribute(SessionAttribute.USER_ID.name(), userId);
        vkClient.setAccessToken(userToken);
    }

    public Token getValidToken () {
        if(session.isNew())
            return null;

        var userId = getUserId();

        if(userId == null)
            return null;

        var tokenRecord = tokenRepository.findById(userId);

        if(tokenRecord.isEmpty())
            return null;

        var token = tokenRecord.get();
        var expiresIn = token.getExpiresIn();
        var createdOn = token.getCreationDate();

        var now = new Date();
        var expirationDate = new Date(createdOn.getTime() + expiresIn * 1000L);

        var isValid = now.after(createdOn) && now.before(expirationDate);

        if(!isValid){
            tokenRepository.deleteById(userId);

            return null;
        }

        logger.log(Level.INFO, "Успешно получен токен из БД: " + token.getAccessToken());

        return token;
    }

    public void logout (){
        var userId = getUserId();

        if(userId != null){
            var tokenRecord = tokenRepository.findById(userId);

            if(tokenRecord.isPresent())
                tokenRepository.deleteById(userId);

            logger.log(Level.INFO, String.format("Пользователь %s вышел из сессии", userId));
        }

        session.invalidate();
    }
}
