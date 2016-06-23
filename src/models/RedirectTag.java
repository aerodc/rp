package models;

import java.io.Serializable;
import java.util.Date;

public class RedirectTag implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long camId;
    private long placementId;
    private String tagType;
    private String tagValue;
    private String status; //validated, archived, new
    private Date createdDate;
    private String lastupdatedDate;
    private String tagDescription;
    private String modifyByName;
    private boolean toValid;
    private boolean wellSave;
    private int adServer;

    public RedirectTag() {

    }

    public RedirectTag(long placementId, String tagType, String tagValue) {

        this.placementId = placementId;
        this.tagType = tagType;
        this.tagValue = tagValue;
    }

    public RedirectTag(long camId, long placementId, String tagType, String tagValue, String status, Date createdDate,
            String lastupdatedDate, String tagDescription, String modifyByName, boolean toValid, boolean wellSave) {
        this.camId = camId;
        this.placementId = placementId;
        this.tagType = tagType;
        this.tagValue = tagValue;
        this.status = status;
        this.createdDate = createdDate;
        this.lastupdatedDate = lastupdatedDate;
        this.tagDescription = tagDescription;
        this.modifyByName = modifyByName;
        this.toValid = toValid;
        this.wellSave = wellSave;
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

    public String getLastupdatedDate() {
        return lastupdatedDate;
    }

    public void setLastupdatedDate(String lastupdatedDate) {
        this.lastupdatedDate = lastupdatedDate;
    }

    public long getCamId() {
        return camId;
    }

    public void setCamId(long camId) {
        this.camId = camId;
    }

    public long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(long placementId) {
        this.placementId = placementId;
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

    public boolean isToValid() {
        return toValid;
    }

    public void setToValid(boolean toValid) {
        this.toValid = toValid;
    }

    public boolean isWellSave() {
        return wellSave;
    }

    public void setWellSave(boolean wellSave) {
        this.wellSave = wellSave;
    }

    public int getAdServer() {
        return adServer;
    }

    public void setAdServer(int adServer) {
        this.adServer = adServer;
    }

}
