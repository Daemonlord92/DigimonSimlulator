package com.horrorcore;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventSystem {
    private static final String[] POLITICAL_EVENTS = {
        "Alliance Formed", "War Declared", "Tribe Split", "New Leader Elected",
        "Convert Digimon", "Make Peace", "Trade Agreement", "Cultural Exchange",
        "Diplomatic Mission", "Espionage", "Form New Tribe", "Build City"
    };
    private static final String[] NATURAL_EVENTS = {
        "Food Shortage", "Plague", "Storm", "Earthquake", "Mass Birth"
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
                if (world.getTribes().size() >= 2 && Math.random() < 0.1) {
                    String politicalEvent = POLITICAL_EVENTS[random.nextInt(POLITICAL_EVENTS.length)];
                    SimulationSubject.getInstance().notifyEvent("Political Event: " + politicalEvent, SimulationEvent.EventType.POLITICAL);
                    handlePoliticalEvent(world, politicalEvent);
                    break;
                } else if (random.nextBoolean() && random.nextInt(10) % ((random.nextInt(10) + 1)) == 0) {
                    String politicalEvent = "Form New Tribe";
                    SimulationSubject.getInstance().notifyEvent("Political Event: " + politicalEvent, SimulationEvent.EventType.POLITICAL);
                    handlePoliticalEvent(world, politicalEvent);
                    break;
                } else if (random.nextBoolean() && world.getTribes().size() > 1) {
                    String politicalEvent = "Convert Digimon";
                    SimulationSubject.getInstance().notifyEvent("Political Event: " + politicalEvent, SimulationEvent.EventType.POLITICAL);
                    handlePoliticalEvent(world, politicalEvent);
                    break;
                }
            case 1:
                String naturalEvent = NATURAL_EVENTS[random.nextInt(NATURAL_EVENTS.length)];
                SimulationSubject.getInstance().notifyEvent("Natural Event: " + naturalEvent, SimulationEvent.EventType.OTHER);
                handleNaturalEvent(world, naturalEvent);
                break;
            case 2:
                SimulationSubject.getInstance().notifyEvent("Healing Event: A mysterious force heals all Digimon!", SimulationEvent.EventType.OTHER);
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
                        SimulationSubject.getInstance().notifyEvent("Trade agreement formed between " + tribe1.getName() + " and " + tribe2.getName(), SimulationEvent.EventType.POLITICAL);
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
                        SimulationSubject.getInstance().notifyEvent("Cultural exchange initiated between " + tribe1.getName() + " and " + tribe2.getName(), SimulationEvent.EventType.POLITICAL);
                        // Implement cultural exchange effects
                    }
                }
                break;
            case "Diplomatic Mission":
                if (!tribes.isEmpty()) {
                    Tribe tribe = tribes.get(random.nextInt(tribes.size()));
                    SimulationSubject.getInstance().notifyEvent(tribe.getName() + " has sent out a diplomatic mission", SimulationEvent.EventType.POLITICAL);
                    // Implement diplomatic mission effects
                    tribes.stream()
                            .flatMap(sTribe -> sTribe.getMembers().stream())
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
                        SimulationSubject.getInstance().notifyEvent(spyingTribe.getName() + " is spying on " + targetTribe.getName(), SimulationEvent.EventType.POLITICAL);
                        // Implement espionage effects

                    }
                }
                break;
            case "Convert Digimon":
                if (!tribes.isEmpty()) {
                    Tribe convertingTribe = tribes.get(random.nextInt(tribes.size()));
                    List<Digimon> unaffiliatedDigimon = tribes.stream()
                            .flatMap(tribe -> tribe.getMembers().stream())
                            .filter(digimon -> digimon.getTribe() != convertingTribe)
                            .toList();

                    if (!unaffiliatedDigimon.isEmpty()) {
                        Digimon convertedDigimon = unaffiliatedDigimon.get(random.nextInt(unaffiliatedDigimon.size()));
                        convertingTribe.addMember(convertedDigimon);
                        Politics.convertDigimon(convertedDigimon, convertingTribe);
                        SimulationSubject.getInstance().notifyEvent(convertedDigimon.getName() + " has been converted to " + convertingTribe.getName(), SimulationEvent.EventType.POLITICAL);

                        // Increase loyalty and decrease aggression of the converted Digimon
                        convertedDigimon.setAggression(Math.max(0, convertedDigimon.getAggression() - 25));
                    }
                }
                break;

            case "Make Peace":
                if (tribes.size() >= 2) {
                    Tribe tribe1 = tribes.get(random.nextInt(tribes.size()));
                    Tribe tribe2 = tribes.get(random.nextInt(tribes.size()));

                    SimulationSubject.getInstance().notifyEvent(tribe1.getName() + " and " + tribe2.getName() + " have made peace", SimulationEvent.EventType.POLITICAL);

                    // Decrease aggression and increase happiness for both tribes
                    Stream.concat(tribe1.getMembers().stream(), tribe2.getMembers().stream())
                            .forEach(digimon -> {
                                digimon.setAggression(Math.max(0, digimon.getAggression() - 50));
                            });
                }
                break;
                case "Form New Tribe":
                    Tribe.formNewTribe(world);
                    break;
                case "Build City":
                    Tribe tribe = tribes.stream().findAny().orElse(null);
                    if (tribe!= null) {
                        SimulationSubject.getInstance().notifyEvent(tribe.getName() + " has built a city", SimulationEvent.EventType.POLITICAL);
                        Tribe.buildCity(tribe);
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
        List<Digimon> allDigimon = world.getSectors().stream()
                .flatMap(sector -> sector.getDigimons().stream())
                .collect(Collectors.toList());
    
        for (Digimon digimon : allDigimon) {
            switch (event) {
                case "Food Shortage":
                    digimon.setAggression(digimon.getAggression() + 25);
                    digimon.setHunger(digimon.getHunger() + 30);
                    SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has been affected by a food shortage", SimulationEvent.EventType.OTHER);
                    break;
                case "Plague":
                    digimon.setHunger(digimon.getHunger() + 10);
                    digimon.setAggression(digimon.getAggression() + 10);
                    digimon.setHealth(digimon.getHealth() - 20);
                    SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has been affected by a plague", SimulationEvent.EventType.OTHER);
                    break;
                case "Storm":
                    digimon.setHealth(digimon.getHealth() - 15);
                    SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has been affected by a storm", SimulationEvent.EventType.OTHER);
                    break;
                case "Earthquake":
                    digimon.setHealth(digimon.getHealth() - 25);
                    digimon.setAggression(digimon.getAggression() + 15);
                    SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has been affected by an earthquake", SimulationEvent.EventType.OTHER);
                    break;
                case "Mass Birth":
                    for (int i = 0; i < random.nextInt(100); i++) {
                        world.getSectors().stream().findAny().orElse(world.getSectors().get(random.nextInt(world.getSectors().size())))
                                .getDigimons().add(DigimonGenerator.generateRandomDigimon());
                    }
                    SimulationSubject.getInstance().notifyEvent("A mass birth event has occurred!", SimulationEvent.EventType.OTHER);
                    return; // This will exit the method immediately after handling Mass Birth
            }
        }
    }
}