package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Site implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int siteId;

    private String siteName;

    private List<Redirect> redirectList;

    public Site(int siteId, String siteName, List<Redirect> redirectList) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.redirectList = redirectList;
    }

    public Site() {
    }

    public Site(int siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.redirectList = new ArrayList<>();
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

    public List<Redirect> getRedirectList() {
        return redirectList;
    }

    public void setRedirectList(List<Redirect> redirectList) {
        this.redirectList = redirectList;
    }

    public void addRedirect(Redirect redirect) {
        if (this.redirectList == null) { this.redirectList = new ArrayList<>(); }
        this.redirectList.add(redirect);
    }
}
