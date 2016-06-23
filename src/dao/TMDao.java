package dao;

import models.CamTM;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component("TMDao")
public class TMDao {

    private static final Logger logger = Logger.getLogger(RedirectTagDao.class);

    public CamTM getCamTM(int camDCMId) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "select c.camName,c.camId,a.ancName from Campaign c, Annonceur a where c.ancId=a.ancId and "
                + "cadId=?";

        try {

            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, String.valueOf(camDCMId));
            rs = stmt.executeQuery();
            CamTM camTM = new CamTM();
            if (rs.next()) {
                camTM.setAdvNameTM(rs.getString("ancName"));
                camTM.setCamId(rs.getInt("camId"));
                camTM.setCamNameTM(rs.getString("camName"));
            }
            return camTM;
        } catch (SQLException e) {
            logger.error("getCamTM error--camDCMId: " + camDCMId, e);
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
                logger.error("getCamTM error--camDCMId: " + camDCMId, e);
            }
        }
    }

    public Integer getCamAnnonceurId(int camDCMId) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "select ancId from Campaign where cadId=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, String.valueOf(camDCMId));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ancId");

            }
        } catch (SQLException e) {
            logger.error("getCamAnnonceurId error--camDCMId: " + camDCMId, e);
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
                logger.error("getCamAnnonceurId error--camDCMId: " + camDCMId, e);
            }
        }

        return null;
    }

    public boolean verfiyHavasNetwork(int idNetWork, String subName) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "select id from Network where resId=?;";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, idNetWork);
            rs = stmt.executeQuery();
            if (rs.next()) {

                if (idNetWork == 4022 && !subName.equals("")) {
                    if (subName.toUpperCase().contains("HYUNDAI") || subName.toUpperCase().contains("KIA")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                if (idNetWork == 6601 && !subName.equals("")) {
                    if (subName.equals("Citroen (HD/MC Xcountry)") || subName.equals("Peugeot (HD/MC Xcountry)")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            logger.error("verfiyHavasNetwork error--idNetWork: " + idNetWork, e);
            return false;
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
                logger.error("verfiyHavasNetwork error--idNetWork: " + idNetWork, e);
            }
        }

    }

    public String isPlacementValid(int idP, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "select Status from RPPlacementRed where PlacementID=? and Adserver=?";

        try {

            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, idP);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Status");
            }

        } catch (SQLException e) {
            logger.error("isPlacementValid error--idP: " + idP, e);
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
                logger.error("isPlacementValid error--idP: " + idP, e);
            }
        }

        return null;
    }

}
