package com.sync2kin.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Helper consists of functionalities to help the app work
 * @author Sandiso S. Yali
 */
public class Helper {
  
  /**
   * createFolder takes the list of folders to be watched and create them in the primary storage of the user device
   * @param folders List of folders to be created
   */
  public static void createFolder(Map<String, String> folders) {
    
    for(Map.Entry<String, String> folder : folders.entrySet()) {
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
  
  /**
   * Delete unwanted files from the folders
   * @param file_path Path to the file to be deleted
   */
  public static void deleteFile(Path file_path) {
    try {
      Files.deleteIfExists(file_path);
    } catch (IOException ioe) {
      System.out.println("Error deleting a file: " + ioe.getMessage());
    }
  }
  
}
