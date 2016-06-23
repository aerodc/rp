package models;

import java.util.List;

public class Site2 {

    private int siteId;

    private String siteName;

    private List<Redirect2> redirectList;

    public Site2(int siteId, String siteName, List<Redirect2> redirectList) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.redirectList = redirectList;
    }

    public Site2() {
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<Redirect2> getRedirectList() {
        return redirectList;
    }

    public void setRedirectList(List<Redirect2> redirectList) {
        this.redirectList = redirectList;
    }

}
