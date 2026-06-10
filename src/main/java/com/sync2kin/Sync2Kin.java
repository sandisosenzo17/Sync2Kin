package com.sync2kin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.auth.Credentials;

@SuppressWarnings({ "unused" })
public class Sync2Kin {
	private final String parent_folder; // Home directory of the app
	private int folder_count; // Number of watched folders
	private Map<String, String> folders; // List of all the monitored folders
	
	// Entities for the Google Drive API
	private static final String APP_NAME = "Sync2Kin";
	private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_PATH = "tokens";
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
	private static final String CREDENTIALS_PATH = "/credentials.json";
	
	public Sync2Kin() {
		this.parent_folder = "C:\\Sync2Kin";
		this.folders = new HashMap<>();
		this.folders.put("rt", parent_folder);
		this.folders.put("dv", parent_folder + "\\Devices");
		this.folders.put("gd", parent_folder + "\\GoogleDrive");
		this.folders.put("db", parent_folder + "\\DropBox");
		this.folders.put("od", parent_folder + "\\OneDrive");
	
		createFolder();
		
	}
	
	private static Credential getCredential(NetHttpTransport HTTP_Transport) {
	  
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

  // Create the folders to be monitored
	private void createFolder() {
	  
	  for(Map.Entry<String, String> folder : this.folders.entrySet()) {
	    // Parent folder will be created by subfolders so do not create it
	    if(folder.getKey().equals("rt")) continue;
	    
	    // Create the folders if they do not exist
	    Path dir = Paths.get(folder.getValue());
	    if(Files.notExists(dir)) {
	      try {
          Files.createDirectories(dir);
        } catch (IOException ioe) {
          System.out.println("Folder creation failed: " + ioe.getMessage());
        }
	    }
	    
	  }
	  
	}
	
	// Delete unwanted files from the folders to reduce file duplication in storage
	public void deleteFile(Path file_path) {
	  try {
      Files.deleteIfExists(file_path);
    } catch (IOException ioe) {
      System.out.println("Error deleting a file: " + ioe.getMessage());
    }
	}
	
	
	public static void main(String[] args) {
	  Sync2Kin sk = new Sync2Kin();
	  List<Path> dir = new ArrayList<>();
	  
	  // Populate the List with directory names
    for(Map.Entry<String, String> folder : sk.folders.entrySet()) {
      dir.add(Paths.get(folder.getValue()));
    }
    
    //Authorize a user for the Drive API
    final NetHttpTransport HTTP_TRANSPORT;
    Drive service;
    
    HTTP_TRANSPORT = new NetHttpTransport();
    service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, Sync2Kin.getCredential(HTTP_TRANSPORT))
        .setApplicationName(APP_NAME)
        .build();
    
    try(WatchService watchservice = FileSystems.getDefault().newWatchService()){
      if(!dir.isEmpty()) {
        
        // Allow each directory in the list to be monitored
        for(Path path : dir) {
          path.register(watchservice, 
              StandardWatchEventKinds.ENTRY_MODIFY,
              StandardWatchEventKinds.ENTRY_CREATE);
        }
      }else {
        System.out.println("List of directories is empty.");
      }
      
      System.out.println("Application started...");
      
      while(true) {
        try {
          // Gather specified changes to the monitored folders
          WatchKey key = watchservice.take();
          
          System.out.println(LocalDateTime.now());
          
          // Process all events in the watchkey
          for(WatchEvent<?> event : key.pollEvents()) {
            if(event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
            
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            
            // Track the file path with changes
            Path file_info = (Path)key.watchable();
            
            // Obtain the name of the event, and the affected file with its size
            // The name of the event is obtained wirh kind().name() and the file name with context()
            System.out.println("[" + ev.kind().name() + "] "
                                + ev.context() + " "
                                + Files.size(file_info.resolve((Path) ev.context())) + "B");
          }
          
          if(!key.reset()) break;
          
        } catch (InterruptedException ie) {
          System.out.println("WatchService interrupted: " + ie.getMessage());
        }
      }
      
    } catch (IOException ioe) {
      System.out.println("WatchService initialisation error: " + ioe.getMessage());
    }
  }


}
