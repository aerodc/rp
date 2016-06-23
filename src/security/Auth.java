package security;

import dao.TokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class Auth {

    private TokenDao tokenDao;

    @Autowired
    public void setTokenDao(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public Token firstHandShake(String login, String pwd) {

        if (MAUAuth.isMAUUser(login, pwd)) {//MAU
            return generateToken();
        } else {
            return null;
        }

    }

    public boolean isTokenExpired(String tokenValue) {
        return tokenDao.isTokenExpired(tokenValue);
    }

    public Token generateToken() {

        SecureRandom random = new SecureRandom();

        String tokenValue = new BigInteger(130, random).toString(32);

        Token token = new Token(tokenValue);

        tokenDao.saveToken(token);

        return token;
    }

    public void saveDCMToken(String dcmToken) {

        Token token = new Token(dcmToken);
        tokenDao.saveToken(token);
    }

}
