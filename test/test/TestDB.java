import dao.TokenDao;

public class TestDB {

    public static void main(String[] args) {

        TokenDao tokenDao = new TokenDao();
        //tokenDao.saveToken((new Auth()).generateToken());

        System.out.println(tokenDao.isTokenExpired("inivjmtgnkod9a4oj1rr75q57i"));
    }

}
