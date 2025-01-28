package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Tribe {
    private static int nextId = 1;
    private static final List<Tribe> allTribes = new ArrayList<>();

    private final int id;
    private String name;
    private List<Digimon> members;
    private Digimon leader;
    private int buildings;
    private final TechnologySystem technologySystem;
    private int totalFood;
    private int militaryStrength;
    private int researchPoints;

    // Constructors

    public Tribe(int id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
        this.buildings = 0;
        this.totalFood = 0;
        this.militaryStrength = 3;
        this.researchPoints = 0;
        this.technologySystem = new TechnologySystem();
        allTribes.add(this);
        nextId++;
    }

    // Static methods

    public static List<Tribe> getAllTribes() {
        return allTribes;
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

            for (int i = 0; i < 2 && !availableDigimon.isEmpty(); i++) {
                Digimon member = availableDigimon.remove(random.nextInt(availableDigimon.size()));
                newTribe.addMember(member);
            }

            world.getTribes().add(newTribe);

            Sector tribeSector = world.getSectors().parallelStream()
                    .filter(sector -> sector.getDigimons().contains(leader))
                    .findFirst()
                    .orElse(world.getSectors().get(0));

            VisualGUI.getInstance(null).addEvent(tribeName + " has been formed in " + tribeSector.getName() + "!", VisualGUI.EventType.POLITICAL);
        }
    }

    public static void buildCity(Tribe tribe) {
        tribe.buildings++;
        VisualGUI.getInstance(null).addEvent(tribe.getName() + " has built a city!", VisualGUI.EventType.POLITICAL);
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
