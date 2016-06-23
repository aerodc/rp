package dao;

import models.Redirect;
import models.RedirectTag;
import models.RedirectTag2;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import utils.DBConnection;
import utils.TimeUtil;

import javax.jdo.annotations.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component("RedirectTagDao")
public class RedirectTagDao {

    private static final Logger logger = Logger.getLogger(RedirectTagDao.class);

    @Transactional
    public void saveSingleRedTag2(RedirectTag redirectTag, Redirect redirect, Connection connection) throws Exception {
        if (redTagExist2(redirect.getPlacementId(), redirectTag.getTagType(), redirect.getAdServer())) {
            updateSingleRedTag2(redirectTag, connection);
        } else {
            insertSingleRedTag2(redirectTag, connection);
        }
    }

    public boolean redTagExist2(long placementID, String tagType, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT PlacementID FROM RPRedTag where PlacementID =? and tagType=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(i++, (int) placementID);
            stmt.setString(i++, tagType);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error("redTagExist2 error--placementID: " + placementID, e);
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
                logger.error("redTagExist2 error--placementID: " + placementID, e);
            }
        }
        return false;
    }

    public void updateSingleRedTag2(RedirectTag redirectTag, Connection con) throws Exception {
        PreparedStatement stmt = null;

        String sqlUpdateRedTag = "update RPRedTag set TagValue=?, Status=?, LastModDate=?, TagDescription=? where "
                + "PlacementID=? and TagType=? and Adserver=?";

        try {
            int i = 1;
            stmt = con.prepareStatement(sqlUpdateRedTag);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setString(i++, redirectTag.getTagValue());
            stmt.setString(i++, redirectTag.getStatus());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setString(i++, redirectTag.getTagDescription());
            stmt.setLong(i++, redirectTag.getPlacementId());
            stmt.setString(i++, redirectTag.getTagType());
            stmt.setInt(i++, redirectTag.getAdServer());
            stmt.executeUpdate();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                logger.error("updateSingleRedTag2 error--redirectTag: " + redirectTag.getPlacementId(), e);
            }
        }
    }

    public void insertSingleRedTag2(RedirectTag redirectTag, Connection con) throws SQLException {
        PreparedStatement stmt = null;

        String sqlSaveRedTag = "insert into RPRedTag(PlacementID,TagType,TagValue,Status,LastModDate,TagDescription,"
                + "Adserver) values (?,?,?,?,?,?,?)";

        try {
            int i = 1;
            stmt = con.prepareStatement(sqlSaveRedTag);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setLong(i++, redirectTag.getPlacementId());
            stmt.setString(i++, redirectTag.getTagType());
            stmt.setString(i++, redirectTag.getTagValue());
            stmt.setString(i++, redirectTag.getStatus());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setString(i++, redirectTag.getTagDescription());
            stmt.setInt(i++, redirectTag.getAdServer());
            stmt.executeUpdate();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                logger.error("insertSingleRedTag2 error--redirectTag: " + redirectTag.getPlacementId(), e);
            }

        }
    }

    public RedirectTag getSingRedTag2(String placementID, String tagType, int adServer) {
        RedirectTag redTag = null;

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        PreparedStatement stmt1 = null;
        ResultSet rs1 = null;

        try {

            String sql = "select TagType,TagValue,CreateDate,TagDescription from RPRedTag where PlacementID=? and "
                    + "TagType=? and Adserver=?";

            String sql1 = "select top(1) ModifyByName,LastModDate,Status from RPLog where PlacementID=? and tagType=?"
                    + " and Adserver=? order by LastModDate desc;";
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(i++, Integer.parseInt(placementID));
            stmt.setString(i++, tagType);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();
            if (rs.next()) {
                redTag = new RedirectTag();
                redTag.setPlacementId(Integer.parseInt(placementID));
                redTag.setAdServer(1);
                redTag.setTagType(rs.getString("TagType"));
                redTag.setTagValue(rs.getString("TagValue"));
                //redTag.setStatus(rs.getString("Status"));
                redTag.setCreatedDate(rs.getDate("CreateDate"));
                //redTag.setLastupdatedDate(TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"),"yyyy-MM-dd
                // HH:mm:ss"));
                redTag.setTagDescription(rs.getString("TagDescription"));
                //redTag.setModifyByName(rs.getString("ModifyByName"));
                //System.out.println("dao "+rs.getInt("PlacementID")+rs.getString("TagType")+rs.getString
                // ("ModifyByName"));

                stmt1 = con.prepareStatement(sql1);
                stmt1.setInt(1, Integer.parseInt(placementID));
                stmt1.setString(2, tagType);
                stmt1.setInt(3, adServer);
                rs1 = stmt1.executeQuery();

                if (rs1.next()) {
                    redTag.setStatus(rs1.getString("Status"));
                    redTag.setModifyByName(rs1.getString("ModifyByName"));
                    redTag.setLastupdatedDate(
                            TimeUtil.timeStampToString(rs1.getTimestamp("LastModDate"), "yyyy-MM-dd HH:mm:ss"));
                }

                return redTag;
            } else {
                return redTag;
            }
        } catch (SQLException e) {
            logger.error("getSingRedTag2 error--redirectTag: " + placementID, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs1 != null) {
                    rs1.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getSingRedTag2 error--redirectTag: " + placementID, e);
            }
        }
        return null;
    }

    public List<RedirectTag2> getRedTagsByPlacementId2(long placementID, int adServer) {
        List<RedirectTag2> redTagList = new ArrayList<RedirectTag2>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;

        String sql = "SELECT RPRedTag.PlacementID, RPRedTag.TagType, RPRedTag.TagValue, RPRedTag.Status,"
                + "RPRedTag.CreateDate, RPRedTag.LastModDate, RPRedTag.TagDescription FROM RPRedTag where RPRedTag"
                + ".PlacementId=? and RPRedTag.Adserver =? ";

        String sql2 = "select top(1) ModifyByName from RPLog where PlacementID=? and tagType=? and Adserver=? order "
                + "by LastModDate desc;";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, (int) placementID);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            while (rs.next()) {

                RedirectTag2 redTag = new RedirectTag2();
                if (adServer == 2) {
                    if (rs.getString("TagType").toUpperCase().equals("SCRIPT")) {
                        redTag.setTagType("Javascript");
                    } else if (rs.getString("TagType").toUpperCase().equals("CREATEIFRAME")) {
                        redTag.setTagType("CreateIframe");
                    } else if (rs.getString("TagType").toUpperCase().equals("IFRAME")) {
                        redTag.setTagType("Iframe");
                    }
                } else {
                    redTag.setTagType(rs.getString("TagType"));
                }

                redTag.setTagValue(rs.getString("TagValue"));
                redTag.setStatus(rs.getString("Status"));
                redTag.setCreatedDate(rs.getDate("CreateDate"));
                redTag.setLastupdatedDate(rs.getTimestamp("LastModDate"));
                redTag.setTagDescription(rs.getString("TagDescription"));

                stmt2 = con.prepareStatement(sql2);
                stmt2.setInt(1, (int) placementID);
                stmt2.setString(2, rs.getString("TagType"));
                stmt2.setInt(3, adServer);
                rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    redTag.setModifyByName(rs2.getString("ModifyByName"));
                }

                redTagList.add(redTag);
            }
        } catch (SQLException e) {
            logger.error("getRedTagsByPlacementId2 error--redirectTag: " + placementID, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs2 != null) {
                    rs2.close();
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
                logger.error("getRedTagsByPlacementId2 error--redirectTag: " + placementID, e);
            }
        }
        return redTagList;
    }
}
