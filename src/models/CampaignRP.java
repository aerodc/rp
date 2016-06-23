package models;

import java.io.Serializable;
import java.util.List;

public class CampaignRP implements Serializable {

    private static final long serialVersionUID = 1L;

    private String responseCode;

    private long camId;

    private String camName;

    private long advertiserId;

    private long networkId;

    private String begindate;

    private String enddate;

    private String adServer;

    private String description;

    private String lastModifyDate;

    private String lastRetriveDate;

    private List<Site> siteList;

    private boolean hasModifToRetrive;

    public CampaignRP() {

        this.adServer = "DCM";
        this.description = "";
    }

    public CampaignRP(String responseCode, long camId, String camName, long advertiserId, long networkId,
            String begindate, String enddate, String lastModifyDate, String adServer, String description,
            List<Site> siteList, String lastRetriveDate) {
        super();
        this.responseCode = responseCode;
        this.camId = camId;
        this.camName = camName;
        this.advertiserId = advertiserId;
        this.networkId = networkId;
        this.begindate = begindate;
        this.enddate = enddate;
        this.adServer = adServer;
        this.description = description;
        this.lastModifyDate = lastModifyDate;
        this.siteList = siteList;
        this.lastRetriveDate = lastRetriveDate;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public long getCamId() {
        return camId;
    }

    public void setCamId(long camId) {
        this.camId = camId;
    }

    public String getCamName() {
        return camName;
    }

    public void setCamName(String camName) {
        this.camName = camName;
    }

    public long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(long networkId) {
        this.networkId = networkId;
    }

    public String getBegindate() {
        return begindate;
    }

    public void setBegindate(String begindate) {
        this.begindate = begindate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getAdServer() {
        return adServer;
    }

    public void setAdServer(String adServer) {
        this.adServer = adServer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    public String getLastRetriveDate() {
        return lastRetriveDate;
    }

    public void setLastRetriveDate(String lastRetriveDate) {
        this.lastRetriveDate = lastRetriveDate;
    }

    public boolean isHasModifToRetrive() {
        return hasModifToRetrive;
    }

    public void setHasModifToRetrive(boolean hasModifToRetrive) {
        this.hasModifToRetrive = hasModifToRetrive;
    }

}
