package com.horrorcore.config;

/**
 * Centralized configuration for all simulation parameters.
 * Modify these values to tune simulation behavior without touching core logic.
 */
public class SimulationConfig {
    
    // === Population Control ===
    public static final int MAX_DIGIMON_PER_SECTOR = 50;
    public static final int MIN_DIGIMON_PER_SECTOR = 5;
    public static final int IDEAL_TOTAL_POPULATION = 200;
    public static final int MAX_TOTAL_POPULATION = 300;
    
    // === Birth System ===
    public static final double BIRTH_PROBABILITY = 0.3;
    public static final int BIRTH_CHECK_INTERVAL = 10; // ticks
    public static final int EMERGENCY_BIRTH_COUNT = 5; // When sector is empty
    
    // === Death System ===
    public static final double BASE_DEATH_CHANCE = 0.001; // 0.1% base chance
    public static final int OLD_AGE_THRESHOLD = 50;
    public static final int YOUNG_AGE_THRESHOLD = 10;
    public static final double DEATH_PROBABILITY_PER_TICK = 0.0005;
    
    // === Performance Timing ===
    public static final int SIMULATION_TICK_MS = 3000;
    public static final int GUI_UPDATE_INTERVAL_MS = 1000;
    public static final int WATCHDOG_CHECK_INTERVAL_MS = 10000;
    public static final int WATCHDOG_TIMEOUT_MS = 15000;
    
    // === Pathfinding ===
    public static final int MAX_PATHFINDING_DISTANCE = 15;
    public static final int PATHFINDING_COOLDOWN_TICKS = 3;
    
    // === Grid System ===
    public static final int DEFAULT_GRID_SIZE = 20;
    public static final double BLOCKED_CELL_PROBABILITY = 0.15;
    public static final int MIN_PATH_WIDTH = 2;
    
    // === Combat ===
    public static final int HIGH_AGGRESSION_THRESHOLD = 250;
    public static final double CELESTIAL_HELP_PROBABILITY = 0.3;
    
    // === Tribe System ===
    public static final int MIN_DIGIMON_FOR_TRIBE = 2;
    public static final int INITIAL_TRIBE_MEMBERS = 3;
    public static final int INITIAL_TRIBE_FOOD = 150;
    public static final int INITIAL_MILITARY_STRENGTH = 3;
    public static final long FEED_COOLDOWN_MS = 1000;
    
    // === Event System ===
    public static final int EVENT_TRIGGER_INTERVAL = 5; // Every 5 ticks
    public static final double POLITICS_UPDATE_PROBABILITY = 0.5;
    
    // === Movement ===
    public static final int YOUNG_MOVEMENT_AGE = 25;
    public static final int HEALTHY_MOVEMENT_THRESHOLD = 15;
    
    // === Technology ===
    public static final int[] AGES_REQUIRED = {500, 1000, 1500, 2000};
    
    // === Profession Assignment ===
    public static final double PROFESSION_REASSIGNMENT_CHANCE = 0.1;
    
    private SimulationConfig() {
        // Prevent instantiation
    }
}
