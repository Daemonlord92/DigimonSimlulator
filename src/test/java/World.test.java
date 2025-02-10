import com.horrorcore.World;
import com.horrorcore.entity.Digimon;
import com.horrorcore.entity.Sector;
import com.horrorcore.gui.VisualGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorldTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = World.getInstance();
    }

    @Test
    public void testAddDigimon() {
        World world = World.getInstance();
        world.initialize();
        
        Digimon testDigimon = new Digimon("TestDigimon", 10, 50, 30, 20, "Rookie");
        world.addDigimon(testDigimon);
        
        boolean digimonFound = false;
        for (Sector sector : world.getSectors()) {
            if (sector.getDigimons().contains(testDigimon)) {
                digimonFound = true;
                break;
            }
        }
        
        assertTrue(digimonFound, "Digimon should be added to a sector");
    }

    @Test
    public void testSimulateTimeProgressionAndDigimonStateUpdate() {
        World world = World.getInstance();
        world.initialize();
        
        // Add some Digimon to the world
        Digimon digimon1 = new Digimon("TestDigimon1", 10, 100, 50, 100, "Rookie");
        Digimon digimon2 = new Digimon("TestDigimon2", 20, 150, 75, 100, "Champion");
        world.addDigimon(digimon1);
        world.addDigimon(digimon2);
        
        // Create a mock VisualGUI
        VisualGUI mockGUI = mock(VisualGUI.class);
        
        // Run simulation for a short time
        Thread simulationThread = new Thread(() -> world.simulate(mockGUI));
        simulationThread.start();
        
        try {
            // Let the simulation run for a short time
            Thread.sleep(5000);
            world.stop();
            simulationThread.join(2000);
        } catch (InterruptedException e) {
            fail("Simulation was interrupted: " + e.getMessage());
        }
        
        // Verify that time has progressed
        assertTrue(world.getTime() > 0, "Time should have progressed");
        
        // Verify that Digimon states have been updated
        for (Sector sector : world.getSectors()) {
            for (Digimon digimon : sector.getDigimons()) {
                assertTrue(digimon.getAge() > 0, "Digimon age should have increased");
                // Add more assertions here to check other Digimon state changes
            }
        }
        
        // Verify that the GUI was updated
        verify(mockGUI, atLeastOnce()).updateWorldInfo(world);
    }
    

    @Test
    public void testConcurrentAccessToWorldState() throws InterruptedException {
        World world = World.getInstance();
        world.initialize();

        int numThreads = 10;
        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    // Simulate concurrent read operations
                    List<Sector> sectors = world.getSectors();
                    assertNotNull(sectors);
                    assertFalse(sectors.isEmpty());

                    // Simulate concurrent write operations
                    Digimon testDigimon = new Digimon("TestDigimon", 10, 50, 30, 20, "Rookie");
                    world.addDigimon(testDigimon);

                    latch.countDown();
                } catch (Exception e) {
                    fail("Exception occurred: " + e.getMessage());
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "All threads did not complete in time");

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Verify that all operations were performed successfully
        assertTrue(world.getSectors().stream().anyMatch(sector -> !sector.getDigimons().isEmpty()));
    }
}
