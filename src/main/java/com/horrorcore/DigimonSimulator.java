package com.horrorcore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigimonSimulator {
    private static final Logger LOGGER = Logger.getLogger(DigimonSimulator.class.getName());

    public static void main(String[] args) {
        World world = World.getInstance();
        VisualGUI gui = VisualGUI.getInstance(world);
        LOGGER.info("Created VisualGUI instance: " + gui);

        try {
            // Initialize the world first
            world.initialize();
            LOGGER.info("World initialized with sectors: " + world.getSectors());

            // Then initialize GUI
            gui.initialize();

            // Add Digimons
            for (int i = 0; i < 10; i++) {
                Digimon digimon = DigimonGenerator.generateRandomDigimon();
                world.addDigimon(digimon);
                LOGGER.info("Added Digimon: " + digimon.getName());
            }

            ExecutorService executor = Executors.newCachedThreadPool();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down...");
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.warning("Executor did not terminate in the specified time.");
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Shutdown interrupted", e);
                }
                gui.shutdown();
            }));

            LOGGER.info("Starting simulation...");
            executor.submit(() -> {
                try {
                    world.simulate(gui);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Simulation failed", e);
                }
            }, "Simulation Thread");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize the simulator", e);
            System.exit(1);
        }
    }
}