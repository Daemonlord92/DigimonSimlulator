package com.horrorcore;

import java.util.*;
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
    private static World INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());
    private List<Digimon> digimonList;
    private Set<Tribe> tribes;
    private TechnologySystem technologySystem;
    private int time;
    private List<Sector> sectors;
    private Random random;
    private final ReadWriteLock worldLock = new ReentrantReadWriteLock();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final List<Integer> agesRequired = List.of(500, 1000, 1500, 2000);
    private volatile long lastUpdateTime = 0;
    private WorldState savedState;

    /**
     * Constructs a new World object, initializing all its components.
     * This constructor sets up the initial state of the Digimon world, including:
     * - An empty list of Digimons
     * - An empty list of Tribes
     * - A new TechnologySystem
     * - The initial time set to 0
     * - An empty list of Sectors
     * - A new Random object for generating random events
     * After initializing these components, it calls the initializeSectors() method
     * to set up the world's geographical structure.
     */
    private World() {
        this.digimonList = new ArrayList<>();
        this.tribes = new HashSet<>();
        this.technologySystem = new TechnologySystem();
        this.time = 0;
        this.sectors = new ArrayList<>();
        this.random = new Random();
    }

    public static World getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new World();
        }
        return INSTANCE;
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
     * Each sector is connected to one or more adjacent sectors to create a coherent world map.
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
     * The simulation performs the following actions:
     * - Ages up Digimons and checks for evolution
     * - Initiates combat between aggressive Digimons
     * - Moves Digimons between sectors
     * - Checks for rebirth and initiates random births
     * - Triggers random events
     * - Forms new tribes and builds cities
     * - Advances the technological age
     * - Displays the status of all Digimons in each sector
     * The simulation pauses for 3 seconds between each time step to allow for observation.
     * This method does not take any parameters and does not return any value as it runs indefinitely.
     */
    public void simulate(VisualGUI gui) {
            System.out.println("Simulation started with GUI: " + gui);
        Thread watchdog = getWatchdog();
        watchdog.start();
            while (running.get()) {
                lastUpdateTime = System.currentTimeMillis();
                boolean lockAcquired = false;
                try {
                    lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
                    if (!lockAcquired) {
                        LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping this simulation step.");
                        continue;
                    }

                    for (Sector sector : sectors) {
                    for (Digimon digimon : new ArrayList<>(sector.getDigimons())) {
                        digimon.ageUp();
                        if (digimon instanceof CelestialDigimon celestial) {
                            List<Digimon> nearbyDigimon = sector.getDigimons();
                            if (Math.random() < 0.3) { // 30% chance to help
                                if (Math.random() < 0.5) {
                                    celestial.provideFood(nearbyDigimon);
                                } else {
                                    celestial.heal(nearbyDigimon);
                                }
                            }
                        }
                        EvolutionSystem.checkEvolution(digimon);

                        if (digimon.getAggression() > 250) {
                            Digimon target = findTarget(digimon, sector);
                            if (target != null) {
                                digimon.attack(target);
                            }
                        }

                        if (digimon.getAge() <= 25 || digimon.getHealth() >= 15 && random.nextBoolean()) {
                            SectorMovement.moveDigimon(digimon, sector, random);
                        }
                    }
                    for (int i = 0; i < 5; i++) {
                        DigimonGenerator.generateRandomDigimon();
                    }
                    INSTANCE.getTribes().forEach(tribe -> {
                        tribe.getMembers().forEach(digimon -> {
                            if (digimon.getProfession() == null || random.nextDouble() < 0.1) { // 10% chance to reassign profession
                                String randomProfession = tribe.getTechnologySystem().getRandomProfession();
                                if (randomProfession != null) {
                                    tribe.getTechnologySystem().assignProfession(digimon, randomProfession);
                                    LOGGER.info(digimon.getName() + " assigned profession: " + randomProfession);
                                }
                            }
                        });
                        tribe.getTechnologySystem().performWork(INSTANCE);
                        tribe.feedTribe();
                    });

                    List<Digimon> digimons = sector.getDigimons();
                    RebirthSystem.checkRebirth(digimons);
                        if (random.nextDouble() < 0.3 && digimons.size() < 25) {  // 30% chance each tick, higher population cap
                            BirthSystem.randomBirth(digimons);
                            // Could add multiple birth attempts
                            if (random.nextDouble() < 0.5) {  // 50% chance of additional birth
                                BirthSystem.randomBirth(digimons);
                            }
                        } if (digimons.isEmpty()) {
                            BirthSystem.randomBirth(digimons);
                        }
                    if (time % 5 == 0) {
                        EventSystem.triggerRandomEvent(INSTANCE);
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


                if(random.nextBoolean() && tribes.size() > 1) {
                    LOGGER.info("Triggering Political Situation");
                    Politics.updatePoliticalSituation();
                }
                // Update political relationships

                int currentAgeIndex = Arrays.asList(TechnologySystem.AGES).indexOf(technologySystem.getCurrentAge());
                // Advance technological age
                if (time == agesRequired.get(currentAgeIndex)) {
                    technologySystem.advanceAge();
                }

                // Replace console output with GUI updates


                int totalDigimon = sectors.stream().mapToInt(sector -> sector.getDigimons().size()).sum();
                double deathProbability = 0.0005; // 5% chance of death per Digimon per time step
                int expectedDeaths = (int) Math.round(totalDigimon * deathProbability);
                int actualDeaths = random.nextInt(expectedDeaths * 2 + 1); // Allow for some variability

                for (int i = 0; i < actualDeaths; i++) {
                    simulateRandomDeath();
                }
                List<Tribe> tribesToRemove = INSTANCE.getTribes().stream()
                    .filter(tribe -> tribe.getMembers().isEmpty())
                    .toList();

                if (!tribesToRemove.isEmpty()) {
                    tribesToRemove.forEach(tribes::remove);
                    LOGGER.info("Removed " + tribesToRemove.size() + " empty tribes.");
                }
                tribes = Tribe.getAllTribes();
                SimulationSubject.getInstance().notifyWorldUpdate(this); // Use the passed GUI instance

                time++;
                    LOGGER.info("World simulated. Time: " + time + ", Tech Age: " + technologySystem.getCurrentAge());
                System.gc();
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
            SimulationSubject.getInstance().notifyWorldUpdate(this);

            try {
                Thread.sleep(3000); // Adjust as needed
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Sleep interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Thread getWatchdog() {
        Thread watchdog = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(10000); // Check every 10 seconds
                    if (System.currentTimeMillis() - lastUpdateTime > 15000) {
                        LOGGER.warning("Simulation seems to be frozen. Last update was more than 15 seconds ago.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        watchdog.setDaemon(true);
        return watchdog;
    }

    /**
 * Simulates a random death of a Digimon in the world without rebirth.
 * This method has a small chance of removing a random Digimon from the world.
 */
private void simulateRandomDeath() {
    List<Digimon> allDigimon = new ArrayList<>();
    for (Sector sector : sectors) {
        allDigimon.addAll(sector.getDigimons());
    }
    
    if (!allDigimon.isEmpty()) {
        for (Digimon digimon : allDigimon) {
            if (!(digimon instanceof CelestialDigimon)) {
                if (shouldDigimonDie(digimon)) {
                    Sector digimonSector = sectors.stream()
                        .filter(sector -> sector.getDigimons().contains(digimon))
                        .findFirst()
                        .orElse(null);

                    if (digimonSector != null) {
                        if (digimon.getTribe()!= null) {
                            digimon.leaveTribe();
                        }
                        digimonSector.removeDigimon(digimon);
                        LOGGER.info(digimon.getName() + " has died in " + digimonSector.getName());
                        SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has died in " + digimonSector.getName(), SimulationEvent.EventType.OTHER);
                    }
                }
            }

        }
    }
}


private boolean shouldDigimonDie(Digimon digimon) {
    int baseDeathChance = 1; // Reduced from 5% to 1% base chance of death
    int healthFactor = Math.max(0, 100 - digimon.getHealth()) / 4; // Reduced impact of health
    int evolutionStageFactor = getEvolutionStageFactor(digimon);
    Optional<Tribe> tribe = Optional.ofNullable(digimon.getTribe());
    
    int totalDeathChance = baseDeathChance + healthFactor - evolutionStageFactor;
    
    // Apply tribe bonus if the Digimon belongs to a tribe
    if (tribe.isPresent()) {
        totalDeathChance -= 1; // Tribe members are slightly more resilient
    }
    
    // Apply age factor
    if (digimon.getAge() < 10) {
        totalDeathChance += 1; // Very young Digimon are slightly more vulnerable
    } else if (digimon.getAge() > 50) {
        totalDeathChance += 2; // Old Digimon are more vulnerable
    }
    
    totalDeathChance = Math.max(0, Math.min(totalDeathChance, 50)); // Ensure chance is between 0% and 50%
    
    return random.nextInt(1000) < totalDeathChance * 10; // This gives more granularity
}

private int getEvolutionStageFactor(Digimon digimon) {
    return switch (digimon.getStage()) {
        case "Fresh" -> 0; // Most vulnerable
        case "In-Training" -> 1;
        case "Rookie" -> 2;
        case "Champion" -> 3;
        case "Ultimate" -> 4;
        case "Mega" -> 5; // Most resilient
        default -> 2; // Default to Rookie level resilience
    };
}

    /**
     * Finds a potential target for the attacking Digimon within the current sector or adjacent sectors.
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
        int currentAgeIndex = Arrays.asList(TechnologySystem.AGES).indexOf(technologySystem.getCurrentAge());
        return agesRequired.get(currentAgeIndex) - time;
    }

    public Tribe getTribeByName(Tribe tribe) {
        return tribes.stream()
               .filter(t -> t.getName().equalsIgnoreCase(tribe.getName()))
               .findFirst()
               .orElse(null);
    }

    public Set<Tribe> getTribes() {
        return tribes;
    }
    public void saveState() {
        boolean lockAcquired = false;
        try {
            lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping saveState operation.");
                return;
            }
            this.savedState = new WorldState(this);
            LOGGER.info("World state saved successfully.");
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while trying to acquire lock for saveState", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lockAcquired) {
                worldLock.writeLock().unlock();
            }
        }
    }

    /**
     * Resets the world to its initial state.
     */
    public void reset() {
        boolean lockAcquired = false;
        try {
            lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping reset operation.");
                return;
            }
            this.digimonList = new ArrayList<>();
            this.tribes = Tribe.getAllTribes();
            this.technologySystem = new TechnologySystem();
            this.time = 0;
            this.sectors = new ArrayList<>();
            this.random = new Random();
            initialize();
            LOGGER.info("World reset to initial state.");
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while trying to acquire lock for reset", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lockAcquired) {
                worldLock.writeLock().unlock();
            }
        }
    }

    /**
     * Loads a previously saved state of the world.
     */
    public void loadState() {
        boolean lockAcquired = false;
        try {
            lockAcquired = worldLock.writeLock().tryLock(5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOGGER.warning("Failed to acquire write lock within 5 seconds. Skipping loadState operation.");
                return;
            }
            if (this.savedState == null) {
                LOGGER.warning("No saved state available to load.");
                return;
            }
            this.digimonList = new ArrayList<>(savedState.digimonList);
            this.tribes = new HashSet<>(savedState.tribes);
            this.technologySystem = new TechnologySystem(savedState.technologySystem);
            this.time = savedState.time;
            this.sectors = new ArrayList<>(savedState.sectors);
            LOGGER.info("World state loaded successfully.");
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while trying to acquire lock for loadState", e);
            Thread.currentThread().interrupt();
        } finally {
            if (lockAcquired) {
                worldLock.writeLock().unlock();
            }
        }
    }

    public boolean isInitialized() {
        return digimonList != null && tribes != null && technologySystem != null && sectors != null;
    }

    private static class WorldState {
        private final List<Digimon> digimonList;
        private final List<Tribe> tribes;
        private final TechnologySystem technologySystem;
        private final int time;
        private final List<Sector> sectors;
    
        public WorldState(World world) {
            this.digimonList = new ArrayList<>(world.digimonList);
            this.tribes = new ArrayList<>(world.tribes);
            this.technologySystem = new TechnologySystem(world.technologySystem);
            this.time = world.time;
            this.sectors = new ArrayList<>(world.sectors);
        }
    }
}
