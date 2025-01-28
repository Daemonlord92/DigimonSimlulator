package com.horrorcore;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventSystem {
    private static final String[] POLITICAL_EVENTS = {
            "Alliance Formed", "War Declared", "Tribe Split", "New Leader Elected", 
            "Convert Digimon", "Make Peace", "Trade Agreement", "Cultural Exchange",
            "Diplomatic Mission", "Espionage"
    };
    private static final String[] NATURAL_EVENTS = {
            "Food Shortage", "Plague", "Storm", "Earthquake"
    };

    public static Random random = new Random();

    /**
     * Triggers a random event in the Digimon world, which can be political, natural, or healing.
     * The event is chosen randomly and its effects are applied to the Digimon and tribes.
     *
     * @param world using the current state of the Digimon world.
     */
    public static void triggerRandomEvent(World world) {
        int eventType = random.nextInt(3); // 0: Political, 1: Natural, 2: Healing

        switch (eventType) {
            case 0:
                String politicalEvent = POLITICAL_EVENTS[random.nextInt(POLITICAL_EVENTS.length)];
                VisualGUI.getInstance(null).addEvent("Political Event: " + politicalEvent, VisualGUI.EventType.POLITICAL);
                handlePoliticalEvent(world, politicalEvent);
                break;
            case 1:
                String naturalEvent = NATURAL_EVENTS[random.nextInt(NATURAL_EVENTS.length)];
                VisualGUI.getInstance(null).addEvent("Natural Event: " + naturalEvent, VisualGUI.EventType.OTHER);
                handleNaturalEvent(world, naturalEvent);
                break;
            case 2:
                VisualGUI.getInstance(null).addEvent("Healing Event: A mysterious force heals all Digimon!", VisualGUI.EventType.OTHER);
                world.getSectors().stream()
                        .flatMap(sector -> sector.getDigimons().stream())
                        .forEach(digimon -> digimon.setHealth(100));
                break;
        }
    }

    /**
     * Handles various political events that can occur between tribes in the Digimon world.
     * This method processes different types of political events such as forming alliances,
     * declaring war, making trade agreements, and more. Each event type has its own set of
     * effects on the tribes and their member Digimon.
     *
     * @param world Current instance of the Digimon world.
     * @param event A string representing the type of political event to be handled. Valid
     *              events include "Alliance Formed", "War Declared", "Trade Agreement",
     *              "Cultural Exchange", "Diplomatic Mission", "Espionage", "Convert Digimon",
     *              and "Make Peace".
     */
    private static void handlePoliticalEvent(World world, String event) {
        List<Tribe> tribes = world.getTribes();

        switch (event) {
            case "Alliance Formed":
                if (tribes.size() >= 2) {
                    Tribe tribeA = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribeB = tribes.get(random.nextInt(tribes.size()));
                    if (tribeA != tribeB) {
                        Politics.formAlliance(tribeA, tribeB);
                        world.getSectors().stream()
                                .flatMap(sector -> sector.getDigimons().stream())
                                .forEach(digimon -> {
                                    if (digimon.getTribe() == tribeA || digimon.getTribe() == tribeB) {
                                        digimon.setAggression(digimon.getAggression() + 100);
                                    } else if (digimon.getTribe() != null) {
                                        digimon.setAggression(digimon.getAggression() + 50);
                                    } else {
                                        digimon.setAggression(digimon.getAggression() + 26);
                                    }
                                });
                    }
                }
                break;
            case "War Declared":
                if (tribes.size() >= 2) {
                    Tribe tribeA = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribeB = tribes.get(random.nextInt(tribes.size()));
                    if (tribeA != tribeB) {
                        Politics.declareWar(tribeA, tribeB);
                        world.getSectors().stream()
                                .flatMap(sector -> sector.getDigimons().stream())
                                .forEach(digimon -> {
                                    if (digimon.getTribe() == tribeA || digimon.getTribe() == tribeB) {
                                        digimon.setAggression(digimon.getAggression() + 100);
                                    } else if (digimon.getTribe() != null) {
                                        digimon.setAggression(digimon.getAggression() + 50);
                                    } else {
                                        digimon.setAggression(digimon.getAggression() + 26);
                                    }
                                });
                    }
                }
                break;
            case "Trade Agreement":
                if (tribes.size() >= 2) {
                    Tribe tribe1 = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribe2 = tribes.get(random.nextInt(tribes.size()));
                    if (tribe1 != tribe2) {
                        VisualGUI.getInstance(null).addEvent("Trade agreement formed between " + tribe1.getName() + " and " + tribe2.getName(), VisualGUI.EventType.POLITICAL);
                        world.getSectors().stream()
                                .flatMap(sector -> sector.getDigimons().stream())
                                .forEach(digimon -> {
                                    if (digimon.getTribe() == tribe1) {
                                        digimon.setHunger(digimon.getHunger() - 20);
                                    } else if (digimon.getTribe() == tribe2) {
                                        digimon.setHunger(digimon.getHunger() + 20);
                                    }
                                });
                    }
                }
                break;
            case "Cultural Exchange":
                if (tribes.size() >= 2) {
                    Tribe tribe1 = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribe2 = tribes.get(random.nextInt(tribes.size()));
                    if (tribe1 != tribe2) {
                        VisualGUI.getInstance(null).addEvent("Cultural exchange initiated between " + tribe1.getName() + " and " + tribe2.getName(), VisualGUI.EventType.POLITICAL);
                        // Implement cultural exchange effects
                    }
                }
                break;
            case "Diplomatic Mission":
                if (!tribes.isEmpty()) {
                    Tribe tribe = tribes.get(random.nextInt(tribes.size()));
                    VisualGUI.getInstance(null).addEvent(tribe.getName() + " has sent out a diplomatic mission", VisualGUI.EventType.POLITICAL);
                    // Implement diplomatic mission effects
                    tribes.parallelStream()
                            .flatMap(sTribe -> sTribe.getMembers().parallelStream())
                            .forEach(digimon -> {
                                if (digimon.getTribe() == null) {
                                    digimon.setAggression(digimon.getAggression() - 20);
                                }
                            });
                }
                break;
            case "Espionage":
                if (tribes.size() >= 2) {
                    Tribe spyingTribe = tribes.get(random.nextInt(tribes.size()));
                    Tribe targetTribe = tribes.get(random.nextInt(tribes.size()));
                    if (spyingTribe != targetTribe) {
                        VisualGUI.getInstance(null).addEvent(spyingTribe.getName() + " is spying on " + targetTribe.getName(), VisualGUI.EventType.POLITICAL);
                        // Implement espionage effects

                    }
                }
                break;
            case "Convert Digimon":
                if (!tribes.isEmpty()) {
                    Tribe convertingTribe = tribes.get(random.nextInt(tribes.size()));
                    List<Digimon> unaffiliatedDigimon = tribes.parallelStream()
                            .flatMap(tribe -> tribe.getMembers().parallelStream())
                            .filter(digimon -> digimon.getTribe() == null)
                            .toList();

                    if (!unaffiliatedDigimon.isEmpty()) {
                        Digimon convertedDigimon = unaffiliatedDigimon.get(random.nextInt(unaffiliatedDigimon.size()));
                        convertingTribe.addMember(convertedDigimon);
                        Politics.convertDigimon(convertedDigimon, convertingTribe);
                        VisualGUI.getInstance(null).addEvent(convertedDigimon.getName() + " has been converted to " + convertingTribe.getName(), VisualGUI.EventType.POLITICAL);

                        // Increase loyalty and decrease aggression of the converted Digimon
                        convertedDigimon.setAggression(Math.max(0, convertedDigimon.getAggression() - 25));
                    }
                }
                break;

            case "Make Peace":
                if (tribes.size() >= 2) {
                    Tribe tribe1 = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribe2 = tribes.get(random.nextInt(tribes.size()));

                    VisualGUI.getInstance(null).addEvent(tribe1.getName() + " and " + tribe2.getName() + " have made peace", VisualGUI.EventType.POLITICAL);

                    // Decrease aggression and increase happiness for both tribes
                    Stream.concat(tribe1.getMembers().stream(), tribe2.getMembers().stream())
                            .forEach(digimon -> {
                                digimon.setAggression(Math.max(0, digimon.getAggression() - 50));
                            });
                }
                break;
        }
    }

    /**
     * Handles the effects of natural events on the Digimon population.
     * This method applies the consequences of various natural disasters or phenomena
     * to each Digimon in the provided list. Currently, it handles two types of events:
     * "Food Shortage" and "Plague".
     *
     * @param world Current state of the Digimon World.
     * @param event A String representing the type of natural event occurring. 
     *              Valid values are "Food Shortage" and "Plague".
     *              For "Food Shortage", Digimon's aggression and hunger increase.
     *              For "Plague", Digimon's hunger and aggression increase, while health decreases.
     */
    private static void handleNaturalEvent(World world, String event) {
        world.getSectors().stream()
                .flatMap(sector -> sector.getDigimons().stream())
                .forEach(digimon -> {
                    if (event.equals("Food Shortage")) {
                        digimon.setAggression(digimon.getAggression() + 25);
                        digimon.setHunger(digimon.getHunger() + 30);
                        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been affected by a food shortage", VisualGUI.EventType.OTHER);
                    } else if (event.equals("Plague")) {
                        digimon.setHunger(digimon.getHunger() + 10);
                        digimon.setAggression(digimon.getAggression() + 10);
                        digimon.setHealth(digimon.getHealth() - 20);
                        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been affected by a plague", VisualGUI.EventType.OTHER);
                    } else if (event.equals("Storm")) {
                        digimon.setHealth(digimon.getHealth() - 15);
                        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been affected by a storm", VisualGUI.EventType.OTHER);
                    } else if (event.equals("Earthquake")) {
                        digimon.setHealth(digimon.getHealth() - 25);
                        digimon.setAggression(digimon.getAggression() + 15);
                        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been affected by an earthquake", VisualGUI.EventType.OTHER);
                    }
                });
    }
}