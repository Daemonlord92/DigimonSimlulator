package com.horrorcore.config;

/**
 * Central configuration for all simulation parameters.
 * Modify these values to tune simulation behavior without changing logic code.
 */
public class SimulationConfig {
    
    // ===== POPULATION CONTROL =====
    public static final int MAX_DIGIMON_PER_SECTOR = 50;
    public static final int MIN_DIGIMON_PER_SECTOR = 5;
    public static final int INITIAL_DIGIMON_COUNT = 100;
    public static final int INITIAL_CELESTIAL_COUNT = 10;
    
    // ===== BIRTH SYSTEM =====
    public static final double BIRTH_PROBABILITY_NORMAL = 0.3; // 30% chance when population is normal
    public static final double BIRTH_PROBABILITY_LOW = 0.7;    // 70% chance when underpopulated
    public static final int BIRTH_CHECK_INTERVAL = 1;          // Check every N ticks
    public static final int EMERGENCY_SPAWN_INTERVAL = 10;     // Spawn emergency Digimon every N ticks when empty
    public static final int EMERGENCY_SPAWN_COUNT = 5;         // How many to spawn in emergency
    
    // ===== DEATH SYSTEM =====
    public static final double BASE_DEATH_PROBABILITY = 0.0005; // 0.05% base chance per tick
    public static final int BASE_DEATH_CHANCE = 1;              // 1% base chance (used in different calculation)
    public static final int OLD_AGE_THRESHOLD = 50;             // Age at which death chance increases
    public static final int YOUNG_AGE_THRESHOLD = 10;           // Age below which Digimon are vulnerable
    
    // ===== PERFORMANCE & TIMING =====
    public static final int SIMULATION_TICK_MS = 3000;          // Main simulation loop delay
    public static final int GUI_UPDATE_INTERVAL_MS = 1000;      // GUI refresh rate
    public static final int WATCHDOG_CHECK_INTERVAL_MS = 10000; // Watchdog thread check interval
    public static final int WATCHDOG_FREEZE_THRESHOLD_MS = 15000; // Time before declaring simulation frozen
    
    // ===== OPERATION THROTTLING =====
    public static final int EVOLUTION_CHECK_INTERVAL = 1;       // Check evolution every N ticks
    public static final int EVENT_TRIGGER_INTERVAL = 5;         // Trigger random events every N ticks
    public static final int POLITICS_UPDATE_INTERVAL = 3;       // Update political situation every N ticks
    public static final int PATHFINDING_COOLDOWN = 3;           // Ticks between pathfinding operations per Digimon
    
    // ===== GRID SYSTEM =====
    public static final int GRID_SIZE = 20;
    public static final double BLOCKED_CELL_PROBABILITY = 0.15; // 15% of cells are blocked terrain
    public static final int MIN_PATH_WIDTH = 2;
    
    // ===== COMBAT SYSTEM =====
    public static final int AGGRESSION_THRESHOLD = 250;         // Aggression level to initiate combat
    public static final double CELESTIAL_HELP_PROBABILITY = 0.3; // 30% chance Celestial helps per tick
    
    // ===== TRIBE SYSTEM =====
    public static final int MIN_DIGIMON_FOR_TRIBE = 2;          // Minimum Digimon needed to form tribe
    public static final int INITIAL_TRIBE_SIZE = 3;             // Size of newly formed tribes (leader + members)
    public static final int INITIAL_TRIBE_FOOD = 150;
    public static final int INITIAL_MILITARY_STRENGTH = 3;
    public static final long TRIBE_FEED_COOLDOWN_MS = 1000;
    
    // ===== TECHNOLOGY SYSTEM =====
    public static final int[] AGE_REQUIREMENTS = {500, 1000, 1500, 2000}; // Time required for each age
    public static final double PROFESSION_REASSIGNMENT_CHANCE = 0.1; // 10% chance to reassign profession
    
    // ===== MOVEMENT =====
    public static final int MOVEMENT_AGE_THRESHOLD = 25;        // Digimon younger than this move more
    public static final int MOVEMENT_HEALTH_THRESHOLD = 15;     // Minimum health to move
    public static final int MAX_PATHFINDING_DISTANCE = 15;      // Maximum distance for pathfinding
    
    // ===== LOGGING =====
    public static final boolean VERBOSE_LOGGING = false;        // Enable detailed logs
    public static final boolean LOG_PERFORMANCE = true;         // Log performance metrics
    
    private SimulationConfig() {
        // Private constructor to prevent instantiation
    }
}
