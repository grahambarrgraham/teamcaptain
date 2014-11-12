package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

@Configuration
public class GoogleAuthorisation {

    private static final String APPLICATION_NAME = "Actorspace Limited-TeamCaptain/0.1";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".store/calendar_sample");

    private static final JsonFactory JSON_FACTORY = JacksonFactory
            .getDefaultInstance();

    @Bean
    public Calendar googleCalendarClient() throws GeneralSecurityException,
            IOException, Exception {
        return new Calendar.Builder(httpTransport(), JSON_FACTORY,
                authorizeCalendarApi()).setApplicationName(APPLICATION_NAME)
                .build();
    }

    private HttpTransport httpTransport() throws GeneralSecurityException,
            IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    private DataStoreFactory dataStoreFactory() throws IOException {
        return new FileDataStoreFactory(DATA_STORE_DIR);
    }

    private Credential authorizeCalendarApi() throws IOException,
            GeneralSecurityException {
        return authoriseApp(createAuthorisationFlow(
                getClientSecrets("/client_secrets.json"),
                CalendarScopes.CALENDAR));
    }

    private GoogleClientSecrets getClientSecrets(String clientSecretFile)
            throws IOException {
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(this.getClass()
                        .getResourceAsStream(clientSecretFile)));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret()
                        .startsWith("Enter ")) {
            throw new RuntimeException(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                            + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
        }
        return clientSecrets;
    }

    private GoogleAuthorizationCodeFlow createAuthorisationFlow(
            final GoogleClientSecrets clientSecrets, String calendarScope)
            throws IOException, GeneralSecurityException {
        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport(), JSON_FACTORY, clientSecrets,
                Collections.singleton(calendarScope)).setDataStoreFactory(
                dataStoreFactory()).build();
        return flow;
    }

    private Credential authoriseApp(final GoogleAuthorizationCodeFlow flow)
            throws IOException {
        return new AuthorizationCodeInstalledApp(flow,
                new LocalServerReceiver()).authorize("user");
    }

}
