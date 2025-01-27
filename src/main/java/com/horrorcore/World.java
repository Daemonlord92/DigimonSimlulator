package com.horrorcore;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the Digimon world, containing all the elements and systems of the simulation.
 * This class manages the overall state of the world, including Digimons, tribes, sectors,
 * and various systems like technology and evolution.
 */
public class World {
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());
    private List<Digimon> digimonList;
    private List<Tribe> tribes;
    private TechnologySystem technologySystem;
    private int time;
    private List<Sector> sectors;
    private Random random;
    private final ReadWriteLock worldLock = new ReentrantReadWriteLock();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final List<Integer> agesRequired = List.of(500, 1000, 1500, 2000);


    /**
     * Constructs a new World object, initializing all its components.
     * This constructor sets up the initial state of the Digimon world, including:
     * - An empty list of Digimons
     * - An empty list of Tribes
     * - A new TechnologySystem
     * - The initial time set to 0
     * - An empty list of Sectors
     * - A new Random object for generating random events
     *
     * After initializing these components, it calls the initializeSectors() method
     * to set up the world's geographical structure.
     */
    public World() {
        this.digimonList = new ArrayList<>();
        this.tribes = Tribe.getAllTribes();
        this.technologySystem = new TechnologySystem();
        this.time = 0;
        this.sectors = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Adds a Digimon to a randomly selected sector in the world.
     * This method chooses a random sector from the list of available sectors
     * and adds the given Digimon to that sector.
     *
     * @param digimon The Digimon to be added to the world. This Digimon will be
     *                placed in a randomly chosen sector.
     */
    public void addDigimon(Digimon digimon) {
        if (digimon == null) {
            LOGGER.warning("Attempted to add null Digimon to the world");
            return;
        }
        boolean lockAcquired = false;
        try {
            lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping addDigimon operation.");
                return;
            }
            Sector randomSector = getRandomSector();
            randomSector.addDigimon(digimon);
            LOGGER.info("Added Digimon " + digimon.getName() + " to sector " + randomSector.getName());
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while trying to acquire lock", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lockAcquired) {
                worldLock.writeLock().unlock();
            }
        }
    }

    private Sector getRandomSector() {
        return sectors.get(random.nextInt(sectors.size()));
    }

    /**
     * Initializes the sectors of the Digimon world and sets up their adjacencies.
     * This method creates ten different sectors, establishes their geographical relationships,
     * and adds them to the world's list of sectors.
     *
     * The sectors created are:
     * - File Island
     * - Server Continent
     * - Folder Continent
     * - WWW Continent
     * - Net Ocean
     * - Desert Region
     * - Frozen Tundra
     * - Volcanic Zone
     * - Sky City
     * - Underground Caverns
     *
     * Each sector is connected to one or more adjacent sectors to create a coherent world map.
     *
     * This method does not take any parameters and does not return any value.
     * It operates on the class-level 'sectors' list, populating it with the created Sector objects.
     */
    public void initialize() {
        // Create all sectors here
        Sector fileIsland = new Sector("File Island");
        Sector serverContinent = new Sector("Server Continent");
        Sector folderContinent = new Sector("Folder Continent");
        Sector wwwContinent = new Sector("WWW Continent");
        Sector netOcean = new Sector("Net Ocean");
        Sector desertRegion = new Sector("Desert Region");
        Sector frozenTundra = new Sector("Frozen Tundra");
        Sector volcanicZone = new Sector("Volcanic Zone");
        Sector skyCity = new Sector("Sky City");
        Sector undergroundCaverns = new Sector("Underground Caverns");

        // Set up adjacencies
        fileIsland.addAdjacentSector(serverContinent);
        fileIsland.addAdjacentSector(netOcean);

        serverContinent.addAdjacentSector(folderContinent);
        serverContinent.addAdjacentSector(desertRegion);

        folderContinent.addAdjacentSector(wwwContinent);
        folderContinent.addAdjacentSector(frozenTundra);

        wwwContinent.addAdjacentSector(fileIsland);
        wwwContinent.addAdjacentSector(volcanicZone);

        netOcean.addAdjacentSector(desertRegion);
        netOcean.addAdjacentSector(skyCity);

        desertRegion.addAdjacentSector(frozenTundra);
        desertRegion.addAdjacentSector(undergroundCaverns);

        frozenTundra.addAdjacentSector(volcanicZone);

        volcanicZone.addAdjacentSector(skyCity);

        skyCity.addAdjacentSector(undergroundCaverns);

        List<Sector> sectorList = Arrays.asList(
                fileIsland,
                serverContinent,
                folderContinent,
                wwwContinent,
                netOcean,
                desertRegion,
                frozenTundra,
                volcanicZone,
                skyCity,
                undergroundCaverns);
        for (Sector sector : sectorList) {
            addSector(sector);
        }
    }

    private void addSector(Sector sector) {
        sectors.add(sector);
    }

    /**
     * Simulates the progression of the Digimon world over time.
     * This method runs in an infinite loop, updating the state of the world at each time step.
     * It handles Digimon aging, evolution, movement, combat, birth, rebirth, and tribe formation.
     * It also manages technological advancement and displays the status of each sector and its Digimons.
     *
     * The simulation performs the following actions:
     * - Ages up Digimons and checks for evolution
     * - Initiates combat between aggressive Digimons
     * - Moves Digimons between sectors
     * - Checks for rebirth and initiates random births
     * - Triggers random events
     * - Forms new tribes and builds cities
     * - Advances the technological age
     * - Displays the status of all Digimons in each sector
     *
     * The simulation pauses for 3 seconds between each time step to allow for observation.
     *
     * This method does not take any parameters and does not return any value as it runs indefinitely.
     */
    public void simulate(VisualGUI gui) {
        System.out.println("Simulation started with GUI: " + gui);
        while (running.get()) {
            boolean lockAcquired = false;
            try {
                lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
                if (!lockAcquired) {
                    LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping this simulation step.");
                    continue;
                }
                StringBuilder output = new StringBuilder();
                output.append("\n--- Time: ").append(time).append(" ---\n");
                output.append("Current Age: ").append(technologySystem.getCurrentAge()).append("\n");

                for (Sector sector : sectors) {
                for (Digimon digimon : new ArrayList<>(sector.getDigimons())) {
                    digimon.ageUp();
                    EvolutionSystem.checkEvolution(digimon);

                    if (digimon.getAggression() > 50) {
                        Digimon target = findTarget(digimon, sector);
                        if (target != null) {
                            digimon.attack(target);
                        }
                    }

                    if (digimon.getAge() <= 25 || digimon.getHealth() >= 15 && random.nextBoolean()) {
                        Optional<Sector> targetSectorOptional = sector.getAdjacentSectors().stream().findAny();
                        if (targetSectorOptional.isPresent()) {
                            sector.removeDigimon(digimon);
                            Sector targetSector = targetSectorOptional.get();
                            targetSector.addDigimon(digimon);
                            VisualGUI.getInstance(null).addEvent(digimon.getName() + " has moved to sector " + targetSector.getName(), VisualGUI.EventType.OTHER);
                        }
                    }
                }
                List<Digimon> digimons = sector.getDigimons();
                RebirthSystem.checkRebirth(digimons);
                if (time % (random.nextInt(2) + 1) == 0 && digimons.size() < 10) {
                    BirthSystem.randomBirth(digimons);
                }
                if (time % 5 == 0) {
                    EventSystem.triggerRandomEvent(digimons, tribes);
                }

                if (time % 2 == 0 && random.nextBoolean()) {
                    Tribe.formNewTribe(digimons, tribes);
                    if (!tribes.isEmpty()) {
                        Tribe.buildCity(tribes.get(random.nextInt(tribes.size())));
                    }
                }

                if (digimons.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        Digimon newDigimon = DigimonGenerator.generateRandomDigimon();
                        sector.addDigimon(newDigimon);
                    }
                }
                if(random.nextBoolean()) {
                    FoodSystem.distributeFood(digimons);
                }

            }

            // Update political relationships
            int currentAgeIndex = Arrays.asList(technologySystem.AGES).indexOf(technologySystem.getCurrentAge());
            // Advance technological age
            if (time == agesRequired.get(currentAgeIndex)) {
                technologySystem.advanceAge();
            }

            // Replace console output with GUI updates
            for (Sector sector : sectors) {
                output.append("\nSector: ").append(sector.getName()).append("\n");
                for (Digimon digimon : sector.getDigimons()) {
                    output.append(digimon.getStatusString()).append("\n");
                }
            }

                int totalDigimon = sectors.stream().mapToInt(sector -> sector.getDigimons().size()).sum();
                double deathProbability = 0.05; // 5% chance of death per Digimon per time step
                int expectedDeaths = (int) Math.round(totalDigimon * deathProbability);
                int actualDeaths = random.nextInt(expectedDeaths * 2 + 1); // Allow for some variability

                for (int i = 0; i < actualDeaths; i++) {
                    simulateRandomDeath();
                }

            gui.updateDisplay(); // Use the passed GUI instance

                time++;
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Simulation interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } finally {
                if (lockAcquired) {
                    worldLock.writeLock().unlock();
                }
            }

            // Update GUI on EDT
            SwingUtilities.invokeLater(gui::updateDisplay);

            try {
                Thread.sleep(1000); // Adjust as needed
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Sleep interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
/**
 * Simulates a random death of a Digimon in the world without rebirth.
 * This method has a small chance of removing a random Digimon from the world.
 */
private void simulateRandomDeath() {
    if (random.nextInt(100) < 5) { // 5% chance of death occurring
        List<Digimon> allDigimon = new ArrayList<>();
        for (Sector sector : sectors) {
            allDigimon.addAll(sector.getDigimons());
        }
        
        if (!allDigimon.isEmpty()) {
            Digimon unfortunateDigimon = allDigimon.get(random.nextInt(allDigimon.size()));
            Sector digimonSector = sectors.stream()
                .filter(sector -> sector.getDigimons().contains(unfortunateDigimon))
                .findFirst()
                .orElse(null);
            
            if (digimonSector != null) {
                digimonSector.removeDigimon(unfortunateDigimon);
                LOGGER.info(unfortunateDigimon.getName() + " has died in " + digimonSector.getName());
                VisualGUI.getInstance(null).addEvent(unfortunateDigimon.getName() + " has died in " + digimonSector.getName(), VisualGUI.EventType.OTHER);
            }
        }
    }
}

    /**
     * Finds a potential target for the attacking Digimon within the current sector or adjacent sectors.
     *
     * This method creates a list of possible targets by combining all Digimon in the current sector
     * and its adjacent sectors, excluding the attacker itself. It then randomly selects a target
     * from this list if it's not empty.
     *
     * @param attacker The Digimon initiating the attack.
     * @param currentSector The sector where the attacker is currently located.
     * @return A randomly selected Digimon target from the current or adjacent sectors, or null if no targets are available.
     */
    private Digimon findTarget(Digimon attacker, Sector currentSector) {
        List<Digimon> possibleTargets = new ArrayList<>(currentSector.getDigimons());
        for (Sector adjacentSector : currentSector.getAdjacentSectors()) {
            possibleTargets.addAll(adjacentSector.getDigimons());
        }
        possibleTargets.remove(attacker);

        if (!possibleTargets.isEmpty()) {
            return possibleTargets.get(random.nextInt(possibleTargets.size()));
        }
        return null;
    }

    /**
     * Retrieves a list of all sectors in the world.
     * This method attempts to acquire a read lock on the world state to ensure thread-safe access.
     * If the lock cannot be acquired within 5 seconds, an empty list is returned.
     *
     * @return A new ArrayList containing all sectors in the world. If the lock cannot be acquired,
     *         or if an InterruptedException occurs, an empty ArrayList is returned.
     */
    public List<Sector> getSectors() {
        boolean lockAcquired = false;
        try {
            lockAcquired = worldLock.readLock().tryLock(5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOGGER.warning("Failed to acquire read lock within 5 seconds. Returning empty sector list.");
                return new ArrayList<>();
            }
            return new ArrayList<>(sectors);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while trying to acquire lock", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            if (lockAcquired) {
                worldLock.readLock().unlock();
            }
        }
    }

    public int getTime() {
        return time;
    }

    public int getBuildings() {
        return tribes.stream().mapToInt(Tribe::getBuildings).sum();
    }

    public TechnologySystem getTechnologySystem() {
        return technologySystem;
    }

    public void stop() {
        running.set(false);
    }

    /**
     * Calculates the remaining time until the next technological age in the simulation.
     * This method determines how many time units are left before the world advances to the next age
     * based on the current time and the predefined age requirements.
     *
     * @return An integer representing the number of time units remaining until the next age.
     *         If the current age is the final age, this method will return a negative value
     *         indicating the number of time units that have passed since the last age transition.
     */
    public int getTimeToNextAge() {
        int currentAgeIndex = Arrays.asList(technologySystem.AGES).indexOf(technologySystem.getCurrentAge());
        return agesRequired.get(currentAgeIndex) - time;
    }
}
