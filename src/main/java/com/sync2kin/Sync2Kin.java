package com.sync2kin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
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
          
          System.out.println(LocalDateTime.now());
          
          // Process all events in the watchkey
          for(WatchEvent<?> event : key.pollEvents()) {
            if(event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
            
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            
            // Track the file with changes and its path
            Path file_info = (Path)key.watchable();
            
            // Obtain the name of the event, and the affected file with its size
            System.out.println("[" + ev.kind().name() + "] "
                                + ev.context() + " "
                                + Files.size(file_info.resolve(ev.context())) + "B");
          }
          
          if(!key.reset()) break;
          
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
