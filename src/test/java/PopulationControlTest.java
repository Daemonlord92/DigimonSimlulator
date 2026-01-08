import com.horrorcore.World;
import com.horrorcore.config.SimulationConfig;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify population control mechanisms
 */
class PopulationControlTest {

    private World world;

    @BeforeEach
    void setUp() {
        // Get a fresh instance of World
        world = World.getInstance();
        world.reset();
        world.initialize();
    }

    @Test
    void testPopulationStatisticsTracking() {
        // Verify initial state
        assertEquals(0, world.getPeakPopulation(), "Initial peak population should be 0");
        assertEquals(0, world.getTotalBirths(), "Initial births should be 0");
        assertEquals(0, world.getTotalDeaths(), "Initial deaths should be 0");
    }

    @Test
    void testConfigurationConstants() {
        // Verify that configuration constants are properly set
        assertTrue(SimulationConfig.MAX_DIGIMON_PER_SECTOR > SimulationConfig.MIN_DIGIMON_PER_SECTOR,
            "Max population should be greater than min");
        assertTrue(SimulationConfig.BIRTH_PROBABILITY_LOW > SimulationConfig.BIRTH_PROBABILITY_NORMAL,
            "Low population should have higher birth rate");
    }

    @Test
    void testEmergencySpawnConfiguration() {
        // Verify emergency spawn constants are reasonable
        assertTrue(SimulationConfig.EMERGENCY_SPAWN_INTERVAL > 0,
            "Emergency spawn interval should be positive");
        assertTrue(SimulationConfig.EMERGENCY_SPAWN_COUNT > 0,
            "Emergency spawn count should be positive");
        assertTrue(SimulationConfig.EMERGENCY_SPAWN_COUNT <= SimulationConfig.MIN_DIGIMON_PER_SECTOR,
            "Emergency spawn should not exceed min population");
    }

    @Test
    void testThrottlingIntervals() {
        // Verify all throttling intervals are positive
        assertTrue(SimulationConfig.BIRTH_CHECK_INTERVAL > 0,
            "Birth check interval should be positive");
        assertTrue(SimulationConfig.EVOLUTION_CHECK_INTERVAL > 0,
            "Evolution check interval should be positive");
        assertTrue(SimulationConfig.EVENT_TRIGGER_INTERVAL > 0,
            "Event trigger interval should be positive");
        assertTrue(SimulationConfig.POLITICS_UPDATE_INTERVAL > 0,
            "Politics update interval should be positive");
    }

    @Test
    void testInitialPopulationConfiguration() {
        // Test that initial population values are sensible
        assertTrue(SimulationConfig.INITIAL_DIGIMON_COUNT > 0,
            "Initial digimon count should be positive");
        assertTrue(SimulationConfig.INITIAL_CELESTIAL_COUNT > 0,
            "Initial celestial count should be positive");
        
        // Verify the initial population can be distributed across sectors
        int sectorCount = world.getSectors().size();
        assertTrue(sectorCount > 0, "Should have sectors initialized");
    }

    @Test
    void testAgeRequirementsProgression() {
        // Verify age requirements are in ascending order
        int[] ageRequirements = SimulationConfig.AGE_REQUIREMENTS;
        assertTrue(ageRequirements.length >= 4,
            "Should have at least 4 age requirements");
        
        for (int i = 1; i < ageRequirements.length; i++) {
            assertTrue(ageRequirements[i] > ageRequirements[i-1],
                "Age requirements should be in ascending order");
        }
    }

    @Test
    void testTribeConfiguration() {
        // Verify tribe formation constants
        assertTrue(SimulationConfig.MIN_DIGIMON_FOR_TRIBE >= 2,
            "Should need at least 2 digimon to form a tribe");
        assertTrue(SimulationConfig.INITIAL_TRIBE_SIZE >= SimulationConfig.MIN_DIGIMON_FOR_TRIBE,
            "Initial tribe size should meet minimum requirement");
        assertTrue(SimulationConfig.INITIAL_TRIBE_FOOD > 0,
            "Tribes should start with some food");
    }

    @Test
    void testDeathSystemConfiguration() {
        // Verify death system uses configuration
        assertTrue(SimulationConfig.BASE_DEATH_PROBABILITY >= 0 && SimulationConfig.BASE_DEATH_PROBABILITY < 1,
            "Base death probability should be a valid probability");
        assertTrue(SimulationConfig.OLD_AGE_THRESHOLD > SimulationConfig.YOUNG_AGE_THRESHOLD,
            "Old age threshold should be higher than young age");
    }

    @Test
    void testMovementConfiguration() {
        // Verify movement configuration
        assertTrue(SimulationConfig.MOVEMENT_AGE_THRESHOLD > 0,
            "Movement age threshold should be positive");
        assertTrue(SimulationConfig.MOVEMENT_HEALTH_THRESHOLD > 0,
            "Movement health threshold should be positive");
    }

    @Test
    void testCombatConfiguration() {
        // Verify combat configuration
        assertTrue(SimulationConfig.AGGRESSION_THRESHOLD > 0,
            "Aggression threshold should be positive");
        assertTrue(SimulationConfig.CELESTIAL_HELP_PROBABILITY >= 0 && 
                  SimulationConfig.CELESTIAL_HELP_PROBABILITY <= 1,
            "Celestial help probability should be valid");
    }
}
