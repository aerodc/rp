package models;

import java.io.Serializable;

public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    private long campaignId;

    private String site;

    private long placementId;

    private String placementName;

    private String tagType;

    private String tagValue;

    private String status; //validated, archived, new

    private String modifyByName;

    private String lastModifyDate;

    private int adServer;

    public Log() {

    }

    public Log(long campaignId, String site, long placementId, String placementName, String tagType, String tagValue,
            String status, String modifyByName, String lastModifyDate, int adServer) {
        this.campaignId = campaignId;
        this.site = site;
        this.placementId = placementId;
        this.placementName = placementName;
        this.tagType = tagType;
        this.tagValue = tagValue;
        this.status = status;
        this.modifyByName = modifyByName;
        this.lastModifyDate = lastModifyDate;
        this.adServer = adServer;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(long placementId) {
        this.placementId = placementId;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModifyByName() {
        return modifyByName;
    }

    public void setModifyByName(String modifyByName) {
        this.modifyByName = modifyByName;
    }

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public int getAdServer() {
        return adServer;
    }

    public void setAdServer(int adServer) {
        this.adServer = adServer;
    }

}
