package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tribe {
    private static int nextId = 1;
    private int id;
    private String name;
    private List<Digimon> members;

    public Tribe(String name) {
        this.id = nextId;
        this.name = name;
        this.members = new ArrayList<>();
        nextId++;
    }

    public Tribe(int id,String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Digimon digimon) {
        members.add(digimon);
        digimon.joinTribe(this.name);
    }

    public static void formNewTribe(List<Digimon> digimonList, List<Tribe> tribes) {
        Random random = new Random();
        if (digimonList.size() >= 3) {
            Digimon leader = digimonList.get(random.nextInt(digimonList.size()));
            String tribeName = "Tribe of " + leader.getName();
            Tribe newTribe = new Tribe(nextId ,tribeName);
            nextId++;
            newTribe.addMember(leader);
            tribes.add(newTribe);
            VisualGUI.getInstance(null).addEvent(tribeName + " has been formed!", VisualGUI.EventType.POLITICAL);
        }
    }

    public static void buildCity(Tribe tribe) {
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
}
