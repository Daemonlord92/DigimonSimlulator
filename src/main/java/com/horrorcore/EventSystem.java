package com.horrorcore;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class EventSystem {
    private static final Random random = new Random();

    private static final String[] POLITICAL_EVENTS = {
        "Alliance Formed", "War Declared", "Tribe Split", "New Leader Elected", 
        "Convert Digimon", "Make Peace", "Trade Agreement", "Cultural Exchange",
        "Diplomatic Mission", "Espionage", "Form New Tribe", "Build City"
    };

    private static final String[] NATURAL_EVENTS = {
        "Food Shortage", "Plague", "Storm", "Earthquake"
    };

    public static void triggerRandomEvent(World world) {
        int eventType = random.nextInt(3); // 0: Political, 1: Natural, 2: Healing

        switch (eventType) {
            case 0:
                handlePoliticalEvent(world);
                break;
            case 1:
                handleNaturalEvent(world);
                break;
            case 2:
                handleHealingEvent(world);
                break;
        }
    }

    private static void handlePoliticalEvent(World world) {
        String politicalEvent = POLITICAL_EVENTS[random.nextInt(POLITICAL_EVENTS.length)];
        VisualGUI.getInstance(null).addEvent("Political Event: " + politicalEvent, VisualGUI.EventType.POLITICAL);

        switch (politicalEvent) {
            case "Alliance Formed":
                handleAllianceFormed(world);
                break;
            case "War Declared":
                handleWarDeclared(world);
                break;
            case "Trade Agreement":
                handleTradeAgreement(world);
                break;
            case "Cultural Exchange":
                handleCulturalExchange(world);
                break;
            case "Diplomatic Mission":
                handleDiplomaticMission(world);
                break;
            case "Espionage":
                handleEspionage(world);
                break;
            case "Convert Digimon":
                handleConvertDigimon(world);
                break;
            case "Make Peace":
                handleMakePeace(world);
                break;
            case "Form New Tribe":
                Tribe.formNewTribe(world);
                break;
            case "Build City":
                handleBuildCity(world);
                break;
            // Add other cases as needed
        }
    }

    private static void handleNaturalEvent(World world) {
        String naturalEvent = NATURAL_EVENTS[random.nextInt(NATURAL_EVENTS.length)];
        VisualGUI.getInstance(null).addEvent("Natural Event: " + naturalEvent, VisualGUI.EventType.OTHER);

        world.getSectors().stream()
            .flatMap(sector -> sector.getDigimons().stream())
            .forEach(digimon -> applyNaturalEventEffect(digimon, naturalEvent));
    }

    private static void handleHealingEvent(World world) {
        VisualGUI.getInstance(null).addEvent("Healing Event: A mysterious force heals all Digimon!", VisualGUI.EventType.OTHER);
        world.getSectors().stream()
            .flatMap(sector -> sector.getDigimons().stream())
            .forEach(digimon -> digimon.setHealth(100));
    }

    private static void handleAllianceFormed(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe tribeA = getRandomTribe(tribes);
            Tribe tribeB = getRandomTribe(tribes);
            if (tribeA != tribeB) {
                Politics.formAlliance(tribeA, tribeB);
                applyAllianceEffects(world, tribeA, tribeB);
            }
        }
    }

    private static void handleWarDeclared(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe tribeA = getRandomTribe(tribes);
            Tribe tribeB = getRandomTribe(tribes);
            if (tribeA != tribeB) {
                Politics.declareWar(tribeA, tribeB);
                applyWarEffects(world, tribeA, tribeB);
            }
        }
    }

    private static void handleTradeAgreement(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe tribe1 = getRandomTribe(tribes);
            Tribe tribe2 = getRandomTribe(tribes);
            if (tribe1 != tribe2) {
                VisualGUI.getInstance(null).addEvent("Trade agreement formed between " + tribe1.getName() + " and " + tribe2.getName(), VisualGUI.EventType.POLITICAL);
                applyTradeAgreementEffects(world, tribe1, tribe2);
            }
        }
    }

    private static void handleCulturalExchange(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe tribe1 = getRandomTribe(tribes);
            Tribe tribe2 = getRandomTribe(tribes);
            if (tribe1 != tribe2) {
                VisualGUI.getInstance(null).addEvent("Cultural exchange initiated between " + tribe1.getName() + " and " + tribe2.getName(), VisualGUI.EventType.POLITICAL);
                // Implement cultural exchange effects
            }
        }
    }

    private static void handleDiplomaticMission(World world) {
        List<Tribe> tribes = world.getTribes();
        if (!tribes.isEmpty()) {
            Tribe tribe = getRandomTribe(tribes);
            VisualGUI.getInstance(null).addEvent(tribe.getName() + " has sent out a diplomatic mission", VisualGUI.EventType.POLITICAL);
            applyDiplomaticMissionEffects(world);
        }
    }

    private static void handleEspionage(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe spyingTribe = getRandomTribe(tribes);
            Tribe targetTribe = getRandomTribe(tribes);
            if (spyingTribe != targetTribe) {
                VisualGUI.getInstance(null).addEvent(spyingTribe.getName() + " is spying on " + targetTribe.getName(), VisualGUI.EventType.POLITICAL);
                // Implement espionage effects
            }
        }
    }

    private static void handleConvertDigimon(World world) {
        List<Tribe> tribes = world.getTribes();
        if (!tribes.isEmpty()) {
            Tribe convertingTribe = getRandomTribe(tribes);
            List<Digimon> unaffiliatedDigimon = getUnaffiliatedDigimon(tribes);
            if (!unaffiliatedDigimon.isEmpty()) {
                Digimon convertedDigimon = getRandomDigimon(unaffiliatedDigimon);
                convertDigimon(convertedDigimon, convertingTribe);
            }
        }
    }

    private static void handleMakePeace(World world) {
        List<Tribe> tribes = world.getTribes();
        if (tribes.size() >= 2) {
            Tribe tribe1 = getRandomTribe(tribes);
            Tribe tribe2 = getRandomTribe(tribes);
            VisualGUI.getInstance(null).addEvent(tribe1.getName() + " and " + tribe2.getName() + " have made peace", VisualGUI.EventType.POLITICAL);
            applyPeaceEffects(tribe1, tribe2);
        }
    }

    private static void handleBuildCity(World world) {
        List<Tribe> tribes = world.getTribes();
        if (!tribes.isEmpty()) {
            Tribe.buildCity(getRandomTribe(tribes));
            VisualGUI.getInstance(null).addEvent("A new city has been built", VisualGUI.EventType.POLITICAL);
        } else {
            VisualGUI.getInstance(null).addEvent("Can't build a city without a tribe", VisualGUI.EventType.POLITICAL);
        }
    }

    private static void applyNaturalEventEffect(Digimon digimon, String event) {
        switch (event) {
            case "Food Shortage":
                digimon.setAggression(digimon.getAggression() + 25);
                digimon.setHunger(digimon.getHunger() + 30);
                break;
            case "Plague":
                digimon.setHunger(digimon.getHunger() + 10);
                digimon.setAggression(digimon.getAggression() + 10);
                digimon.setHealth(digimon.getHealth() - 20);
                break;
            case "Storm":
                digimon.setHealth(digimon.getHealth() - 15);
                break;
            case "Earthquake":
                digimon.setHealth(digimon.getHealth() - 25);
                digimon.setAggression(digimon.getAggression() + 15);
                break;
        }
        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been affected by " + event, VisualGUI.EventType.OTHER);
    }

    private static Tribe getRandomTribe(List<Tribe> tribes) {
        return tribes.get(random.nextInt(tribes.size()));
    }

    private static Digimon getRandomDigimon(List<Digimon> digimons) {
        return digimons.get(random.nextInt(digimons.size()));
    }

    private static List<Digimon> getUnaffiliatedDigimon(List<Tribe> tribes) {
        return tribes.parallelStream()
            .flatMap(tribe -> tribe.getMembers().parallelStream())
            .filter(digimon -> digimon.getTribe() == null)
            .toList();
    }

    private static void applyAllianceEffects(World world, Tribe tribeA, Tribe tribeB) {
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

    private static void applyWarEffects(World world, Tribe tribeA, Tribe tribeB) {
        applyAllianceEffects(world, tribeA, tribeB); // War effects are currently the same as alliance effects
    }

    private static void applyTradeAgreementEffects(World world, Tribe tribe1, Tribe tribe2) {
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
    private static void applyDiplomaticMissionEffects(World world) {
        world.getTribes().parallelStream()
            .flatMap(tribe -> tribe.getMembers().parallelStream())
            .forEach(digimon -> {
                if (digimon.getTribe() == null) {
                    digimon.setAggression(digimon.getAggression() - 20);
                }
            });
    }

    private static void convertDigimon(Digimon convertedDigimon, Tribe convertingTribe) {
        convertingTribe.addMember(convertedDigimon);
        Politics.convertDigimon(convertedDigimon, convertingTribe);
        VisualGUI.getInstance(null).addEvent(convertedDigimon.getName() + " has been converted to " + convertingTribe.getName(), VisualGUI.EventType.POLITICAL);

        // Increase loyalty and decrease aggression of the converted Digimon
        convertedDigimon.setAggression(Math.max(0, convertedDigimon.getAggression() - 25));
    }

    private static void applyPeaceEffects(Tribe tribe1, Tribe tribe2) {
        Stream.concat(tribe1.getMembers().stream(), tribe2.getMembers().stream())
            .forEach(digimon -> {
                digimon.setAggression(Math.max(0, digimon.getAggression() - 50));
            });
    }


}