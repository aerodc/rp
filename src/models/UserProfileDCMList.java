package models;

import java.io.Serializable;
import java.util.List;

public class UserProfileDCMList implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7053659281505747504L;

    private List<UserProfileDCM> userProfileList;

    private String token;

    public UserProfileDCMList() {
    }

    public List<UserProfileDCM> getUserProfileList() {
        return userProfileList;
    }

    public void setUserProfileList(List<UserProfileDCM> userProfileList) {
        this.userProfileList = userProfileList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
