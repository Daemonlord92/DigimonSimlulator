import com.horrorcore.World;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;
import com.horrorcore.systems.persistence.SaveSystem;
import com.horrorcore.systems.persistence.WorldSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SaveSystemTest {

    private World world;
    private static final String TEST_SAVE_FILE = "test_save.json";

    @BeforeEach
    void setUp() {
        world = World.getInstance();
        world.reset();
        world.initialize();
    }

    @AfterEach
    void tearDown() {
        // Clean up test save file
        SaveSystem.deleteSaveFile(TEST_SAVE_FILE);
    }

    @Test
    void testSaveToFile() {
        // Add some Digimon to the world
        Digimon testDigimon = new Digimon("SaveTestDigimon", 10, 100, 50, 20, "Rookie");
        world.addDigimon(testDigimon);

        // Save the world
        boolean saved = SaveSystem.saveToFile(world, TEST_SAVE_FILE);
        assertTrue(saved, "Save should succeed");

        // Verify file exists
        File saveFile = new File("saves/" + TEST_SAVE_FILE);
        assertTrue(saveFile.exists(), "Save file should exist");
    }

    @Test
    void testLoadFromFile() {
        // Add some Digimon to the world
        Digimon testDigimon1 = new Digimon("LoadTestDigimon1", 15, 80, 40, 25, "Champion");
        Digimon testDigimon2 = new Digimon("LoadTestDigimon2", 8, 60, 30, 15, "Rookie");
        world.addDigimon(testDigimon1);
        world.addDigimon(testDigimon2);

        int originalTime = world.getTime();
        int originalDigimonCount = world.getSectors().stream()
                .mapToInt(s -> s.getDigimons().size())
                .sum();

        // Save the world
        boolean saved = SaveSystem.saveToFile(world, TEST_SAVE_FILE);
        assertTrue(saved, "Save should succeed");

        // Load the world
        WorldSnapshot snapshot = SaveSystem.loadFromFile(TEST_SAVE_FILE);
        assertNotNull(snapshot, "Snapshot should not be null");
        assertEquals(originalTime, snapshot.getTime(), "Time should match");

        // Count Digimon in snapshot
        int snapshotDigimonCount = snapshot.getSectors().stream()
                .mapToInt(s -> s.getDigimons().size())
                .sum();
        assertEquals(originalDigimonCount, snapshotDigimonCount, "Digimon count should match");
    }

    @Test
    void testListSaveFiles() {
        // Save multiple files
        SaveSystem.saveToFile(world, "test1.json");
        SaveSystem.saveToFile(world, "test2.json");

        String[] saves = SaveSystem.listSaveFiles();
        assertTrue(saves.length >= 2, "Should have at least 2 save files");

        // Clean up
        SaveSystem.deleteSaveFile("test1.json");
        SaveSystem.deleteSaveFile("test2.json");
    }

    @Test
    void testDeleteSaveFile() {
        // Save a file
        SaveSystem.saveToFile(world, TEST_SAVE_FILE);
        
        // Delete it
        boolean deleted = SaveSystem.deleteSaveFile(TEST_SAVE_FILE);
        assertTrue(deleted, "Delete should succeed");

        // Verify it's gone
        File saveFile = new File("saves/" + TEST_SAVE_FILE);
        assertFalse(saveFile.exists(), "Save file should not exist after deletion");
    }

    @Test
    void testSaveAndLoadPreservesDigimonProperties() {
        // Create a Digimon with specific properties
        Digimon testDigimon = new Digimon("PropertyTestDigimon", 25, 120, 60, 35, "Ultimate");
        testDigimon.setProfession("Farmer");
        world.addDigimon(testDigimon);

        // Save the world
        SaveSystem.saveToFile(world, TEST_SAVE_FILE);

        // Load the snapshot
        WorldSnapshot snapshot = SaveSystem.loadFromFile(TEST_SAVE_FILE);
        assertNotNull(snapshot, "Snapshot should not be null");

        // Find the Digimon in the snapshot
        boolean found = snapshot.getSectors().stream()
                .flatMap(s -> s.getDigimons().stream())
                .anyMatch(d -> d.getName().equals("PropertyTestDigimon") 
                        && d.getAge() == 25
                        && d.getHealth() == 120
                        && d.getStage().equals("Ultimate")
                        && d.getProfession().equals("Farmer"));

        assertTrue(found, "Digimon with correct properties should be in snapshot");
    }
}
