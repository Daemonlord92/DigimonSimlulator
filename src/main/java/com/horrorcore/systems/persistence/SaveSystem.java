package com.horrorcore.systems.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.horrorcore.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles serialization and deserialization of World state to/from JSON files.
 */
public class SaveSystem {
    private static final Logger LOGGER = Logger.getLogger(SaveSystem.class.getName());
    private static final ObjectMapper mapper = createObjectMapper();
    private static final String SAVE_DIRECTORY = "saves";
    private static final String DEFAULT_SAVE_FILE = "world_save.json";
    
    private static ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return om;
    }
    
    /**
     * Saves the current world state to a JSON file.
     * 
     * @param world The world instance to save
     * @param filename The name of the save file (without path)
     * @return true if save was successful, false otherwise
     */
    public static boolean saveToFile(World world, String filename) {
        try {
            // Ensure save directory exists
            Path savePath = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
            
            // Create snapshot
            WorldSnapshot snapshot = WorldSnapshot.fromWorld(world);
            
            // Write to file
            File saveFile = savePath.resolve(filename).toFile();
            mapper.writeValue(saveFile, snapshot);
            
            LOGGER.info("World saved successfully to " + saveFile.getAbsolutePath());
            return true;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save world to file: " + filename, e);
            return false;
        }
    }
    
    /**
     * Saves the current world state using the default filename.
     * 
     * @param world The world instance to save
     * @return true if save was successful, false otherwise
     */
    public static boolean saveToFile(World world) {
        return saveToFile(world, DEFAULT_SAVE_FILE);
    }
    
    /**
     * Loads a world state from a JSON file.
     * 
     * @param filename The name of the save file (without path)
     * @return WorldSnapshot if load was successful, null otherwise
     */
    public static WorldSnapshot loadFromFile(String filename) {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY, filename);
            
            if (!Files.exists(savePath)) {
                LOGGER.warning("Save file does not exist: " + savePath);
                return null;
            }
            
            File saveFile = savePath.toFile();
            WorldSnapshot snapshot = mapper.readValue(saveFile, WorldSnapshot.class);
            
            LOGGER.info("World loaded successfully from " + saveFile.getAbsolutePath());
            return snapshot;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load world from file: " + filename, e);
            return null;
        }
    }
    
    /**
     * Loads a world state using the default filename.
     * 
     * @return WorldSnapshot if load was successful, null otherwise
     */
    public static WorldSnapshot loadFromFile() {
        return loadFromFile(DEFAULT_SAVE_FILE);
    }
    
    /**
     * Lists all available save files.
     * 
     * @return Array of save file names, or empty array if none exist
     */
    public static String[] listSaveFiles() {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(savePath)) {
                return new String[0];
            }
            
            return Files.list(savePath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
                    
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to list save files", e);
            return new String[0];
        }
    }
    
    /**
     * Deletes a save file.
     * 
     * @param filename The name of the save file to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteSaveFile(String filename) {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY, filename);
            return Files.deleteIfExists(savePath);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to delete save file: " + filename, e);
            return false;
        }
    }
}
