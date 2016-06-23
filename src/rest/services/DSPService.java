package rest.services;

import dao.RedirectDao;
import models.CamMin;
import models.CamUpdate;
import models.CampaignRP;
import models.CampaignRP2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class DSPService {

    RedirectDao redirectDao;

    CampaignRP campaignRP;

    CamMin camMin;

    @Autowired
    public void setRedirectDao(RedirectDao redirectDao) {
        this.redirectDao = redirectDao;
    }

    public CampaignRP2 getRedirectsByCampaignId(long id, int adServer) {

        //return redirectDao.getAllRedirectsByCampaign(id, adServer);
        return redirectDao.getAllRedirectsByCampaignId(id, adServer);

    }

    public CamUpdate getUpdatedCampaigns(Timestamp beginDate, Timestamp endDate) {

        return redirectDao.getCampaignsUpdated(beginDate, endDate);
    }

}
