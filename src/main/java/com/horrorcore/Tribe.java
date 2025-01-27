package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tribe {
    private static int nextId = 1;
    private int id;
    private String name;
    private List<Digimon> members;
    private int buildings = 0;
    private static List<Tribe> allTribes = new ArrayList<>();

    /**
     * Constructs a new Tribe with a given name and automatically assigned ID.
     * 
     * This constructor initializes a new Tribe object, setting its name,
     * creating an empty list of members, and assigning it the next available ID.
     * The ID counter is then incremented for future Tribe objects.
     *
     * @param name The name of the tribe to be created.
     */
    public Tribe(String name) {
        this.id = nextId;
        this.name = name;
        this.members = new ArrayList<>();
        nextId++;
    }

    /**
     * Constructs a new Tribe with a specified ID and name.
     * 
     * This constructor initializes a new Tribe object with a given ID and name,
     * and creates an empty list to store its members.
     *
     * @param id   The unique identifier for the tribe.
     * @param name The name of the tribe to be created.
     */
    public Tribe(int id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
    }

    public static List<Tribe> getAllTribes() {
        return allTribes;
    }

    /**
     * Adds a new Digimon member to the tribe.
     * 
     * This method adds the specified Digimon to the tribe's list of members
     * and updates the Digimon's tribe affiliation.
     *
     * @param digimon The Digimon to be added to the tribe.
     */
    public void addMember(Digimon digimon) {
        members.add(digimon);
        digimon.joinTribe(this.name);
    }

    /**
     * Forms a new tribe from a list of Digimon if there are at least three Digimon available.
     * 
     * This method randomly selects a leader from the provided list of Digimon,
     * creates a new tribe named after the leader, adds the leader to the new tribe,
     * and adds the new tribe to the list of existing tribes. It also announces the
     * formation of the new tribe through the VisualGUI.
     *
     * @param digimonList A list of Digimon from which to form the new tribe.
     *                    Must contain at least three Digimon for a tribe to be formed.
     * @param tribes      The list of existing tribes to which the new tribe will be added.
     */
    public static void formNewTribe(List<Digimon> digimonList, List<Tribe> tribes) {
        Random random = new Random();
        if (digimonList.size() >= 3) {
            Digimon leader = digimonList.get(random.nextInt(digimonList.size()));
            String tribeName = "Tribe of " + leader.getName();
            Tribe newTribe = new Tribe(nextId ,tribeName);
            nextId++;
            newTribe.addMember(leader);
            tribes.add(newTribe);
            Tribe.allTribes.add(newTribe);
            VisualGUI.getInstance(null).addEvent(tribeName + " has been formed!", VisualGUI.EventType.POLITICAL);
        }
    }

    /**
     * Builds a city for the specified tribe and announces the event.
     * 
     * This method increments the number of buildings for the given tribe
     * and adds a political event to the VisualGUI to announce the city's construction.
     *
     * @param tribe The tribe for which a city is being built.
     */
    public static void buildCity(Tribe tribe) {
        tribe.buildings++;
        VisualGUI.getInstance(null).addEvent(tribe.getName() + " has built a city!", VisualGUI.EventType.POLITICAL);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Digimon> getMembers() {
        return members;
    }

    public void setMembers(List<Digimon> members) {
        this.members = members;
    }

    public int getBuildings() {
        return buildings;
    }
}
