import com.horrorcore.World;
import com.horrorcore.entity.Digimon;
import com.horrorcore.systems.persistence.SaveSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaveFileSampleTest {
    
    @Test
    void generateSampleSaveFile() {
        World world = World.getInstance();
        world.reset();
        world.initialize();
        
        // Add some test Digimon
        world.addDigimon(new Digimon("Agumon", 10, 100, 50, 20, "Rookie"));
        world.addDigimon(new Digimon("Gabumon", 12, 90, 45, 25, "Rookie"));
        world.addDigimon(new Digimon("Greymon", 20, 150, 60, 40, "Champion"));
        
        // Save
        boolean saved = SaveSystem.saveToFile(world, "sample_save.json");
        assertTrue(saved, "Save should succeed");
    }
}
