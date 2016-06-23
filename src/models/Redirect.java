package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Redirect implements Serializable {

    private static final long serialVersionUID = 1L;

    private long camId;

    private long placementId;

    private String placementName;

    private String placementSize;

    private String siteName;

    private int siteId;

    private String date_begin;

    private String date_end;

    private boolean archived;

    private String status;

    private String date_last_modify;

    private String description;

    private List<RedirectTag> redTagList;

    private int adServer;

    private String login;

    private boolean show2;//filter or not at front

    private Map<String, RedirectTag> redirectTagMap;

    public Redirect() {
        redTagList = new ArrayList<>();
        redirectTagMap = new HashMap<>();
    }

    public Redirect(long camId, long placementId, String placementName, String placementSize, String siteName,
            String date_begin, String date_end, boolean archived, String status, String date_last_modify,
            String description, List<RedirectTag> redTagList, boolean show2) {
        super();
        this.camId = camId;
        this.placementId = placementId;
        this.placementName = placementName;
        this.placementSize = placementSize;
        this.siteName = siteName;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.archived = archived;
        this.status = status;
        this.date_last_modify = date_last_modify;
        this.description = description;
        this.redTagList = redTagList;
        this.show2 = show2;
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

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getPlacementSize() {
        return placementSize;
    }

    public void setPlacementSize(String placementSize) {
        this.placementSize = placementSize;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getDate_begin() {
        return date_begin;
    }

    public void setDate_begin(String date_begin) {
        this.date_begin = date_begin;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_last_modify() {
        return date_last_modify;
    }

    public void setDate_last_modify(String date_last_modify) {
        this.date_last_modify = date_last_modify;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RedirectTag> getRedTagList() {
        return redTagList;
    }

    public void setRedTagList(List<RedirectTag> redTagList) {
        this.redTagList = redTagList;
    }

    public int getAdServer() {
        return adServer;
    }

    public void setAdServer(int adServer) {
        this.adServer = adServer;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isShow2() {
        return show2;
    }

    public void setShow2(boolean show2) {
        this.show2 = show2;
    }

    public void addRedirectTag(RedirectTag redirectTag) {
        if (this.redTagList == null) {
            this.redTagList = new ArrayList<>();
        }
        this.redTagList.add(redirectTag);
    }

    public Map<String, RedirectTag> getRedirectTagMap() {
        return redirectTagMap;
    }

    public void setRedirectTagMap(Map<String, RedirectTag> redirectTagMap) {
        this.redirectTagMap = redirectTagMap;
    }

    public RedirectTag getTagByType(String type) {
        return redirectTagMap.get(type);
    }

}