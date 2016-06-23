package rest.services;

import dao.UserProfileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    UserProfileDao userProfileDao;

    @Autowired
    public void setUserProfileDao(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public int getUserProfileIdByResId(int reseauId) {

        return userProfileDao.getUserProfileId(reseauId);
    }

}
