package dao;

import models.CamMin;
import models.CamUpdate;
import models.CampaignRP;
import models.CampaignRP2;
import models.Log;
import models.Redirect;
import models.Redirect2;
import models.RedirectTag;
import models.RedirectTag2;
import models.Site;
import models.Site2;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import rest.services.LogService;
import rest.services.RedirectService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component("RedirectDao")
@EnableAsync
public class RedirectDao {

    @Autowired
    LogService logService;

    @Autowired
    RedirectService redirectService;

    @Autowired
    RedirectTagDao redirectTagDao;

    private static final Logger logger = Logger.getLogger(RedirectDao.class);

    @Transactional
    public synchronized boolean saveSingleRedTag(RedirectTag redirectTag, Redirect redirect) {

        if (redTagExist(redirect.getPlacementId(), redirectTag.getTagType(), redirect.getAdServer())) {
            updateSingleRedTag(redirectTag);

        } else {

            insertSingleRedTag(redirectTag);
        }

        if (!placementExist(redirect.getPlacementId())) {
            savePlacementRed(redirect);
        }

        updatePlacementLastModDate(redirectTag.getPlacementId(), redirectTag.getAdServer());
        updateCamLastModDate(redirectTag.getCamId(), redirectTag.getAdServer());

        return true;
    }

    @Transactional
    public boolean saveSingleRedTag2(RedirectTag redirectTag, Redirect redirect) {

        if (redTagExist(redirect.getPlacementId(), redirectTag.getTagType(), redirect.getAdServer())) {
            updateSingleRedTag(redirectTag);

        } else {
            insertSingleRedTag(redirectTag);
        }

        return true;
    }

    public synchronized void insertSingleRedTag(RedirectTag redirectTag) {
        Connection con = null;
        PreparedStatement stmt = null;

        String sqlSaveRedTag = "insert into RPRedTag(PlacementID,TagType,TagValue,Status,LastModDate,TagDescription,"
                + "Adserver) values (?,?,?,?,?,?,?)";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
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

        } catch (SQLException e) {
            logger.error("insertSingleRedTag error: " + redirectTag, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("insertSingleRedTag error: " + redirectTag, e);
            }

        }
    }

    public synchronized void updateSingleRedTag(RedirectTag redirectTag) {
        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdateRedTag = "update RPRedTag set TagValue=?, Status=?, LastModDate=?, TagDescription=? where "
                + "PlacementID=? and TagType=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
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

        } catch (SQLException e) {
            logger.error("updateSingleRedTag error: " + redirectTag, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updateSingleRedTag error: " + redirectTag, e);
            }

        }
    }

    public RedirectTag getSingRedTag(String placementID, String tagType, int adServer) {
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
            logger.error("getSingRedTag error--placementId: " + placementID + " tageType: " + tagType, e);
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
                logger.error("getSingRedTag error--placementId: " + placementID + " tageType: " + tagType, e);
            }
        }
        return null;
    }

	/*public List<RedirectTag> getRedTagsByPlacementId(long placementID, int adServer) {
        List<RedirectTag> redTagList = new ArrayList<RedirectTag>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;

		try {
			
			String sql1= "select TagType, TagValue, CreateDate, TagDescription from RPRedTag  "
					+ "left join "
					+ "where PlacementID=? and Adserver=?";
			
			String sql2="select top(1) ModifyByName,LastModDate,Status from RPLog where PlacementID=? and tagType=?
			and Adserver=? order by LastModDate desc;";
			
			con = DBConnection.getDBConnection();
			stmt = con.prepareStatement(sql1);
			stmt.setInt(1, (int) placementID);
			stmt.setInt(2, adServer);
			rs = stmt.executeQuery();
			while (rs.next()) {

				RedirectTag redTag = new RedirectTag();
				redTag.setPlacementId(placementID);
				redTag.setTagType(rs.getString("TagType"));
				redTag.setTagValue(rs.getString("TagValue"));
				redTag.setCreatedDate(rs.getDate("CreateDate"));
				//redTag.setLastupdatedDate(TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"),"yyyy-MM-dd
				HH:mm:ss"));
				redTag.setTagDescription(rs.getString("TagDescription"));
				//redTag.setModifyByName(rs.getString("ModifyByName"));
				stmt2=con.prepareStatement(sql2);
				stmt2.setInt(1, (int) placementID);
				stmt2.setString(2, rs.getString("TagType"));
				stmt2.setInt(3, adServer);
				rs2=stmt2.executeQuery();
				if(rs2.next()){
					redTag.setStatus(rs2.getString("Status"));
					redTag.setLastupdatedDate(TimeUtil.timeStampToString(rs2.getTimestamp("LastModDate"),"yyyy-MM-dd
					HH:mm:ss"));
					redTag.setModifyByName(rs2.getString("ModifyByName"));
				}				
				
				redTagList.add(redTag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}
		return redTagList;
	}*/

    public List<RedirectTag> getRedTagsByPlacementId(long placementID, int adServer) throws SQLException {
        List<RedirectTag> redTagList = new ArrayList<RedirectTag>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            String sql1 =
                    "SELECT RPRedTag.TagType, RPRedTag.TagValue, CreateDate, TagDescription, tmp.ModifyByName, "
                            + "RPRedTag.LastModDate, RPRedTag.Status "
                            + "from RPRedTag "
                            + "left join (select top(1) * from RPLog where RPLog.PlacementID=? and  RPLog.Adserver=? "
                            + "order by tagType,LastModDate desc) as tmp on (RPRedTag.PlacementID=tmp.PlacementID and"
                            + " RPRedTag.Adserver=tmp.Adserver and RPRedTag.TagType=tmp.tagType) "
                            + "where RPRedTag.PlacementID=? and  RPRedTag.Adserver=?";

            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql1);
            stmt.setInt(1, (int) placementID);
            stmt.setInt(2, adServer);
            stmt.setInt(3, (int) placementID);
            stmt.setInt(4, adServer);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RedirectTag redTag = new RedirectTag();
                redTag.setPlacementId(placementID);
                redTag.setTagType(rs.getString("TagType"));
                redTag.setTagValue(rs.getString("TagValue"));
                redTag.setCreatedDate(rs.getDate("CreateDate"));
                //redTag.setLastupdatedDate(TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"),"yyyy-MM-dd
                // HH:mm:ss"));
                redTag.setTagDescription(rs.getString("TagDescription"));
                //redTag.setModifyByName(rs.getString("ModifyByName"));
                redTag.setStatus(rs.getString("Status"));
                redTag.setLastupdatedDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy-MM-dd HH:mm:ss"));
                redTag.setModifyByName(rs.getString("ModifyByName"));
                redTagList.add(redTag);
            }
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
                logger.error("getRedTagsByPlacementId error--placementId: " + placementID, e);
            }
        }
        return redTagList;
    }

    public boolean placementExist(long campaignID, long placementID, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT PlacementID FROM RPPlacementRed where CampaignId =? and PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(i++, (int) campaignID);
            stmt.setInt(i++, (int) placementID);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error("placementExist error--placementId: " + placementID, e);
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
                logger.error("placementExist error--placementId: " + placementID, e);
            }
        }
        return false;
    }

    public String getPlacementStatus(long campaignID, long placementID, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT Status FROM RPPlacementRed where CampaignId =? and PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(i++, (int) campaignID);
            stmt.setInt(i++, (int) placementID);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Status");
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error("getPlacementStatus error--placementId: " + placementID, e);
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
                logger.error("getPlacementStatus error--placementId: " + placementID, e);
            }
        }
        return null;
    }

    public boolean placementExist(long placementID) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT ID FROM RPPlacementRed where ID =?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(i++, (int) placementID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            logger.error("placementExist error--placementId: " + placementID, e);
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
                logger.error("placementExist error--placementId: " + placementID, e);
            }
        }
        return false;
    }

    public boolean redTagExist(long placementID, String tagType, int adServer) {

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
            logger.error("redTagExist error--placementId: " + placementID + " tagType: " + tagType, e);
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
                logger.error("redTagExist error--placementId: " + placementID + " tagType: " + tagType, e);
            }
        }
        return false;
    }

    @Transactional
    public synchronized void savePlacementAllReds(Redirect redirect) {

        savePlacementRed(redirect);

        if (redirect.getRedTagList().size() > 0) {
            for (RedirectTag redTag : redirect.getRedTagList()) {
                saveSingleRedTag(redTag, redirect);
            }
        }
    }

    @Transactional
    public synchronized Redirect validPlacementTM(Redirect redirect) {

        boolean allTagSaved = true;

        String redirectStatusOrigin = getPlacementStatus(redirect.getCamId(), redirect.getPlacementId(),
                redirect.getAdServer());

        for (int i = 0; i < redirect.getRedTagList().size(); i++) {

            redirect.getRedTagList().get(i).setWellSave(true);//initialize the save status

            if (!redirect.getRedTagList().get(i).getStatus().toUpperCase().equals("DELETE") && redirect.getRedTagList()
                    .get(i).isToValid()) {

                if (redirect.getRedTagList().get(i).getTagValue() != null
                        && redirect.getRedTagList().get(i).getTagValue().length() > 2000) {
                    redirect.getRedTagList().get(i)
                            .setTagValue(redirect.getRedTagList().get(i).getTagValue().substring(0, 1990));
                    redirect.getRedTagList().get(i).setTagDescription(
                            "Due to tag length limit, this tag value has been cut partly; " + redirect.getRedTagList()
                                    .get(i).getTagDescription());
                }

                redirect.getRedTagList().get(i).setStatus(redirect.getStatus());

                Boolean wellSave = saveSingleRedTag(redirect.getRedTagList().get(i), redirect);
                Log log = new Log(redirect.getCamId(), redirect.getSiteName(), redirect.getPlacementId(),
                        redirect.getPlacementName(), redirect.getRedTagList().get(i).getTagType(),
                        redirect.getRedTagList().get(i).getTagValue(), redirect.getStatus(), redirect.getLogin(), "",
                        redirect.getAdServer());
                logService.saveLog(log);

                RedirectTag redTagSaved = getSingRedTag(String.valueOf(redirect.getPlacementId()),
                        redirect.getRedTagList().get(i).getTagType(), redirect.getAdServer());
                redirect.getRedTagList().get(i).setLastupdatedDate(redTagSaved.getLastupdatedDate());
                redirect.getRedTagList().get(i).setStatus(redTagSaved.getStatus());
                redirect.getRedTagList().get(i).setModifyByName(redTagSaved.getModifyByName());

                if (wellSave == null || wellSave == false) {
                    redirect.getRedTagList().get(i).setWellSave(false);
                    allTagSaved = false;
                }
            }
        }

        if (allTagSaved == false) {
            String statusDefault = "NEW";
            if (redirectStatusOrigin != null) {
                statusDefault = redirectStatusOrigin;
            }
            updatePlacementStatus(redirect.getPlacementId(), statusDefault, redirect.getAdServer());
        }

        return redirect;
    }

    @Transactional
    @Async
    public Future<Redirect> validPlacementTM2(Redirect redirect, Connection connection) {

        if (redirect.isArchived() == true || redirect.isShow2() == false) {

            return new AsyncResult<Redirect>(redirect);
        } else {

            boolean allTagSaved = true;

            String redirectStatusOrigin = getPlacementStatus(redirect.getCamId(), redirect.getPlacementId(),
                    redirect.getAdServer());

            List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
            for (RedirectTag tag : redirect.getRedTagList()) {

                futures.add(redirectService.saveTag(redirect, tag, connection));
            }

            for (Future<Boolean> future : futures) {
                try {
                    if (future.get() == false) {
                        allTagSaved = false;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    logger.error("validPlacementTM2 error--redirect: " + redirect.getPlacementId(), e);
                }
            }

            if (!placementExist(redirect.getPlacementId())) {
                savePlacementRed2(redirect);
            }

            updatePlacementLastModDate2(redirect.getPlacementId(), redirect.getAdServer());
            updateCamLastModDate2(redirect.getCamId(), redirect.getAdServer());

            if (allTagSaved == false) {
                String statusDefault = "NEW";
                if (redirectStatusOrigin != null) {
                    statusDefault = redirectStatusOrigin;
                }
                redirect.setStatus(statusDefault);
                updatePlacementStatus2(redirect.getPlacementId(), statusDefault, redirect.getAdServer());
            }
            return new AsyncResult<Redirect>(redirect);
        }
    }

    public synchronized void savePlacementRed(Redirect redirect) {

        Connection con = null;
        PreparedStatement stmt = null;

        if (placementExist(redirect.getCamId(), redirect.getPlacementId(), redirect.getAdServer())) {
            updatePlacementRed(redirect);
        } else {
            String sqlSavePlacementRed = "insert into RPPlacementRed(CampaignId,PlacementID,PlacementName,"
                    + "PlacementSize,SiteName,SiteId,DateBegin,DateEnd,LastModDate,Status,Adserver) values (?,?,?,?,"
                    + "?,?,?,?,?,?,?)";

            try {
                int i = 1;
                con = DBConnection.getDBConnection();
                stmt = con.prepareStatement(sqlSavePlacementRed);
                java.sql.Date beginDate = new java.sql.Date(
                        TimeUtil.stringToDate(redirect.getDate_begin(), "yyyy-MM-dd").getTime());
                java.sql.Date endDate = new java.sql.Date(
                        TimeUtil.stringToDate(redirect.getDate_end(), "yyyy-MM-dd").getTime());
                java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
                stmt.setLong(i++, redirect.getCamId());
                stmt.setLong(i++, redirect.getPlacementId());
                stmt.setString(i++, redirect.getPlacementName());
                stmt.setString(i++, redirect.getPlacementSize());
                stmt.setString(i++, redirect.getSiteName());
                stmt.setInt(i++, redirect.getSiteId());
                stmt.setDate(i++, beginDate);
                stmt.setDate(i++, endDate);
                stmt.setTimestamp(i++, lastModifyTime);
                stmt.setString(i++, redirect.getStatus());
                stmt.setInt(i++, redirect.getAdServer());
                stmt.executeUpdate();

            } catch (SQLException e) {
                logger.error("savePlacementRed error--redirect: " + redirect.getPlacementId(), e);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    logger.error("savePlacementRed error--redirect: " + redirect.getPlacementId(), e);
                }

            }

        }

    }

    public void savePlacementRed2(Redirect redirect) {

        Connection con = null;
        PreparedStatement stmt = null;

        if (placementExist(redirect.getCamId(), redirect.getPlacementId(), redirect.getAdServer())) {
            updatePlacementRed(redirect);
        } else {
            String sqlSavePlacementRed = "insert into RPPlacementRed(CampaignId,PlacementID,PlacementName,"
                    + "PlacementSize,SiteName,SiteId,DateBegin,DateEnd,LastModDate,Status,Adserver) values (?,?,?,?,"
                    + "?,?,?,?,?,?,?)";

            try {
                int i = 1;
                con = DBConnection.getDBConnection();
                stmt = con.prepareStatement(sqlSavePlacementRed);
                java.sql.Date beginDate = new java.sql.Date(
                        TimeUtil.stringToDate(redirect.getDate_begin(), "yyyy-MM-dd").getTime());
                java.sql.Date endDate = new java.sql.Date(
                        TimeUtil.stringToDate(redirect.getDate_end(), "yyyy-MM-dd").getTime());
                java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
                stmt.setLong(i++, redirect.getCamId());
                stmt.setLong(i++, redirect.getPlacementId());
                stmt.setString(i++, redirect.getPlacementName());
                stmt.setString(i++, redirect.getPlacementSize());
                stmt.setString(i++, redirect.getSiteName());
                stmt.setInt(i++, redirect.getSiteId());
                stmt.setDate(i++, beginDate);
                stmt.setDate(i++, endDate);
                stmt.setTimestamp(i++, lastModifyTime);
                stmt.setString(i++, redirect.getStatus());
                stmt.setInt(i++, redirect.getAdServer());
                stmt.executeUpdate();

            } catch (SQLException e) {
                logger.error("savePlacementRed2 error--redirect: " + redirect.getPlacementId(), e);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    logger.error("savePlacementRed2 error--redirect: " + redirect.getPlacementId(), e);
                }

            }

        }

    }

    @Transactional
    public synchronized void updatePlacementRed(Redirect redirect) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdatePlacementRed = "update RPPlacementRed set PlacementName=?, PlacementSize=?,DateBegin=?, "
                + "DateEnd=?, LastModDate=?, Status=? where CampaignId=? and PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdatePlacementRed);
            java.sql.Date beginDate = new java.sql.Date(
                    TimeUtil.stringToDate(redirect.getDate_begin(), "yyyy-MM-dd").getTime());
            java.sql.Date endDate = new java.sql.Date(
                    TimeUtil.stringToDate(redirect.getDate_end(), "yyyy-MM-dd").getTime());
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setString(i++, redirect.getPlacementName());
            stmt.setString(i++, redirect.getPlacementSize());
            stmt.setDate(i++, beginDate);
            stmt.setDate(i++, endDate);
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setString(i++, redirect.getStatus());
            stmt.setLong(i++, redirect.getCamId());
            stmt.setLong(i++, redirect.getPlacementId());
            stmt.setInt(i++, redirect.getAdServer());
            stmt.executeUpdate();

            updateCamLastModDate(redirect.getCamId(), redirect.getAdServer());

        } catch (SQLException e) {
            logger.error("updatePlacementRed error--redirect: " + redirect.getPlacementId(), e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updatePlacementRed error--redirect: " + redirect.getPlacementId(), e);
            }

        }
    }

    public synchronized void updateCamLastModDate(long campaignId, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdateCam = "update RPCampaign set LastModDate=? where CamID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdateCam);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setLong(i++, (int) campaignId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updateCamLastModDate error--campaignId: " + campaignId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updateCamLastModDate error--campaignId: " + campaignId, e);
            }

        }
    }

    public void updateCamLastModDate2(long campaignId, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdateCam = "update RPCampaign set LastModDate=? where CamID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdateCam);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setLong(i++, (int) campaignId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updateCamLastModDate2 error--campaignId: " + campaignId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updateCamLastModDate2 error--campaignId: " + campaignId, e);
            }

        }
    }

    public synchronized void updatePlacementLastModDate(long placementId, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdatePlacement = "update RPPlacementRed set LastModDate=? where PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdatePlacement);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setLong(i++, placementId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updatePlacementLastModDate error--placementId: " + placementId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updatePlacementLastModDate error--placementId: " + placementId, e);
            }

        }
    }

    public void updatePlacementLastModDate2(long placementId, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdatePlacement = "update RPPlacementRed set LastModDate=? where PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdatePlacement);
            java.sql.Timestamp lastModifyTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt.setTimestamp(i++, lastModifyTime);
            stmt.setLong(i++, placementId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updatePlacementLastModDate2 error--placementId: " + placementId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updatePlacementLastModDate2 error--placementId: " + placementId, e);
            }

        }
    }

    public synchronized void updatePlacementStatus(long placementId, String status, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdatePlacement = "update RPPlacementRed set Status=? where PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdatePlacement);
            stmt.setString(i++, status);
            stmt.setLong(i++, placementId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updatePlacementStatus error--placementId: " + placementId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updatePlacementStatus error--placementId: " + placementId, e);
            }

        }
    }

    public void updatePlacementStatus2(long placementId, String status, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlUpdatePlacement = "update RPPlacementRed set Status=? where PlacementID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlUpdatePlacement);
            stmt.setString(i++, status);
            stmt.setLong(i++, placementId);
            stmt.setInt(i++, adServer);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("updatePlacementStatus2 error--placementId: " + placementId, e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("updatePlacementStatus2 error--placementId: " + placementId, e);
            }

        }
    }

    public List<Site> getPlacementRedByCamId(long campaignId, int adServer) {
        Map<Integer, Site> sitesMap = new HashMap<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sqlGetPlacementRed = "select PlacementID, PlacementName, PlacementSize, SiteId, SiteName, SiteId, "
                + "DateBegin, DateEnd, CreateDate, LastModDate, Status from RPPlacementRed where CampaignId=? and "
                + "Adserver=?";

        try {
            con = DBConnection.getDBConnection();

            stmt = con.prepareStatement(sqlGetPlacementRed);
            stmt.setInt(1, (int) campaignId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Site site = sitesMap.get(new Integer(rs.getInt("SiteId")));
                List<Redirect> redirectList;
                if (site == null) {
                    site = new Site();
                    site.setSiteId(rs.getInt("SiteId"));
                    site.setSiteName(rs.getString("SiteName"));
                    redirectList = new ArrayList<Redirect>();
                    site.setRedirectList(redirectList);
                }
                redirectList = site.getRedirectList();
                Redirect redirect = new Redirect();
                redirect.setCamId(campaignId);
                redirect.setPlacementId(rs.getInt("PlacementID"));
                redirect.setPlacementName(rs.getString("PlacementName"));
                redirect.setPlacementSize(rs.getString("PlacementSize"));
                redirect.setSiteName(rs.getString("SiteName"));
                redirect.setSiteId(rs.getInt("SiteId"));
                redirect.setDate_begin(rs.getDate("DateBegin").toString());
                redirect.setDate_end(rs.getDate("DateEnd").toString());
                redirect.setDate_last_modify(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy-MM-dd HH:mm:ss"));
                redirect.setRedTagList(getRedTagsByPlacementId(redirect.getPlacementId(), adServer));
                redirect.setStatus(rs.getString("Status"));
                redirect.setAdServer(adServer);
                redirectList.add(redirect);

                site.setRedirectList(redirectList);
                sitesMap.put(site.getSiteId(), site);
            }

        } catch (SQLException e) {
            logger.error("getPlacementRedByCamId error--campaignId: " + campaignId, e);
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
                logger.error("getPlacementRedByCamId error--campaignId: " + campaignId, e);
            }
        }

        return new ArrayList<Site>(sitesMap.values());

		/*String sqlSite = "select distinct SiteId,SiteName from RPPlacementRed where CampaignId=? and Adserver=?";

		String sqlGetPlacementRed = "select PlacementID, PlacementName, PlacementSize, SiteName, SiteId, DateBegin,
		DateEnd, CreateDate, LastModDate, Status from RPPlacementRed where CampaignId=? and SiteId=? and Adserver=?";

		try {

			con = DBConnection.getDBConnection();

			stmt0 = con.prepareStatement(sqlSite);
			stmt0.setInt(1, (int) campaignId);
			stmt0.setInt(2, adServer);
			rs0 = stmt0.executeQuery();

			while (rs0.next()) {
				Site site = new Site();
				site.setSiteId(rs0.getInt("SiteId"));
				site.setSiteName(rs0.getString("SiteName"));
				List<Redirect> redirectList = new ArrayList<Redirect>();

				stmt = con.prepareStatement(sqlGetPlacementRed);
				stmt.setInt(1, (int) campaignId);
				stmt.setInt(2, rs0.getInt("SiteId"));
				stmt.setInt(3, adServer);
				rs = stmt.executeQuery();

				while (rs.next()) {

					Redirect redirect = new Redirect();
					redirect.setCamId(campaignId);
					redirect.setPlacementId(rs.getInt("PlacementID"));
					redirect.setPlacementName(rs.getString("PlacementName"));
					redirect.setPlacementSize(rs.getString("PlacementSize"));
					redirect.setSiteName(rs.getString("SiteName"));
					redirect.setSiteId(rs.getInt("SiteId"));
					redirect.setDate_begin(rs.getDate("DateBegin")
							.toString());
					redirect.setDate_end(rs.getDate("DateEnd").toString());
					redirect.setDate_last_modify(TimeUtil.timeStampToString(
							rs.getTimestamp("LastModDate"),
							"yyyy-MM-dd HH:mm:ss"));
					redirect
							.setRedTagList(getRedTagsByPlacementId(redirect
									.getPlacementId(),adServer));
					redirect.setStatus(rs.getString("Status"));
					redirect.setAdServer(adServer);
					redirectList.add(redirect);
				}
				site.setRedirectList(redirectList);
				siteDCMList.add(site);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {

				if (rs0 != null) {
					rs0.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (stmt0 != null) {
					stmt0.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return siteDCMList;
		}*/

    }

    public List<Site2> getPlacementRedByCamId2(long campaignId, int adServer) {

        List<Site2> siteDCMList = new ArrayList<Site2>();
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt0 = null;
        ResultSet rs0 = null;
        ResultSet rs = null;

        String sqlSite = "select distinct SiteId,SiteName from RPPlacementRed where CampaignId=? and Adserver=? and "
                + "SiteName like '%affiperf%'";

        String sqlGetPlacementRed = "select PlacementID, PlacementName, PlacementSize, SiteName, SiteId, DateBegin, "
                + "DateEnd, CreateDate, LastModDate from RPPlacementRed where CampaignId=? and SiteId=? and "
                + "Adserver=? and Status!='OOS' and SiteName like '%affiperf%';";

        try {

            con = DBConnection.getDBConnection();

            stmt0 = con.prepareStatement(sqlSite);
            stmt0.setInt(1, (int) campaignId);
            stmt0.setInt(2, adServer);
            rs0 = stmt0.executeQuery();

            while (rs0.next()) {
                Site2 site = new Site2();
                site.setSiteId(rs0.getInt("SiteId"));
                site.setSiteName(rs0.getString("SiteName"));
                List<Redirect2> redirectList = new ArrayList<Redirect2>();

                stmt = con.prepareStatement(sqlGetPlacementRed);
                stmt.setInt(1, (int) campaignId);
                stmt.setInt(2, rs0.getInt("SiteId"));
                stmt.setInt(3, adServer);

                rs = stmt.executeQuery();

                while (rs.next()) {

                    Redirect2 redirect = new Redirect2();
                    redirect.setPlacementId(rs.getInt("PlacementID"));
                    redirect.setPlacementName(rs.getString("PlacementName"));
                    redirect.setPlacementSize(rs.getString("PlacementSize"));
                    redirect.setDate_begin(rs.getDate("DateBegin").toString());
                    redirect.setDate_end(rs.getDate("DateEnd").toString());
                    redirect.setDate_last_modify(
                            TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy-MM-dd HH:mm:ss"));
                    redirect.setRedTagList(
                            redirectTagDao.getRedTagsByPlacementId2(redirect.getPlacementId(), adServer));
                    redirectList.add(redirect);
                }
                site.setRedirectList(redirectList);
                siteDCMList.add(site);
            }

        } catch (SQLException e) {
            logger.error("getPlacementRedByCamId2 error--campaignId: " + campaignId, e);
        } finally {
            try {

                if (rs0 != null) {
                    rs0.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt0 != null) {
                    stmt0.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getPlacementRedByCamId2 error--campaignId: " + campaignId, e);
            }
        }

        return siteDCMList;
    }

    public synchronized void saveAllReds(CampaignRP campaignRP) {

        Connection con = null;
        PreparedStatement stmt = null;

        String sqlSaveCampaign = "insert into RPCampaign(CamID,CamName,AdvertiserId,NetworkId,LastModDate,BeginDate,"
                + "EndDate, Adserver) values (?,?,?,?,?,?,?,?)";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSaveCampaign);
            java.sql.Timestamp lastModifyDate = new Timestamp(Calendar.getInstance().getTimeInMillis());

            java.sql.Date beginDate = new java.sql.Date(
                    TimeUtil.stringToDate(campaignRP.getBegindate(), "yyyy-MM-dd").getTime());
            java.sql.Date endDate = new java.sql.Date(
                    TimeUtil.stringToDate(campaignRP.getEnddate(), "yyyy-MM-dd").getTime());

            stmt.setInt(i++, (int) campaignRP.getCamId());
            stmt.setString(i++, campaignRP.getCamName());
            stmt.setInt(i++, (int) campaignRP.getAdvertiserId());
            stmt.setInt(i++, (int) campaignRP.getNetworkId());
            stmt.setTimestamp(i++, lastModifyDate);
            stmt.setDate(i++, beginDate);
            stmt.setDate(i++, endDate);
            if (campaignRP.getAdServer().toUpperCase().equals("DCM")) {
                stmt.setInt(i++, 1);
            } else {
                if (campaignRP.getAdServer().toUpperCase().equals("SIZMEK")) {
                    stmt.setInt(i++, 2);
                }
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("saveAllReds error--campaignId: " + campaignRP.getCamId(), e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("saveAllReds error--campaignId: " + campaignRP.getCamId(), e);
            }

        }

    }

    // for TM Service
    public CampaignRP getAllRedirectsByCamId(long campaignId, int adServer) {
        CampaignRP campaignRP = null;

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sqlSelectCampaign = "select CamID, CamName, AdvertiserId, NetworkId, LastModDate, BeginDate, EndDate, "
                + "RetrDate from RPCampaign where CamID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSelectCampaign);
            stmt.setInt(i++, (int) campaignId);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                campaignRP = new CampaignRP();
                campaignRP.setCamId(campaignId);
                campaignRP.setCamName(rs.getString("CamName"));
                campaignRP.setAdvertiserId(rs.getLong("AdvertiserId"));
                campaignRP.setNetworkId(rs.getLong("NetworkId"));
                campaignRP.setLastModifyDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy/MM/dd HH:mm:ss"));
                campaignRP.setBegindate(rs.getDate("BeginDate").toString());
                campaignRP.setEnddate(rs.getDate("EndDate").toString());
                campaignRP.setSiteList(getPlacementRedByCamId(campaignRP.getCamId(), adServer));
                campaignRP.setLastRetriveDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("RetrDate"), "yyyy/MM/dd HH:mm:ss"));

                if (rs.getTimestamp("LastModDate") != null && rs.getTimestamp("RetrDate") != null) {
                    if (rs.getTimestamp("LastModDate").after(rs.getTimestamp("RetrDate"))) {
                        campaignRP.setHasModifToRetrive(true);
                    } else {
                        campaignRP.setHasModifToRetrive(false);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("getAllRedirectsByCamId error--campaignId: " + campaignRP.getCamId(), e);
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
                logger.error("getAllRedirectsByCamId error--campaignId: " + campaignRP.getCamId(), e);
            }

        }

        return campaignRP;
    }

    // for TM Service
    public CampaignRP2 getAllRedirectsByCampaign(long campaignId, int adServer) {
        CampaignRP2 campaignRP = new CampaignRP2();

        Connection con = null;
        PreparedStatement stmt = null, stmt2 = null;
        ResultSet rs = null;
        String sqlSelectCampaign = "select CamID, CamName, AdvertiserId, NetworkId, LastModDate, BeginDate, EndDate "
                + "from RPCampaign where CamID=? and Adserver=?";

        String updateSql = "update RPCampaign set RetrDate=? where CamID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSelectCampaign);
            stmt.setInt(i++, (int) campaignId);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                campaignRP.setCamId(campaignId);
                campaignRP.setCamName(rs.getString("CamName"));
                campaignRP.setAdvertiserId(rs.getLong("AdvertiserId"));
                campaignRP.setNetworkId(rs.getLong("NetworkId"));
                campaignRP.setLastModifyDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy/MM/dd HH:mm:ss"));
                campaignRP.setBegindate(rs.getDate("BeginDate").toString());
                campaignRP.setEnddate(rs.getDate("EndDate").toString());
                List<Site2> siteList = new ArrayList<Site2>();
                for (Site2 site : getPlacementRedByCamId2(campaignRP.getCamId(), adServer)) {
                    if (site.getSiteName().toLowerCase().indexOf("affiperf") != -1) {
                        siteList.add(site);
                    }
                }

                if (siteList.size() > 0) {

                    campaignRP.setResponseCode("OK");

                } else {
                    campaignRP.setResponseCode("KO");
                }
                campaignRP.setSiteList(siteList);
            } else {
                campaignRP.setResponseCode("KO");
            }

            if (adServer == 1) {
                campaignRP.setAdServer("DCM");
            } else {
                if (adServer == 2) {
                    campaignRP.setAdServer("SIZMEK");
                }
            }

            stmt2 = con.prepareStatement(updateSql);
            java.sql.Timestamp lastTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt2.setTimestamp(1, lastTime);
            stmt2.setInt(2, (int) campaignId);
            stmt2.setInt(3, adServer);
            stmt2.executeUpdate();

        } catch (SQLException e) {
            logger.error("getAllRedirectsByCamId error--campaignId: " + campaignRP.getCamId(), e);
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
                logger.error("getAllRedirectsByCamId error--campaignId: " + campaignRP.getCamId(), e);
            }

        }

        return campaignRP;
    }

    // for DSP Service
    public CampaignRP2 getAllRedirectsByCampaignId(long campaignId, int adServer) {
        CampaignRP2 campaignRP = new CampaignRP2();

        Connection con = null;
        PreparedStatement stmt = null, stmt2 = null;
        ResultSet rs = null;
        String sqlSelectCampaign = "select CamID, CamName, AdvertiserId, NetworkId, LastModDate, BeginDate, EndDate "
                + "from RPCampaign where CamID=? and Adserver=?";

        String updateSql = "update RPCampaign set RetrDate=? where CamID=? and Adserver=?";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSelectCampaign);
            stmt.setInt(i++, (int) campaignId);
            stmt.setInt(i++, adServer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                campaignRP.setCamId(campaignId);
                campaignRP.setCamName(rs.getString("CamName"));
                campaignRP.setAdvertiserId(rs.getLong("AdvertiserId"));
                campaignRP.setNetworkId(rs.getLong("NetworkId"));
                campaignRP.setLastModifyDate(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy/MM/dd HH:mm:ss"));
                campaignRP.setBegindate(rs.getDate("BeginDate").toString());
                campaignRP.setEnddate(rs.getDate("EndDate").toString());
            } else {
                campaignRP.setResponseCode("KO");
            }

            if (adServer == 1) {
                campaignRP.setAdServer("DCM");
            } else {
                if (adServer == 2) {
                    campaignRP.setAdServer("SIZMEK");
                }
            }

            //get redirects
            List<Site2> siteList = getSiteList((int) campaignId, adServer);

            if (siteList.size() > 0) {
                campaignRP.setResponseCode("OK");
                List<Future<Redirect2>> futures = new ArrayList<Future<Redirect2>>();

                for (Site2 site : siteList) {

                    for (Redirect2 redirect : site.getRedirectList()) {
                        futures.add(redirectService.setRedirectTag(redirect, adServer));
                    }
                }

                for (Future<Redirect2> future : futures) {
                    try {
                        future.get();
                    } catch (ExecutionException | InterruptedException e) {
                        logger.error("getAllRedirectsByCampaignId error--campaignId: " + campaignRP.getCamId(), e);
                    }
                }

                campaignRP.setSiteList(siteList);

            } else {
                campaignRP.setResponseCode("KO");
            }

            stmt2 = con.prepareStatement(updateSql);
            java.sql.Timestamp lastTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
            stmt2.setTimestamp(1, lastTime);
            stmt2.setInt(2, (int) campaignId);
            stmt2.setInt(3, adServer);
            stmt2.executeUpdate();

            return campaignRP;

        } catch (SQLException e) {
            logger.error("getAllRedirectsByCampaignId error--campaignId: " + campaignRP.getCamId(), e);
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
                logger.error("getAllRedirectsByCampaignId error--campaignId: " + campaignRP.getCamId(), e);
            }
        }

        return campaignRP;
    }

    public List<Site2> getSiteList(int campaignId, int adServer) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String getSite = "select * from RPPlacementRed where CampaignId=? and Adserver=? and Status!='OOS' and SiteName like '%affiperf%' order by SiteId";

        List<Site2> siteList = new ArrayList<Site2>();

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(getSite);
            stmt.setInt(1, campaignId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            while (rs.next()) {

                if (siteList.size() == 0 || siteList.get(siteList.size() - 1).getSiteId() != rs.getInt("SiteId")) {
                    Site2 site = new Site2();
                    site.setSiteId(rs.getInt("SiteId"));
                    site.setSiteName(rs.getString("SiteName"));
                    List<Redirect2> redirectList = new ArrayList<Redirect2>();
                    site.setRedirectList(redirectList);
                    siteList.add(site);
                }

                Redirect2 redirect = new Redirect2();
                redirect.setPlacementId(rs.getInt("PlacementID"));
                redirect.setPlacementName(rs.getString("PlacementName"));
                redirect.setPlacementSize(rs.getString("PlacementSize"));
                redirect.setDate_begin(rs.getDate("DateBegin").toString());
                redirect.setDate_end(rs.getDate("DateEnd").toString());
                redirect.setDate_last_modify(
                        TimeUtil.timeStampToString(rs.getTimestamp("LastModDate"), "yyyy-MM-dd HH:mm:ss"));

                List<RedirectTag2> redTagList = new ArrayList<RedirectTag2>();
                redirect.setRedTagList(redTagList);
                siteList.get(siteList.size() - 1).getRedirectList().add(redirect);

            }

            return siteList;

        } catch (SQLException e) {
            logger.error("getSiteList error--campaignId: " + campaignId, e);
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
                logger.error("getSiteList error--campaignId: " + campaignId, e);
            }
        }

        return null;
    }

    // for DSP Service
    public CamUpdate getCampaignsUpdated(Timestamp beginDate, Timestamp endDate) {
        CamUpdate camUpdate = new CamUpdate();
        List<CamMin> camMinList = new ArrayList<CamMin>();

        Connection con = null;
        PreparedStatement stmt = null, stmt2 = null, stmt3 = null;
        ResultSet rs = null, rs1 = null;
        String sqlSelectCampaignMin = "select CamID, CamName, Adserver from RPCampaign where LastModDate is not null and LastModDate>=? and LastModDate<=?";

        String updateSql = "update RPCampaign set RetrDate=? where CamID=?";

        String countSql = "select COUNT(*) as cnt from RPPlacementRed where Status!='OOS' and CampaignId=?;";

        try {
            int i = 1;
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlSelectCampaignMin);
            stmt.setTimestamp(i++, beginDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")));
            stmt.setTimestamp(i++, endDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")));
            rs = stmt.executeQuery();

            while (rs.next()) {
                CamMin camMin = new CamMin();

                stmt3 = con.prepareStatement(countSql);
                stmt3.setInt(1, (int) rs.getLong("CamID"));
                rs1 = stmt3.executeQuery();
                if (rs1.next()) {

                    if (rs1.getInt("cnt") > 0) {
                        camMin.setId(rs.getLong("CamID"));
                        camMin.setName(rs.getString("CamName"));
                        if (rs.getInt("Adserver") == 1) {
                            camMin.setAdserver("DCM");
                        } else if (rs.getInt("Adserver") == 2) {
                            camMin.setAdserver("SIZMEK");
                        }
                        camMinList.add(camMin);

                    }
                }

            }

            stmt2 = con.prepareStatement(updateSql);

            for (CamMin camMin : camMinList) {

                java.sql.Timestamp lastTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
                stmt2.setTimestamp(1, lastTime);
                stmt2.setInt(2, (int) camMin.getId());
                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            logger.error("getCampaignsUpdated error", e);
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
                if (stmt2 != null) {
                    stmt2.close();
                }
                if (stmt3 != null) {
                    stmt3.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("getCampaignsUpdated error", e);
            }

        }

        camUpdate.setCampaignsList(camMinList);
        camUpdate.setResponseCode("OK");
        return camUpdate;
    }

    public synchronized boolean saveCamDesc(int camId, int adServer, String desc) {
        Connection con = null;
        PreparedStatement stmt = null, stmt1 = null, stmt2 = null;
        ResultSet rs = null;

        boolean result = false;

        String sqlFind = "select ID from RPCamDesc where CamID=? and Adserver=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sqlFind);
            stmt.setInt(1, camId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String sqlUpd = "update RPCamDesc set Desc1=?,LastModDate=? where ID=?";
                stmt1 = con.prepareStatement(sqlUpd);
                stmt1.setString(1, desc);
                stmt1.setTimestamp(2, new java.sql.Timestamp((new Date()).getTime()));
                stmt1.setInt(3, rs.getInt("ID"));
                stmt1.executeUpdate();
                result = true;
            } else {
                String sqlIns = "insert into RPCamDesc (CamID, Adserver, Desc1,LastModDate) values (?,?,?,?)";
                stmt2 = con.prepareStatement(sqlIns);
                stmt2.setInt(1, camId);
                stmt2.setInt(2, adServer);
                stmt2.setString(3, desc);
                stmt2.setTimestamp(4, new java.sql.Timestamp((new Date()).getTime()));
                stmt2.executeUpdate();
                result = true;
            }
            return result;
        } catch (SQLException e) {
            logger.error("saveCamDesc error--campaignId: " + camId, e);
            return result;

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
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error("saveCamDesc error--campaignId: " + camId, e);
            }
        }

    }

    public String getCamDesc(int camId, int adServer) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String desc = " ";

        String sql = "select Desc1 from RPCamDesc where CamID=? and Adserver=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, camId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            if (rs.next()) {
                desc = rs.getString("Desc1");
            }
        } catch (SQLException e) {
            logger.error("getCamDesc error--campaignId: " + camId, e);
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
                logger.error("getCamDesc error--campaignId: " + camId, e);
            }
        }

        return desc;
    }

    public String getCamRetriveDate(int camId, int adServer) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        java.sql.Timestamp retriveDate = null;
        String returnedDate = "";

        String sql = "select RetrDate from RPCampaign where CamID=? and AdServer=?";

        try {
            con = DBConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, camId);
            stmt.setInt(2, adServer);
            rs = stmt.executeQuery();
            if (rs.next()) {
                retriveDate = rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            logger.error("getCamRetriveDate error--campaignId: " + camId, e);
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
                logger.error("getCamRetriveDate error--campaignId: " + camId, e);
            }
        }
        if (retriveDate != null) {
            returnedDate = retriveDate.toString();
        }

        return returnedDate;
    }

}