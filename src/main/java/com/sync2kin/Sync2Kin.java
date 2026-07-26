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
import com.sync2kin.helper.Helper;
import com.sync2kin.cloud.Authentication;

/**
 * Application enables user to share files amongst their connected and registered devices on the app and automatically sync files to registered cloud storages
 * @author Sandiso S. Yali
 */
@SuppressWarnings({ "unused" })
public class Sync2Kin {
	private final String parent_folder; // Home directory of the app
	private int folder_count; // Number of watched folders
	private Map<String, String> folders; // List of all the monitored folders
	
	/**
	 * Initialize the application
	 */
	public Sync2Kin() {
		this.parent_folder = "C:\\Sync2Kin";
		this.folders = new HashMap<>();
		this.folders.put("rt", parent_folder);
		this.folders.put("dv", parent_folder + "\\Devices");
		this.folders.put("gd", parent_folder + "\\GoogleDrive");
		this.folders.put("db", parent_folder + "\\DropBox");
		this.folders.put("od", parent_folder + "\\OneDrive");
	
		Helper.createFolder(this.folders);
		
	}
	
	public static void main(String[] args) {
	  Sync2Kin sk = new Sync2Kin();
	  List<Path> dir = new ArrayList<>();
	  
	  // Populate the List with directory names
    for(Map.Entry<String, String> folder : sk.folders.entrySet()) {
      dir.add(Paths.get(folder.getValue()));
    }
    
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
      System.out.println("Starting authentication...");
      
      new Authentication();
      
      System.out.println("WatchService running...");
      
      while(true) {
        try {
          // Gather specified changes to the monitored folders
          WatchKey key = watchservice.take();
          
          // System.out.println(LocalDateTime.now());
          
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
