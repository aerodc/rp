package models;

public class CamWithToken {

    private String tokenValue;

    private CampaignRP campaignRP;

    public CamWithToken() {
    }

    public CamWithToken(String tokenValue, CampaignRP campaignRP) {
        this.tokenValue = tokenValue;
        this.campaignRP = campaignRP;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public CampaignRP getCampaignRP() {
        return campaignRP;
    }

    public void setCampaignRP(CampaignRP campaignRP) {
        this.campaignRP = campaignRP;
    }

}
