package com.horrorcore;

import java.util.concurrent.*;
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
            if (world.isInitialized()) {
                world.initialize();
                LOGGER.info("World initialized with sectors: " + world.getSectors());

                // Then initialize GUI
                gui.initialize();

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
            } else {
                LOGGER.info("World is already initialized and filled with Digimons.");
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

            final long TIMEOUT_SECONDS = 30000; // Adjust this value as needed


            while (true) {
                final Future<?> future = executor.submit(() -> {
                    try {
                        world.simulate(gui);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Simulation failed", e);
                    }
                }, "Simulation Thread");

                try {
                    future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    break; // Simulation completed successfully
                } catch (TimeoutException e) {
                    LOGGER.warning("Potential lock detected. Attempting to restart simulation.");
                    future.cancel(true);
                    world.saveState(); // Assuming you have a method to save the world state
                    world.reset(); // Assuming you have a method to reset the world
                    world.loadState();// Assuming you have a method to load the saved state
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Simulation failed", e);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize the simulator", e);
            System.exit(1);
        }
    }
}