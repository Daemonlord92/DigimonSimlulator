import com.horrorcore.config.SimulationConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify SimulationConfig values are properly defined and accessible
 */
class SimulationConfigTest {

    @Test
    void testPopulationControlConstants() {
        assertTrue(SimulationConfig.MAX_DIGIMON_PER_SECTOR > 0, "Max digimon per sector should be positive");
        assertTrue(SimulationConfig.MIN_DIGIMON_PER_SECTOR > 0, "Min digimon per sector should be positive");
        assertTrue(SimulationConfig.MAX_DIGIMON_PER_SECTOR > SimulationConfig.MIN_DIGIMON_PER_SECTOR, 
            "Max should be greater than min");
        assertTrue(SimulationConfig.INITIAL_DIGIMON_COUNT > 0, "Initial digimon count should be positive");
        assertTrue(SimulationConfig.INITIAL_CELESTIAL_COUNT > 0, "Initial celestial count should be positive");
    }

    @Test
    void testBirthSystemConstants() {
        assertTrue(SimulationConfig.BIRTH_PROBABILITY_NORMAL > 0 && SimulationConfig.BIRTH_PROBABILITY_NORMAL <= 1,
            "Birth probability normal should be between 0 and 1");
        assertTrue(SimulationConfig.BIRTH_PROBABILITY_LOW > 0 && SimulationConfig.BIRTH_PROBABILITY_LOW <= 1,
            "Birth probability low should be between 0 and 1");
        assertTrue(SimulationConfig.BIRTH_PROBABILITY_LOW > SimulationConfig.BIRTH_PROBABILITY_NORMAL,
            "Low population birth rate should be higher than normal");
        assertTrue(SimulationConfig.BIRTH_CHECK_INTERVAL > 0, "Birth check interval should be positive");
        assertTrue(SimulationConfig.EMERGENCY_SPAWN_INTERVAL > 0, "Emergency spawn interval should be positive");
        assertTrue(SimulationConfig.EMERGENCY_SPAWN_COUNT > 0, "Emergency spawn count should be positive");
    }

    @Test
    void testDeathSystemConstants() {
        assertTrue(SimulationConfig.BASE_DEATH_PROBABILITY >= 0, "Base death probability should be non-negative");
        assertTrue(SimulationConfig.BASE_DEATH_CHANCE >= 0, "Base death chance should be non-negative");
        assertTrue(SimulationConfig.OLD_AGE_THRESHOLD > SimulationConfig.YOUNG_AGE_THRESHOLD,
            "Old age threshold should be greater than young age threshold");
    }

    @Test
    void testTimingConstants() {
        assertTrue(SimulationConfig.SIMULATION_TICK_MS > 0, "Simulation tick should be positive");
        assertTrue(SimulationConfig.GUI_UPDATE_INTERVAL_MS > 0, "GUI update interval should be positive");
        assertTrue(SimulationConfig.WATCHDOG_CHECK_INTERVAL_MS > 0, "Watchdog check interval should be positive");
        assertTrue(SimulationConfig.WATCHDOG_FREEZE_THRESHOLD_MS > SimulationConfig.WATCHDOG_CHECK_INTERVAL_MS,
            "Freeze threshold should be greater than check interval");
    }

    @Test
    void testThrottlingConstants() {
        assertTrue(SimulationConfig.EVOLUTION_CHECK_INTERVAL > 0, "Evolution check interval should be positive");
        assertTrue(SimulationConfig.EVENT_TRIGGER_INTERVAL > 0, "Event trigger interval should be positive");
        assertTrue(SimulationConfig.POLITICS_UPDATE_INTERVAL > 0, "Politics update interval should be positive");
        assertTrue(SimulationConfig.PATHFINDING_COOLDOWN > 0, "Pathfinding cooldown should be positive");
    }

    @Test
    void testGridSystemConstants() {
        assertTrue(SimulationConfig.GRID_SIZE > 0, "Grid size should be positive");
        assertTrue(SimulationConfig.BLOCKED_CELL_PROBABILITY >= 0 && SimulationConfig.BLOCKED_CELL_PROBABILITY <= 1,
            "Blocked cell probability should be between 0 and 1");
        assertTrue(SimulationConfig.MIN_PATH_WIDTH > 0, "Min path width should be positive");
    }

    @Test
    void testCombatSystemConstants() {
        assertTrue(SimulationConfig.AGGRESSION_THRESHOLD > 0, "Aggression threshold should be positive");
        assertTrue(SimulationConfig.CELESTIAL_HELP_PROBABILITY >= 0 && SimulationConfig.CELESTIAL_HELP_PROBABILITY <= 1,
            "Celestial help probability should be between 0 and 1");
    }

    @Test
    void testTribeSystemConstants() {
        assertTrue(SimulationConfig.MIN_DIGIMON_FOR_TRIBE > 0, "Min digimon for tribe should be positive");
        assertTrue(SimulationConfig.INITIAL_TRIBE_SIZE >= SimulationConfig.MIN_DIGIMON_FOR_TRIBE,
            "Initial tribe size should be at least min digimon for tribe");
        assertTrue(SimulationConfig.INITIAL_TRIBE_FOOD >= 0, "Initial tribe food should be non-negative");
        assertTrue(SimulationConfig.INITIAL_MILITARY_STRENGTH >= 0, "Initial military strength should be non-negative");
        assertTrue(SimulationConfig.TRIBE_FEED_COOLDOWN_MS > 0, "Tribe feed cooldown should be positive");
    }

    @Test
    void testTechnologySystemConstants() {
        assertNotNull(SimulationConfig.AGE_REQUIREMENTS, "Age requirements should not be null");
        assertTrue(SimulationConfig.AGE_REQUIREMENTS.length > 0, "Age requirements should have elements");
        // Verify ascending order
        for (int i = 1; i < SimulationConfig.AGE_REQUIREMENTS.length; i++) {
            assertTrue(SimulationConfig.AGE_REQUIREMENTS[i] > SimulationConfig.AGE_REQUIREMENTS[i-1],
                "Age requirements should be in ascending order");
        }
        assertTrue(SimulationConfig.PROFESSION_REASSIGNMENT_CHANCE >= 0 && SimulationConfig.PROFESSION_REASSIGNMENT_CHANCE <= 1,
            "Profession reassignment chance should be between 0 and 1");
    }

    @Test
    void testMovementConstants() {
        assertTrue(SimulationConfig.MOVEMENT_AGE_THRESHOLD > 0, "Movement age threshold should be positive");
        assertTrue(SimulationConfig.MOVEMENT_HEALTH_THRESHOLD > 0, "Movement health threshold should be positive");
        assertTrue(SimulationConfig.MAX_PATHFINDING_DISTANCE > 0, "Max pathfinding distance should be positive");
    }
}
