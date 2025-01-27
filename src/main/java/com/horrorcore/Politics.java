package com.horrorcore;

public class Politics {
    /**
     * Forms an alliance between two tribes.
     *
     * @param tribe1 The first tribe to form the alliance.
     * @param tribe2 The second tribe to form the alliance.
     */
    public static void formAlliance(Tribe tribe1, Tribe tribe2) {
        VisualGUI.getInstance(null).addEvent(tribe1.getName() + " and " + tribe2.getName() + " have formed an alliance!", VisualGUI.EventType.POLITICAL);
    }

    /**
     * Declares war between two tribes.
     *
     * @param tribe1 The tribe declaring war.
     * @param tribe2 The tribe being declared war upon.
     */
    public static void declareWar(Tribe tribe1, Tribe tribe2) {
        tribe1.getMembers().stream().forEach(digimon -> {
            digimon.setAggression(digimon.getAggression() + 75);
        });
        tribe2.getMembers().stream().forEach(digimon -> {
            digimon.setAggression(digimon.getAggression() + 75);
        });
        VisualGUI.getInstance(null).addEvent(tribe1.getName() + " has declared war on " + tribe2.getName() + "!", VisualGUI.EventType.POLITICAL);
    }

    /**
     * Converts a Digimon to a new tribe.
     *
     * @param digimon The Digimon to be converted.
     * @param newTribe The new tribe the Digimon is joining.
     */
    public static void convertDigimon(Digimon digimon, Tribe newTribe) {
        digimon.joinTribe(newTribe.getName());
        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has converted to the " + newTribe.getName() + " tribe.", VisualGUI.EventType.POLITICAL);
    }
}
