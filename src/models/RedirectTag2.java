package models;

import java.io.Serializable;
import java.util.Date;

public class RedirectTag2 implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String tagType;
    private String tagValue;
    private String status; //validated, archived, new
    private Date createdDate;
    private Date lastupdatedDate;
    private String tagDescription;
    private String modifyByName;
    private int siteId;
    private int placementId;

    public RedirectTag2(String tagType, String tagValue, String status, Date createdDate, Date lastupdatedDate,
            String tagDescription, String modifyByName) {
        this.tagType = tagType;
        this.tagValue = tagValue;
        this.status = status;
        this.createdDate = createdDate;
        this.lastupdatedDate = lastupdatedDate;
        this.tagDescription = tagDescription;
        this.modifyByName = modifyByName;
    }

    public RedirectTag2() {
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastupdatedDate() {
        return lastupdatedDate;
    }

    public void setLastupdatedDate(Date lastupdatedDate) {
        this.lastupdatedDate = lastupdatedDate;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public String getModifyByName() {
        return modifyByName;
    }

    public void setModifyByName(String modifyByName) {
        this.modifyByName = modifyByName;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getPlacementId() {
        return placementId;
    }

    public void setPlacementId(int placementId) {
        this.placementId = placementId;
    }

}
