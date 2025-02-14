package com.horrorcore.entity;

import com.horrorcore.*;
import com.horrorcore.systems.building.BuildingSystem;
import com.horrorcore.systems.events.SimulationEvent;
import com.horrorcore.systems.events.SimulationSubject;
import com.horrorcore.systems.tech.TechnologySystem;

import java.util.*;

public class Tribe {
    private static int nextId = 1;

    private final int id;
    private String name;
    private List<Digimon> members;
    private Digimon leader;
    private int buildings;
    private final TechnologySystem technologySystem;
    private int totalFood;
    private int militaryStrength;
    private int researchPoints;
    private final Set<Digimon> recentlyFed = new HashSet<>();
    private long lastFeedTime = 0;
    private static final long FEED_COOLDOWN = 1000;

    // Constructors

    public Tribe(int id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
        this.buildings = 0;
        this.totalFood = 150;
        this.militaryStrength = 3;
        this.researchPoints = 0;
        this.technologySystem = new TechnologySystem();
        nextId++;
    }

    // Static methods

    public static Set<Tribe> getAllTribes() {
        return World.getInstance().getTribes();
    }

    public static void formNewTribe(World world) {
        Random random = new Random();
        List<Digimon> availableDigimon = new ArrayList<>();

        for (Sector sector : world.getSectors()) {
            availableDigimon.addAll(sector.getDigimons().stream()
                    .filter(digimon -> digimon.getTribe() == null)
                    .toList());
        }

        if (availableDigimon.size() >= 2) {
            Digimon leader = availableDigimon.get(random.nextInt(availableDigimon.size()));
            String tribeName = "Tribe of " + leader.getName();
            Tribe newTribe = new Tribe(nextId, tribeName);

            newTribe.setLeader(leader);
            availableDigimon.remove(leader);
            leader.setProfession("Farmer");

            for (int i = 0; i < 2 && !availableDigimon.isEmpty(); i++) {
                Digimon member = availableDigimon.remove(random.nextInt(availableDigimon.size()));
                member.setProfession("Farmer");
                newTribe.addMember(member);
            }

            world.getTribes().add(newTribe);

            Sector tribeSector = world.getSectors().stream()
                    .filter(sector -> sector.getDigimons().contains(leader))
                    .findFirst()
                    .orElse(world.getSectors().get(0));

            SimulationSubject.getInstance().notifyEvent(tribeName + " has been formed in " + tribeSector.getName() + "!", SimulationEvent.EventType.POLITICAL);
        }
    }

    public static void buildCity(Tribe tribe) {
        if (BuildingSystem.buildCity(tribe, World.getInstance())) {
            tribe.buildings++;
            SimulationSubject.getInstance().notifyEvent(
                    tribe.getName() + " has successfully built a new city with surrounding buildings!",
                    SimulationEvent.EventType.POLITICAL
            );
        } else {
            SimulationSubject.getInstance().notifyEvent(
                    tribe.getName() + " failed to find a suitable location for a new city.",
                    SimulationEvent.EventType.POLITICAL
            );
        }
    }

    // Instance methods

    public void addMember(Digimon digimon) {
        militaryStrength++;
        digimon.setTribe(this);
        members.add(digimon);
        digimon.joinTribe(this.name);
    }

    public void removeMember(Digimon digimon) {
        members.removeIf(digi -> digi.equals(digimon));
    }

    public void addFood(int foodProduced) {
        totalFood += foodProduced;
    }

    public void addBuildings(int buildingsConstructed) {
        buildings += buildingsConstructed;
    }

    public void feedTribe() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFeedTime < FEED_COOLDOWN) {
            return;
        }

        int baseFoodPerMember = 5;
        int foodToFeed = members.size() * baseFoodPerMember;
        int foodThreshold = members.size() * baseFoodPerMember * 3; // 3 days worth of food

        if (totalFood >= foodToFeed) {
            if (totalFood > foodThreshold) {
                // If we have more than the threshold, consume more food
                int extraFood = Math.min((totalFood - foodThreshold) / 2, foodToFeed);
                totalFood -= (foodToFeed + extraFood);
                members.stream()
                        .filter(digimon -> !recentlyFed.contains(digimon))
                        .forEach(digimon -> {
                            digimon.setHunger(Math.max(0, digimon.getHunger() - 30));
                            recentlyFed.add(digimon);
                        });
                SimulationSubject.getInstance().notifyEvent(getName() + " has well fed their tribe with extra food!", SimulationEvent.EventType.POLITICAL);
            } else {
                // Regular feeding
                totalFood -= foodToFeed;
                members.stream()
                        .filter(digimon -> !recentlyFed.contains(digimon))
                        .forEach(digimon -> {
                            digimon.setHunger(Math.max(0, digimon.getHunger() - 20));
                            recentlyFed.add(digimon);
                        });
                SimulationSubject.getInstance().notifyEvent(
                        getName() + " has fed their tribe!",
                        SimulationEvent.EventType.POLITICAL
                );
            }
            lastFeedTime = currentTime;
        } else if (totalFood > 0) {
            // Not enough food, but feed what we can
            int partialFood = totalFood;
            totalFood = 0;
            members.stream()
                    .filter(digimon -> !recentlyFed.contains(digimon))
                    .forEach(digimon -> {
                        digimon.setHunger(Math.max(0, digimon.getHunger() - 10));
                        recentlyFed.add(digimon);
                    });
            SimulationSubject.getInstance().notifyEvent(
                    getName() + " has partially fed their tribe with " + partialFood + " food!",
                    SimulationEvent.EventType.POLITICAL
            );
            lastFeedTime = currentTime;
        } else {
            SimulationSubject.getInstance().notifyEvent(getName() + " has no food to feed their tribe!", SimulationEvent.EventType.POLITICAL);
        }
    }

    public void clearFeedingStatus() {
        recentlyFed.clear();
    }

    public void produceFood() {
        int baseProduction = 10;
        int farmersCount = this.technologySystem.getProfessions().getOrDefault("Agriculture", new ArrayList<>()).size();
        int initialProduction = farmersCount * baseProduction;

        double technologyBonus = 1 + (this.technologySystem.getTechnologyLevel("Agriculture") * 0.1);
        int foodProduced = (int) (initialProduction * technologyBonus);

        addFood(foodProduced);
        SimulationSubject.getInstance().notifyEvent(getName() + " has produced " + foodProduced + " food!", SimulationEvent.EventType.POLITICAL);
    }

    public void addResearchPoints(int points) {
        this.researchPoints += points;
    }

    // Getters and Setters

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Digimon getLeader() { return leader; }
    public void setLeader(Digimon leader) { this.leader = leader; }

    public List<Digimon> getMembers() { return members; }
    public void setMembers(List<Digimon> members) { this.members = members; }

    public int getBuildings() { return buildings; }
    public void setBuildings(int buildings) { this.buildings = buildings; }

    public int getTotalFood() { return totalFood; }
    public void setTotalFood(int totalFood) { this.totalFood = totalFood; }

    public int getMilitaryStrength() { return militaryStrength; }
    public void setMilitaryStrength(int militaryStrength) { this.militaryStrength = militaryStrength; }

    public int getResearchPoints() { return researchPoints; }
    public void setResearchPoints(int researchPoints) { this.researchPoints = researchPoints; }

    public TechnologySystem getTechnologySystem() { return technologySystem; }

    public static int getNextId() { return nextId; }
    public static void setNextId(int nextId) { Tribe.nextId = nextId; }

    // Object overrides

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tribe tribe)) return false;
        return id == tribe.id && 
               buildings == tribe.buildings && 
               Objects.equals(name, tribe.name) && 
               Objects.equals(members, tribe.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, members, buildings);
    }
}
