import dao.TokenDao;
import security.Auth;

public class TestToken {

    public static void main(String[] args) {
        //System.out.println(Auth.generateToken().toString());
        Auth auth = new Auth();

        TokenDao tokenDao = new TokenDao();

        tokenDao.saveToken(auth.generateToken());

    }
}
