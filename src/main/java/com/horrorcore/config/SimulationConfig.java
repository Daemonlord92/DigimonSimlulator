package com.horrorcore.config;

/**
 * Central configuration for all simulation parameters.
 * Modify these values to balance gameplay without hunting through code.
 */
public class SimulationConfig {
    
    // ===== POPULATION CONTROL =====
    public static final int MAX_DIGIMON_PER_SECTOR = 50;
    public static final int MIN_DIGIMON_PER_SECTOR = 5;
    public static final int INITIAL_DIGIMON_COUNT = 100;
    public static final int INITIAL_CELESTIAL_COUNT = 10;
    
    // Birth rates (probability per tick)
    public static final double BIRTH_PROBABILITY_NORMAL = 0.3;
    public static final double BIRTH_PROBABILITY_UNDERPOPULATED = 0.7;
    public static final int BIRTH_CHECK_INTERVAL_TICKS = 10;
    public static final int EMERGENCY_SPAWN_THRESHOLD = 2; // Spawn if below this
    public static final int EMERGENCY_SPAWN_COUNT = 5;
    
    // ===== PERFORMANCE SETTINGS =====
    public static final int SIMULATION_TICK_MS = 3000;
    public static final int GUI_UPDATE_INTERVAL_MS = 1000;
    public static final int MAX_PATHFINDING_DISTANCE = 15;
    public static final int PATHFINDING_COOLDOWN_TICKS = 3;
    
    // Operation throttling (run every N ticks)
    public static final int EVOLUTION_CHECK_INTERVAL = 1; // Every tick for now
    public static final int COMBAT_RESOLUTION_INTERVAL = 1;
    public static final int TRIBE_FORMATION_INTERVAL = 5;
    public static final int EVENT_TRIGGER_INTERVAL = 5;
    public static final int POLITICS_UPDATE_INTERVAL = 1;
    
    // ===== DEATH SYSTEM =====
    public static final double BASE_DEATH_CHANCE = 0.0005; // 0.05%
    public static final int YOUNG_AGE_THRESHOLD = 10;
    public static final int OLD_AGE_THRESHOLD = 50;
    public static final double OLD_AGE_DEATH_MULTIPLIER = 2.0;
    public static final double YOUNG_AGE_DEATH_MULTIPLIER = 1.5;
    
    // ===== GRID SETTINGS =====
    public static final int DEFAULT_GRID_SIZE = 20;
    public static final double BLOCKED_CELL_PROBABILITY = 0.15;
    public static final int MIN_PATH_WIDTH = 2;
    
    // ===== COMBAT SETTINGS =====
    public static final int AGGRESSION_THRESHOLD = 250;
    public static final int COMBAT_RANGE = 1; // Grid cells
    
    // ===== TRIBE SETTINGS =====
    public static final int MIN_TRIBE_SIZE = 2;
    public static final int INITIAL_TRIBE_FOOD = 150;
    public static final int INITIAL_MILITARY_STRENGTH = 3;
    public static final long TRIBE_FEED_COOLDOWN_MS = 1000;
    
    // ===== CELESTIAL DIGIMON =====
    public static final double CELESTIAL_HELP_PROBABILITY = 0.3;
    public static final double CELESTIAL_HEAL_VS_FEED_RATIO = 0.5;
    
    // ===== TECHNOLOGY SYSTEM =====
    public static final int[] AGE_ADVANCEMENT_TIMES = {500, 1000, 1500, 2000};
    
    // ===== WATCHDOG SETTINGS =====
    public static final int WATCHDOG_CHECK_INTERVAL_MS = 10000;
    public static final int WATCHDOG_FREEZE_THRESHOLD_MS = 15000;
    
    // ===== LOGGING =====
    public static final boolean VERBOSE_LOGGING = false;
    public static final boolean PERFORMANCE_LOGGING = true;
}
