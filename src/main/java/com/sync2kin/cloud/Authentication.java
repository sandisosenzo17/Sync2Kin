package com.sync2kin.cloud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.sync2kin.Sync2Kin;

public class Authentication {
  private static final String APP_NAME = "Sync2Kin";
  private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_PATH = "tokens";
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
  private static final String CREDENTIALS_PATH = "/credentials.json";
  private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  public Authentication() {
    new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(HTTP_TRANSPORT))
        .setApplicationName(APP_NAME)
        .build();
  }

  /**
   * Get the Google Credentials for authentication
   * @param HTTP_Transport Data transportation
   * @return Return the data from the local Credential file
   */
  private Credential getCredential(NetHttpTransport HTTP_Transport) {
    
    InputStream input = null;
    GoogleClientSecrets secrets = null;
    GoogleAuthorizationCodeFlow authorization_flow = null;
    LocalServerReceiver receiver = null;
    Credential credential = null;
    
    try {
      input = Sync2Kin.class.getResourceAsStream(CREDENTIALS_PATH);
      secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(input));
      
      authorization_flow = new GoogleAuthorizationCodeFlow
          .Builder(HTTP_Transport, JSON_FACTORY, secrets, SCOPES)
          .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_PATH)))
          .setAccessType("offline")
          .build();
      
      receiver = new LocalServerReceiver.Builder().setPort(8888).build();
      credential = new AuthorizationCodeInstalledApp(authorization_flow, receiver)
          .authorize("user");
      
    } catch(NullPointerException npe) {
      System.out.println("Credentials not accessible: " + npe.getMessage());
    } catch(IOException ioe) {
      System.out.println("Credentials Secrets are not readable: " + ioe.getMessage());
    }
    
    return credential;

  }
  
}
