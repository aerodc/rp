package models;

public class SiteWithToken {

    private String tokenTM;

    private String tokenSizmek;

    private Site site;

    private int campaignId;

    private int network;

    public String getTokenTM() {
        return tokenTM;
    }

    public void setTokenTM(String tokenTM) {
        this.tokenTM = tokenTM;
    }

    public String getTokenSizmek() {
        return tokenSizmek;
    }

    public void setTokenSizmek(String tokenSizmek) {
        this.tokenSizmek = tokenSizmek;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

}
