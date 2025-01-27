package com.horrorcore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DigimonSimulator {
    /**
     * The main entry point for the Digimon Simulator application.
     * This method initializes the world, creates and populates it with Digimons,
     * sets up the GUI, and starts the simulation in a separate thread.
     * It also sets up a shutdown hook to gracefully terminate the application.
     *
     * @param args Command line arguments passed to the program (not used in this implementation)
     */
    public static void main(String[] args) {
        World world = new World();
        VisualGUI gui = VisualGUI.getInstance(world);
        System.out.println("Created VisualGUI instance: " + gui);
    
        // Initialize the world first
        world.initialize();
        System.out.println("World initialized with sectors: " + world.getSectors());
    
        // Then initialize GUI
        gui.initialize();
    
        // Add Digimons
        for (int i = 0; i < 10; i++) {
            Digimon digimon = DigimonGenerator.generateRandomDigimon();
            world.addDigimon(digimon);
            System.out.println("Added Digimon: " + digimon.getName());
        }
    
        ExecutorService executor = Executors.newSingleThreadExecutor();
    
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            executor.shutdownNow();
            gui.shutdown();
        }));
    
        System.out.println("Starting simulation...");
        executor.submit(() -> {
            try {
                world.simulate(gui);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}