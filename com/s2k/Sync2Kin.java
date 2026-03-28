package com.s2k;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Sync2Kin {
	private String parent_folder; // Home directory of the app
	private int folder_count; // Number of watched folders
	private Map<String, String> folders; // List of all the monitored folders
	
	// Create the folders to be monitored
	public Sync2Kin() {
		this.parent_folder = "C:\\Sync2Kin";
		this.folders = new HashMap<>();
		this.folders.put("gd", parent_folder + "\\GDrive");
		this.folders.put("db", parent_folder + "\\DBox");
		this.folders.put("nt", parent_folder + "\\Network");
	}
	
	
	public static void main(String[] args) {
	  Sync2Kin sk = new Sync2Kin();
	  List<Path> dir = new ArrayList<>();
	  
	  // Populate the List with directories
    for(Map.Entry<String, String> folder : sk.folders.entrySet()) {
      dir.add(Paths.get(folder.getValue()));
    }
    
    try(WatchService ws = FileSystems.getDefault().newWatchService()){
      if(!dir.isEmpty()) {
        
        // Allow each directory in the list to be monitored
        for(Path path : dir) {
          path.register(ws, 
              StandardWatchEventKinds.ENTRY_MODIFY,
              StandardWatchEventKinds.ENTRY_CREATE);
          
          System.out.println("Watching: " + path.toAbsolutePath());
        }
      }else {
        System.out.println("List of directories is empty.");
      }
      
      while(true) {
        try {
          // Gather specified changes to the monitored folders
          WatchKey key = ws.take();
          
          // Process all events in the watchkey
          for(WatchEvent<?> event : key.pollEvents()) {
            if(event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
            
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;k
          }
          
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

	private String createFolder(String f_name) {
		
		
		return "";
	}
}
