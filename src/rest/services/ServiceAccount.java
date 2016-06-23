package rest.services;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.DfareportingScopes;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceAccount {

    private static final Logger logger = Logger.getLogger(ServiceAccount.class);

    private static ResourceBundle doubleClickRB = ResourceBundle.getBundle("properties.doubleclick");
    //private static final String PATH_TO_P12_FILE = doubleClickRB.getString("pathToP12File");
    private static final String PATH_JSON_FILE = doubleClickRB.getString("pathToJsonFile");
    private static final String JSON_FILE = doubleClickRB.getString("jsonFile");
    //private static final String SERVICE_ACCOUNT_EMAIL = doubleClickRB.getString("serviceAccountEmail");
    //private static final String ACCOUNT_TO_IMPERSONATE = doubleClickRB.getString("accountToImpersonate");
    private static final String APPLICATION_NAME = doubleClickRB.getString("applicationName");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;

    /** The scopes used by the DFA Reporting and Trafficking API **/
    private static final List<String> SCOPES = ImmutableList
            .of(DfareportingScopes.DFAREPORTING, DfareportingScopes.DFATRAFFICKING);

    //	private static Credential getServiceAccountCredential(HttpTransport httpTransport, JsonFactory factory) throws
    // Exception {
    //
    //		//System.out.println(PATH_TO_P12_FILE);
    //		if (!new File(PATH_TO_P12_FILE).exists())
    //			throw new FileNotFoundException(
    //					"Le fichier de chiffrement P12 n'existe pas !!!");
    //
    //		// Service account credential.
    //		GoogleCredential credential = new GoogleCredential.Builder()
    //				.setTransport(httpTransport)
    //				.setJsonFactory(factory)
    //				.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
    //				.setServiceAccountScopes(Collections.singleton(DfareportingScopes.DFATRAFFICKING))
    //				.setServiceAccountPrivateKeyFromP12File(new File(PATH_TO_P12_FILE))
    //				.setServiceAccountUser(ACCOUNT_TO_IMPERSONATE).build();
    //
    //		credential.refreshToken();
    //		return credential;
    //	}

    //	    public static Dfareporting getServiceAccount()
    //	throws GeneralSecurityException, IOException, Exception {
    //HttpTransport httpTransport = GoogleNetHttpTransport
    //		.newTrustedTransport();
    //JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    //
    //// Build service account credential.
    //Credential credential = getServiceAccountCredential(httpTransport,
    //		jsonFactory);
    //
    //Dfareporting reporting = new Dfareporting.Builder(httpTransport,
    //		jsonFactory, credential).setApplicationName(APPLICATION_NAME)
    //		.build();
    //return reporting;
    //}

    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets
                .load(JSON_FACTORY, new InputStreamReader(new FileInputStream(new File(JSON_FILE))));

        if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret()
                .startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from " + "https://console.developers.google.com/project into "
                            + "dfareporting-java-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(dataStoreFactory).build();

        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Dfareporting getServiceAccount() {
        java.io.File DATA_STORE_DIR = new java.io.File(PATH_JSON_FILE);
        Credential credential = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            credential = authorize();
        } catch (Exception e) {
            logger.error("getServiceAccount error", e);
        }

        return new Dfareporting.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Credential getCredentialByToken(String token) {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);

        return credential;
    }

    public static Dfareporting getServiceAccount2(String token) {
        HttpTransport httpTransport;
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Dfareporting reporting = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentialByToken(token);
            reporting = new Dfareporting.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("getServiceAccount2 error--token: " + token, e);
        }

        return reporting;
    }

    public static String getDCMUrl() {
        String endpoint = doubleClickRB.getString("dcm.endpoint");
        String scope = doubleClickRB.getString("dcm.scope");
        String redirect_uri = doubleClickRB.getString("dcm.redirect_uri");
        String clientId = doubleClickRB.getString("clientId.datait");

        String resultUrl = endpoint + "?scope=" + scope + "&state=security_token&redirect_uri=" + redirect_uri
                + "&response_type=code&client_id=" +
                clientId + "&approval_prompt=auto&login_hint=emailaddress&access_type=online";

        return resultUrl;
    }

}
