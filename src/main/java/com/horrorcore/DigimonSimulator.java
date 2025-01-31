package com.horrorcore;

import com.horrorcore.entity.CelestialDigimon;
import com.horrorcore.entity.Digimon;
import com.horrorcore.gui.VisualGUI;
import com.horrorcore.systems.events.SimulationSubject;
import com.horrorcore.systems.lifecycle.DigimonGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigimonSimulator extends Application {
    private static final Logger LOGGER = Logger.getLogger(DigimonSimulator.class.getName());
    private static World world;
    private static VisualGUI gui;
    private Timeline guiUpdateTimeline;
    private final AtomicBoolean running = new AtomicBoolean(true);


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

            // Create a Timeline for GUI updates
            guiUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                gui.updateWorldInfo(world);
                gui.updateSectorInfo(world.getSectors());
                SimulationSubject.getInstance().notifyWorldUpdate(world);
                LOGGER.info("GUI updated");
            }));
            guiUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
            guiUpdateTimeline.play();

            // Start the simulation in a separate thread
            Thread simulationThread = getSimulationThread();

            // Shutdown hook
            primaryStage.setOnCloseRequest(event -> {
                LOGGER.info("Shutting down...");
                running.set(false);
                guiUpdateTimeline.stop();
                simulationThread.interrupt();
                gui.shutdown();
                Platform.exit();
            });
    
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize the simulator", e);
            Platform.exit();
        }
    }

    private Thread getSimulationThread() {
        Thread simulationThread = new Thread(() -> {
            try {
                while (running.get()) {
                    world.simulate(gui);
                    LOGGER.info("World simulated. Current time: " + world.getTime());
                    LOGGER.info("Number of sectors: " + world.getSectors().size());
                    int totalDigimons = world.getSectors().stream()
                                            .mapToInt(sector -> sector.getDigimons().size())
                                            .sum();
                    LOGGER.info("Total Digimons: " + totalDigimons);

                    Thread.sleep(500); // Simulation speed control
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Simulation thread interrupted", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Simulation failed", e);
            }
        });
        simulationThread.start();
        return simulationThread;
    }
}