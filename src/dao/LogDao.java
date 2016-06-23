package dao;

import models.Log;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import utils.DBConnection;
import utils.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component("LogDao")
public class LogDao {

    private static final Logger logger = Logger.getLogger(LogDao.class);

    public synchronized void saveLog(Log log) {
        Connection con = null;
        PreparedStatement stmt = null;

        String sqlSaveLog = "insert into RPLog(CampaignId,Site,PlacementID,PlacementName,Status,ModifyByName,tagType,"
                + "tagValue,Adserver) values (?,?,?,?,?,?,?,?,?)";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSaveLog);
            stmt.setLong(i++, log.getCampaignId());
            stmt.setString(i++, log.getSite());
            stmt.setLong(i++, log.getPlacementId());
            stmt.setString(i++, log.getPlacementName());
            stmt.setString(i++, log.getStatus());
            stmt.setString(i++, log.getModifyByName());
            stmt.setString(i++, log.getTagType());
            stmt.setString(i++, log.getTagValue());
            stmt.setInt(i++, log.getAdServer());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("saveLog error: " + log, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("saveLog error: " + log, e);
            }

        }
    }

    public void saveLog2(Log log) {
        Connection con = null;
        PreparedStatement stmt = null;

        String sqlSaveLog = "insert into RPLog(CampaignId,Site,PlacementID,PlacementName,Status,ModifyByName,tagType,"
                + "tagValue,Adserver) values (?,?,?,?,?,?,?,?,?)";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSaveLog);
            stmt.setLong(i++, log.getCampaignId());
            stmt.setString(i++, log.getSite());
            stmt.setLong(i++, log.getPlacementId());
            stmt.setString(i++, log.getPlacementName());
            stmt.setString(i++, log.getStatus());
            stmt.setString(i++, log.getModifyByName());
            stmt.setString(i++, log.getTagType());
            stmt.setString(i++, log.getTagValue());
            stmt.setInt(i++, log.getAdServer());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("saveLog error: " + log, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("saveLog error: " + log, e);
            }

        }
    }

    public List<Log> getAllLog(int adServer) {
        List<Log> logDCMList = new ArrayList<Log>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM RPLog where Adserver =? order by LastModDate desc";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, adServer);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Log log = new Log();
                log.setCampaignId(rs.getLong("CampaignID"));
                log.setSite(rs.getString("Site"));
                log.setPlacementId(rs.getLong("PlacementID"));
                log.setPlacementName(rs.getString("PlacementName"));
                log.setStatus(rs.getString("Status"));
                log.setModifyByName(rs.getString("ModifyByName"));
                log.setLastModifyDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy/MM/dd HH:mm:ss"));
                log.setTagType(rs.getString("tagType"));
                log.setTagValue(rs.getString("tagValue"));

                logDCMList.add(log);
            }
        } catch (SQLException e) {
            logger.error("get all logs error--adserverId: " + adServer, e);
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
                logger.error("get all logs error--adserverId: " + adServer, e);
            }
        }
        return logDCMList;
    }

    public List<Log> getLogByCamId(long campaignId, int adServer) {
        List<Log> logDCMList = new ArrayList<Log>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM RPLog where CampaignId=? and Adserver =? order by LastModDate desc";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, campaignId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Log log = new Log();
                log.setCampaignId(rs.getLong("CampaignID"));
                log.setSite(rs.getString("Site"));
                log.setPlacementId(rs.getLong("PlacementID"));
                log.setPlacementName(rs.getString("PlacementName"));
                log.setStatus(rs.getString("Status"));
                log.setModifyByName(rs.getString("ModifyByName"));
                log.setLastModifyDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy/MM/dd HH:mm:ss"));
                log.setTagType(rs.getString("tagType"));
                log.setTagValue(rs.getString("tagValue"));
                log.setAdServer(1);

                logDCMList.add(log);
            }
        } catch (SQLException e) {
            logger.error("getLogByCamId error--campaignId: " + campaignId, e);
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
                logger.error("getLogByCamId error--campaignId: " + campaignId, e);
            }
        }
        return logDCMList;
    }

}
