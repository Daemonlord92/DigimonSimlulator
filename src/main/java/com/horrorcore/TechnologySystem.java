package com.horrorcore;

import java.util.*;

public class TechnologySystem {
    public static final String[] AGES = {"Stone Age", "Bronze Age", "Iron Age", "Digital Age"};
    private int currentAgeIndex = 0;
    private final Map<String, Integer> technologyLevels;
    private final Map<String, List<Digimon>> professions;
    private int researchPoints = 0;

    public TechnologySystem() {
        technologyLevels = new HashMap<>();
        technologyLevels.put("Agriculture", 0);
        technologyLevels.put("Construction", 0);
        technologyLevels.put("Military", 0);
        technologyLevels.put("Science", 0);

        professions = new HashMap<>();
        professions.put("Farmer", new ArrayList<>());
        professions.put("Builder", new ArrayList<>());
        professions.put("Soldier", new ArrayList<>());
        professions.put("Scientist", new ArrayList<>());
    }

    public void advanceAge() {
        if (currentAgeIndex < AGES.length - 1) {
            currentAgeIndex++;
            VisualGUI.getInstance(null).addEvent("The world has entered the " + AGES[currentAgeIndex] + "!", VisualGUI.EventType.OTHER);
        }
    }

    public String getCurrentAge() {
        return AGES[currentAgeIndex];
    }

    public void assignProfession(Digimon digimon, String profession) {
        if (professions.containsKey(profession)) {
            String currentProfession = digimon.getProfession();
            if (currentProfession != null && currentProfession.equals(profession)) {
                // Digimon already has this profession
                return;
            }
            
            if (currentProfession != null) {
                // Remove from previous profession
                professions.get(currentProfession).remove(digimon);
            }
            
            professions.get(profession).add(digimon);
            digimon.setProfession(profession);
            VisualGUI.getInstance(null).addEvent(digimon.getName() + " has become a " + profession + "!", VisualGUI.EventType.OTHER);
        }
    }

    public void performWork(World world) {
        for (Tribe tribe : world.getTribes()) {
            int foodProduced = 0;
            int buildingsConstructed = 0;
            int militaryStrength = 0;
    
            for (Digimon digimon : tribe.getMembers()) {
                String profession = digimon.getProfession();
                if (profession == null) continue;
    
                switch (profession) {
                    case "Farmer":
                        foodProduced += 1 + (getTechnologyLevel("Agriculture") / 2);
                        break;
                    case "Builder":
                        buildingsConstructed += 1 + (getTechnologyLevel("Construction") / 2);
                        break;
                    case "Soldier":
                        militaryStrength += 1 + (getTechnologyLevel("Military") / 2);
                        break;
                    case "Scientist":
                        researchPoints += 1 + (getTechnologyLevel("Science") / 2);
                        break;
                }
            }
    
            // Apply the results of the work
            tribe.addFood(foodProduced);
            tribe.addBuildings(buildingsConstructed);
            tribe.setMilitaryStrength(tribe.getMilitaryStrength() + militaryStrength);
            tribe.addResearchPoints(researchPoints);
    
            // Log the results
            VisualGUI.getInstance(null).addEvent(tribe.getName() + " produced " + foodProduced + " food, constructed " + 
                                                 buildingsConstructed + " buildings, and increased military strength by " + 
                                                 militaryStrength + ".", VisualGUI.EventType.OTHER);
        }
    
        // Conduct research after all work is done
        conductResearch();
    }

    public void conductResearch() {
        if (researchPoints >= 10) {
            String[] technologies = {"Agriculture", "Construction", "Military", "Science"};
            String technology = technologies[new Random().nextInt(technologies.length)];
            technologyLevels.put(technology, technologyLevels.get(technology) + 1);
            researchPoints -= 10;
            VisualGUI.getInstance(null).addEvent("New advancement in " + technology + "! Level: " + technologyLevels.get(technology), VisualGUI.EventType.OTHER);
        }
    }

    public int getTechnologyLevel(String technology) {
        return technologyLevels.getOrDefault(technology, 0);
    }

    public List<Digimon> getWorkersInProfession(String profession) {
        return professions.getOrDefault(profession, new ArrayList<>());
    }

    public int getResearchPoints() {
        return researchPoints;
    }

    public int getCurrentAgeIndex() {
        return currentAgeIndex;
    }

    public void setCurrentAgeIndex(int currentAgeIndex) {
        this.currentAgeIndex = currentAgeIndex;
    }

    public Map<String, Integer> getTechnologyLevels() {
        return technologyLevels;
    }

    public Map<String, List<Digimon>> getProfessions() {
        return professions;
    }

    public void setResearchPoints(int researchPoints) {
        this.researchPoints = researchPoints;
    }
}
