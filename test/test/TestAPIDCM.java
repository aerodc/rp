import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.UserProfileList;
import rest.services.ServiceAccount;

import java.io.IOException;

public class TestAPIDCM {

    public static void main(String[] args) {
        //Dfareporting reporting = ServiceAccount.getServiceAccount2("ya29
        // .TgGwCnvjk5bKNYaHB7fjx9X1RQjR57AahmHTfusUhRmRZ88InBXnuIqwN73nuE9dLroLSYEznTD3OQ");
        //Dfareporting reporting = ServiceAccount.getServiceAccount2("ya29
        // .TgGYlHAXgT6blVRH0M2iFXeaMa2wL8zVemBsKQn9uSQzmkrJure_81KX7oE9GgEYfiOccIsZ5GhS-w");
        Dfareporting reporting = ServiceAccount.getServiceAccount2(
                "ya29.TwFhc47uJX5EDy6NGFXodFEUvg-RKHTDZNcATxCoItJeMv5wLAcpKoCL-6znm9sgHADwmGNjsDXhYw");

        try {
            UserProfileList profiles = reporting.userProfiles().list().execute();
            System.out.println(profiles.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
