package utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.dfareporting.DfareportingScopes;
import com.google.common.collect.ImmutableList;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

public class OAuthUtil {

    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
            ".store/dfareporting_sample");//to change

    private static FileDataStoreFactory dataStoreFactory;

    private static HttpTransport httpTransport;

    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static GoogleClientSecrets clientSecrets = null;

    /** The scopes used by the DFA Reporting and Trafficking API **/
    private static final List<String> SCOPES = ImmutableList
            .of(DfareportingScopes.DFAREPORTING, DfareportingScopes.DFATRAFFICKING);

    public static GoogleClientSecrets getClientCredential() throws IOException {
        if (clientSecrets == null) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(OAuthUtil.class.getResourceAsStream("/client_secrets.json")));
            Preconditions.checkArgument(
                    !clientSecrets.getDetails().getClientId().startsWith("Enter ") && !clientSecrets.getDetails()
                            .getClientSecret().startsWith("Enter "),
                    "Download client_secrets.json file from https://code.google.com/apis/console/"
                            + "?api=calendar into calendar-appengine-sample/src/main/resources/client_secrets.json");
        }
        return clientSecrets;
    }

    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, getClientCredential(), SCOPES)
                .setDataStoreFactory(dataStoreFactory).setAccessType("offline").build();
    }

}
