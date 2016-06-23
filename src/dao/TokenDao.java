package dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import security.Token;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

@Component("TokenDao")
public class TokenDao {

    private static final Logger logger = Logger.getLogger(TokenDao.class);

    public boolean isTokenExpired(String tokenValue) {
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;

        ResourceBundle resourceBundle = ResourceBundle.getBundle("properties.wsrp");

        int expiretime = Integer.parseInt(resourceBundle.getString("token.expire"));

        String sql = "select CreateDate, Expired from TMToken where Token =?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tokenValue);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("Expired") == 1) { return true; } else {
                    Date sqlDate = rs.getTimestamp("CreateDate");
                    //					System.out.println(sqlDate.getTime());
                    //					System.out.println((new Date()).getTime());
                    if ((new Date()).getTime() - sqlDate.getTime() > expiretime * 60 * 1000) {
                        String sqlUpdate = "update TMToken set Expired=1 where Token=?";
                        stmt2 = con.prepareStatement(sqlUpdate);
                        stmt2.setString(1, tokenValue);
                        stmt2.executeUpdate();
                        return true;
                    } else { return false; }
                }

            }
        } catch (SQLException e) {
            logger.error("isTokenExpired error--tokenValue: " + tokenValue, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("isTokenExpired error--tokenValue: " + tokenValue, e);
            }
        }

        return true;
    }

    public synchronized boolean saveToken(Token token) {

        Connection con = null;
        PreparedStatement stmt = null;
        int rs = 0;

        String sqlSaveToken = "insert into TMToken(Token,Expired) values (?,?)";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSaveToken);
            stmt.setString(1, token.getTokenValue());
            stmt.setInt(2, 0);
            rs = stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("saveToken error--token: " + token.getTokenValue(), e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("isTokenEsaveTokenxpired error--token: " + token.getTokenValue(), e);
            }

        }

        if (rs > 0) { return true; } else { return false; }

    }
}
