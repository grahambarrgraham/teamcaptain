package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.inject.Inject;

import org.rrabarg.teamcaptain.config.JavaUtilLoggingBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.util.AuthenticationException;

@EnableAspectJAutoProxy
@Configuration
public class GoogleAuthorisation {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String CONTACTS_API_SCOPE = "https://www.google.com/m8/feeds";

    public static final String APPLICATION_NAME = "Actorspace Limited-TeamCaptain/0.1";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".store/googleapicredential1");

    private static final JsonFactory JSON_FACTORY = JacksonFactory
            .getDefaultInstance();

    private final String[] scopes = new String[] {
            CalendarScopes.CALENDAR, CONTACTS_API_SCOPE
    };

    @Inject
    JavaUtilLoggingBridgeConfiguration julBridge; // ensure configured

    @Bean
    public ContactsService googleContactsClient() throws IOException, GeneralSecurityException, AuthenticationException {
        final ContactsService contactsService = new ContactsService(APPLICATION_NAME);
        contactsService.setOAuth2Credentials(googleApiCredential());
        return contactsService;
    }

    @Bean
    public Calendar googleCalendarClient() throws GeneralSecurityException,
            IOException, Exception {
        final Credential googleApiCredential = googleApiCredential();
        return new Calendar.Builder(httpTransport(), JSON_FACTORY,
                googleApiCredential).setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    public Credential googleApiCredential() throws IOException, GeneralSecurityException {
        return authorizeApis(scopes);
    }

    private HttpTransport httpTransport() throws GeneralSecurityException,
            IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public DataStoreFactory dataStoreFactory() throws IOException {
        return new FileDataStoreFactory(DATA_STORE_DIR);
    }

    private Credential authorizeApis(String... scopes) throws IOException,
            GeneralSecurityException {
        return authoriseApp(createAuthorisationFlow(
                getClientSecrets("/client_secrets.json"),
                scopes));
    }

    private GoogleClientSecrets getClientSecrets(String clientSecretFile)
            throws IOException {
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(this.getClass()
                        .getResourceAsStream(clientSecretFile)));

        final Details details = clientSecrets.getDetails();

        log.debug("Client secrets are auth uri: " + details.getAuthUri() + " client id " + details.getClientId()
                + " token uri " + details.getTokenUri() + " secret " + details.getClientSecret());

        if (details.getClientId().startsWith("Enter")
                || details.getClientSecret()
                        .startsWith("Enter ")) {
            throw new RuntimeException(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                            + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
        }
        return clientSecrets;
    }

    private GoogleAuthorizationCodeFlow createAuthorisationFlow(
            final GoogleClientSecrets clientSecrets, String... scopes)
            throws IOException, GeneralSecurityException {
        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport(), JSON_FACTORY, clientSecrets,
                Arrays.asList(scopes))
                .setDataStoreFactory(
                        dataStoreFactory())
                .setAccessType("offline")
                .addRefreshListener(
                        new DataStoreCredentialRefreshListener(clientSecrets.getDetails().getClientId(),
                                dataStoreFactory())).build();
        return flow;
    }

    private Credential authoriseApp(final GoogleAuthorizationCodeFlow flow)
            throws IOException {
        return new AuthorizationCodeInstalledApp(flow,
                new LocalServerReceiver()).authorize("user");
    }

}
