package com.horrorcore;

public class TechnologySystem {
    public static final String[] AGES = {"Stone Age", "Bronze Age", "Iron Age", "Digital Age"};
    private int currentAgeIndex = 0;

    /**
     * Advances the current age to the next one in the technology progression.
     * If the current age is not the last one, it increments the age index and
     * adds an event to the VisualGUI to notify about the new age.
     * 
     * This method does nothing if the current age is already the last one.
     */
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
