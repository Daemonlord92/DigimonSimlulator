package com.horrorcore;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigimonSimulator extends Application {
    private static final Logger LOGGER = Logger.getLogger(DigimonSimulator.class.getName());
    private static World world;
    private static VisualGUI gui;

    public static void main(String[] args) {
        world = World.getInstance();
        gui = VisualGUI.getInstance(world);
        LOGGER.info("Created VisualGUI instance: " + gui);

        // Launch the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the world first
            // Replace the if (!world.isInitialized()) block with:
            world.initialize();
            LOGGER.info("World initialized with sectors: " + world.getSectors());
            
            // Add Digimons
            for (int i = 0; i < 100; i++) {
                Digimon digimon = DigimonGenerator.generateRandomDigimon();
                world.addDigimon(digimon);
                LOGGER.info("Added Digimon: " + digimon.getName());
            }
            
            for(int i = 0; i < 10; i++) {
                CelestialDigimon celestialDigimon = DigimonGenerator.generateCelestialDigimon();
                world.addDigimon(celestialDigimon);
                assert celestialDigimon != null;
                LOGGER.info("Added Celestial Digimon: " + celestialDigimon.getName());
            }
            gui.initialize();
            // Initialize GUI
            gui.start(primaryStage);
    
            LOGGER.info("GUI initialized and started");

            // Start the simulation in a separate thread
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    while (true) {
                        world.simulate(gui);
                        LOGGER.info("World simulated. Current time: " + world.getTime());
                        LOGGER.info("Number of sectors: " + world.getSectors().size());
                        int totalDigimons = world.getSectors().stream()
                                                .mapToInt(sector -> sector.getDigimons().size())
                                                .sum();
                        LOGGER.info("Total Digimons: " + totalDigimons);
                        
                        // Update GUI on JavaFX Application Thread
                        Platform.runLater(() -> {
                            gui.updateWorldInfo(world);
                            gui.updateSectorInfo(world.getSectors());
                            SimulationSubject.getInstance().notifyWorldUpdate(world);
                            LOGGER.info("GUI updated");
                        });
                        
                        Thread.sleep(1000); // Increased sleep time for easier observation
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Simulation failed", e);
                }
            });
    
            // Shutdown hook
            primaryStage.setOnCloseRequest(event -> {
                LOGGER.info("Shutting down...");
                executor.shutdownNow();
                gui.shutdown();
                Platform.exit();
            });
    
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize the simulator", e);
            Platform.exit();
        }
    }
}