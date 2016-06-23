package models;

import java.io.Serializable;

public class UserProfileDCM implements Serializable {

    private static final long serialVersionUID = 187520159590149534L;

    private long networkId;

    private String networkName;

    private long profileId;

    private String userName;

    private long subAccountId;

    private String subAccountName;

    private String token;

    private boolean isHavasOwned;

    public UserProfileDCM(long networkId, String networkName, long profileId, String userName, long subAccountId,
            String subAccountName, String token, boolean isHavasOwned) {
        this.networkId = networkId;
        this.networkName = networkName;
        this.profileId = profileId;
        this.userName = userName;
        this.subAccountId = subAccountId;
        this.subAccountName = subAccountName;
        this.token = token;
        this.isHavasOwned = isHavasOwned;
    }

    public UserProfileDCM() {
    }

    public long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(long networkId) {
        this.networkId = networkId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getSubAccountId() {
        return subAccountId;
    }

    public void setSubAccountId(long subAccountId) {
        this.subAccountId = subAccountId;
    }

    public String getSubAccountName() {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isHavasOwned() {
        return isHavasOwned;
    }

    public void setHavasOwned(boolean isHavasOwned) {
        this.isHavasOwned = isHavasOwned;
    }

}
