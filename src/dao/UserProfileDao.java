package dao;

import models.Network;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component("UserProfileDao")
public class UserProfileDao {

    private static final Logger logger = Logger.getLogger(UserProfileDao.class);

    private TMDao tmDao;

    @Autowired
    public void setTmDao(TMDao tmDao) {
        this.tmDao = tmDao;
    }

    public int getUserProfileId(int reseauId) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int userProfileId = 0;

        String sql = "SELECT profileId FROM Network where resId=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, reseauId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                userProfileId = rs.getInt("profileId");
            }
        } catch (SQLException e) {
            logger.error("getUserProfileId error--reseauId: " + reseauId, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getUserProfileId error--reseauId: " + reseauId, e);
            }
        }
        return userProfileId;
    }

    public List<Network> getSizmekNetwork() {

        List<Network> sizmekNetworkList = new ArrayList<Network>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM Network where adsId=2";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Network network = new Network();
                network.setResKey(rs.getString("resKey"));
                network.setLogin(rs.getString("login"));
                network.setPassword(rs.getString("pw"));
                sizmekNetworkList.add(network);
            }
        } catch (SQLException e) {
            logger.error("getSizmekNetwork error", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getSizmekNetwork error", e);
            }
        }
        return sizmekNetworkList;
    }

    public String getSizmekResKeyByCampaign(int camId) {

        int ancId = tmDao.getCamAnnonceurId(camId);

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sizmekResKey = null;

        String sql = "SELECT resKey FROM Annonceur where ancId=? and adsId=2";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, ancId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                sizmekResKey = rs.getString("resKey");
            }
        } catch (SQLException e) {
            logger.error("getSizmekResKeyByCampaign error--camId: " + camId, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getSizmekResKeyByCampaign error--camId: " + camId, e);
            }
        }
        return sizmekResKey;
    }

    public Network getNetworkByResKey(String resKey, int adsId) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT id, login, pw FROM Network where resKey=? and adsId=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, resKey);
            stmt.setInt(2, adsId);
            rs = stmt.executeQuery();
            if (rs.next()) {

                Network network = new Network();
                network.setId(rs.getInt("id"));
                network.setLogin(rs.getString("login"));
                network.setPassword(rs.getString("pw"));
                network.setResKey(resKey);

                return network;
            }
        } catch (SQLException e) {
            logger.error("getNetworkByResKey error--resKey: " + resKey, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getNetworkByResKey error--resKey: " + resKey, e);
            }
        }
        return null;
    }

    public Network getNetworkByLoginPassword(String login, String password, int adsId) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT id, resKey FROM Network where login=? and pw=? and adsId=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setInt(3, adsId);
            rs = stmt.executeQuery();
            if (rs.next()) {

                Network network = new Network();
                network.setId(rs.getInt("id"));
                network.setResKey(rs.getString("resKey"));
                network.setLogin(login);
                network.setPassword(password);

                return network;
            }
        } catch (SQLException e) {
            logger.error("getNetworkByLoginPassword error--login: " + login, e);
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getNetworkByLoginPassword error--login: " + login, e);
            }
        }
        return null;
    }
}
