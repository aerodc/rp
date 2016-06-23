package models;

import java.io.Serializable;
import java.util.List;

public class CamUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String responseCode;

    private List<CamMin> campaigns;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public List<CamMin> getCampaignsList() {
        return campaigns;
    }

    public void setCampaignsList(List<CamMin> camMinList) {
        this.campaigns = camMinList;
    }

}
