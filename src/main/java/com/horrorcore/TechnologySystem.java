package com.horrorcore;

public class TechnologySystem {
    public static final String[] AGES = {"Stone Age", "Bronze Age", "Iron Age", "Digital Age"};
    private int currentAgeIndex = 0;

    public void advanceAge() {
        if (currentAgeIndex < AGES.length - 1) {
            currentAgeIndex++;
            VisualGUI.getInstance(null).addEvent("The world has entered the " + AGES[currentAgeIndex] + "!", VisualGUI.EventType.OTHER);
        }
    }

    public String getCurrentAge() {
        return AGES[currentAgeIndex];
    }

}
