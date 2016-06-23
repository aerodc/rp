package rest.services;

import dao.RedirectDao;
import dao.TMDao;
import models.CamTM;
import models.CampaignRP;
import models.Redirect;
import models.RedirectTag;
import models.Site;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.DBConnection;

import javax.jdo.annotations.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class TMService {

    private static final Logger logger = Logger.getLogger(TMService.class);

    private RedirectDao redirectDao;

    private TMDao tmDao;

    Redirect redirect;

    RedirectTag redirectTag;

    CampaignRP campaignRP;

    @Autowired
    public void setRedirectDao(RedirectDao redirectDao) {
        this.redirectDao = redirectDao;
    }

    @Autowired
    public void setTmDao(TMDao tmDao) {
        this.tmDao = tmDao;
    }

    public RedirectTag getSingleTagTM(String placementId, String tagType, int adServer) {

        return redirectDao.getSingRedTag(placementId, tagType, adServer);
    }

    public List<RedirectTag> getTagsByPlacementTM(String placementId, int adServer) {
        //TODO gérer l'exception
        try {
            return redirectDao.getRedTagsByPlacementId(Long.parseLong(placementId), adServer);
        } catch (NumberFormatException | SQLException e) {
            logger.error("getSingleTagTM error--placementId: " + placementId, e);
        }
        return null;
    }

    public CampaignRP getPlacementByCampaignTM(String camId, int adServer) {

        CampaignRP campaignRP = redirectDao.getAllRedirectsByCamId(Long.parseLong(camId), adServer);

        if (campaignRP == null) {
            campaignRP = new CampaignRP();
        }

        return campaignRP;
    }

    @Transactional
    public synchronized boolean saveSingleTagTM(RedirectTag redirectTag, Redirect redirect) {

        return redirectDao.saveSingleRedTag(redirectTag, redirect);
    }

    @Transactional
    public void savePlacementTM(Redirect redirect) {

        redirectDao.savePlacementRed(redirect);
    }

    @Transactional
    public void savePlacementtagsTM(Redirect redirect) {
        redirectDao.savePlacementAllReds(redirect);
    }

    @Transactional
    public Redirect validPlacementTM(Redirect redirect) {
        return redirectDao.validPlacementTM(redirect);
    }

    @Transactional
    public Site validSiteTM(Site site) {

        List<Redirect> redirectList = new ArrayList<Redirect>();
        for (Redirect redirect : site.getRedirectList()) {
            if (redirect.isArchived() == true || redirect.isShow2() == false) {
                redirectList.add(redirect);
            } else {
                redirectList.add(redirectDao.validPlacementTM(redirect));
            }
        }

        site.setRedirectList(redirectList);

        return site;
    }

    @Transactional
    public Site validSiteTM2(Site site) {

        Connection connection = DBConnection.getDBConnection();
        List<Redirect> redirectList = new ArrayList<Redirect>();
        List<Future<Redirect>> futures = new ArrayList<Future<Redirect>>();

        for (Redirect redirect : site.getRedirectList()) {
            futures.add(redirectDao.validPlacementTM2(redirect, connection));
        }

        for (Future<Redirect> future : futures) {
            try {
                redirectList.add(future.get());
            } catch (ExecutionException | InterruptedException e) {
                logger.error("validSiteTM2 error--site: " + site.getSiteId(), e);
            }
        }

        try {
            if (connection != null) { connection.close(); }
        } catch (SQLException e) {
            logger.error("validSiteTM2 error--site: " + site.getSiteId(), e);
        }

        site.setRedirectList(redirectList);

        return site;
    }

    @Transactional
    public synchronized void saveCamTM(CampaignRP campaignRP) {
        int adServer = 1;
        if (campaignRP.getAdServer().equalsIgnoreCase("DCM")) { adServer = 1; } else if (campaignRP.getAdServer().equalsIgnoreCase("SIZMEK")) {
            adServer = 2;
        }

        if (redirectDao.getAllRedirectsByCamId(campaignRP.getCamId(), adServer) == null) {
            redirectDao.saveAllReds(campaignRP);
        }
    }

    public CamTM getCamTM(int camDCMId) {
        return tmDao.getCamTM(camDCMId);
    }
}
