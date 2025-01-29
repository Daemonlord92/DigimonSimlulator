package com.horrorcore;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class Politics {
    private static final Map<Tribe, Set<Tribe>> alliances = new ConcurrentHashMap<>();
    private static final Map<Tribe, Set<Tribe>> wars = new ConcurrentHashMap<>();

    // Alliance methods
    public static void formAlliance(Tribe tribe1, Tribe tribe2) {
        addAlliance(tribe1, tribe2);
        increaseFriendshipBetweenAllies(tribe1, tribe2);
        increaseAggressionOfOtherTribes(tribe1, tribe2);
        announceAlliance(tribe1, tribe2);
    }

    private static void addAlliance(Tribe tribe1, Tribe tribe2) {
        alliances.computeIfAbsent(tribe1, k -> new HashSet<>()).add(tribe2);
        alliances.computeIfAbsent(tribe2, k -> new HashSet<>()).add(tribe1);
    }

    private static void increaseFriendshipBetweenAllies(Tribe tribe1, Tribe tribe2) {
        for (Digimon digimon1 : tribe1.getMembers()) {
            for (Digimon digimon2 : tribe2.getMembers()) {
                digimon1.increaseFriendship(digimon2, 20);
                digimon2.increaseFriendship(digimon1, 20);
            }
        }
    }

    private static void increaseAggressionOfOtherTribes(Tribe tribe1, Tribe tribe2) {
        World.getInstance().getTribes().stream()
            .filter(tribe -> tribe != tribe1 && tribe != tribe2)
            .forEach(tribe -> tribe.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 10)));
    }

    private static void announceAlliance(Tribe tribe1, Tribe tribe2) {
        SimulationSubject.getInstance().notifyEvent(tribe1.getName() + " and " + tribe2.getName() + " have formed an alliance!", SimulationEvent.EventType.POLITICAL);
    }

    // War methods
    public static void declareWar(Tribe attacker, Tribe defender) {
        addWar(attacker, defender);
        increaseAggressionOfWarringTribes(attacker, defender);
        involveAllies(attacker, defender);
        otherTribesReact(attacker, defender);
        announceWar(attacker, defender);
    }

    private static void addWar(Tribe attacker, Tribe defender) {
        wars.computeIfAbsent(attacker, k -> new HashSet<>()).add(defender);
        wars.computeIfAbsent(defender, k -> new HashSet<>()).add(attacker);
    }

    private static void increaseAggressionOfWarringTribes(Tribe attacker, Tribe defender) {
        attacker.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 75));
        defender.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 75));
    }

    private static void involveAllies(Tribe attacker, Tribe defender) {
        Set<Tribe> attackerAllies = alliances.getOrDefault(attacker, new HashSet<>());
        Set<Tribe> defenderAllies = alliances.getOrDefault(defender, new HashSet<>());

        attackerAllies.stream()
            .filter(ally -> !wars.getOrDefault(ally, new HashSet<>()).contains(defender))
            .forEach(ally -> declareWar(ally, defender));

        defenderAllies.stream()
            .filter(ally -> !wars.getOrDefault(ally, new HashSet<>()).contains(attacker))
            .forEach(ally -> declareWar(ally, attacker));
    }

    private static void otherTribesReact(Tribe attacker, Tribe defender) {
        Set<Tribe> attackerAllies = alliances.getOrDefault(attacker, new HashSet<>());
        Set<Tribe> defenderAllies = alliances.getOrDefault(defender, new HashSet<>());

        World.getInstance().getTribes().stream()
            .filter(tribe -> tribe != attacker && tribe != defender && !attackerAllies.contains(tribe) && !defenderAllies.contains(tribe))
            .forEach(tribe -> {
                tribe.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 20));
            });
    }

    private static void announceWar(Tribe attacker, Tribe defender) {
        SimulationSubject.getInstance().notifyEvent(attacker.getName() + " has declared war on " + defender.getName() + "!", SimulationEvent.EventType.POLITICAL);
    }

    // Conversion methods
    public static void convertDigimon(Digimon digimon, Tribe newTribe) {
        Tribe oldTribe = World.getInstance().getTribeByName(digimon.getTribe());
        updateTribeMembership(digimon, oldTribe, newTribe);
        updateRelationships(digimon, oldTribe, newTribe);
        announceConversion(digimon, newTribe);
    }

    private static void updateTribeMembership(Digimon digimon, Tribe oldTribe, Tribe newTribe) {
        if (oldTribe != null) {
            oldTribe.removeMember(digimon);
        }
        digimon.joinTribe(newTribe.getName());
        newTribe.addMember(digimon);
    }

    private static void updateRelationships(Digimon digimon, Tribe oldTribe, Tribe newTribe) {
        newTribe.getMembers().stream()
            .filter(member -> member != digimon)
            .forEach(member -> {
                digimon.increaseFriendship(member, 30);
                member.increaseFriendship(digimon, 30);
            });

        if (oldTribe != null) {
            oldTribe.getMembers().forEach(oldMember -> oldMember.decreaseFriendship(digimon, 20));
        }
    }

    private static void announceConversion(Digimon digimon, Tribe newTribe) {
        SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has converted to the " + newTribe.getName() + " tribe.", SimulationEvent.EventType.POLITICAL);
    }

    // Political situation update methods
    public static void updatePoliticalSituation() {
        handleRandomPoliticalEvents();
        updateWarSituations();
    }

    private static void handleRandomPoliticalEvents() {
        List<Tribe> tribes = World.getInstance().getTribes();
        if (tribes.size() >= 2 && Math.random() < 0.1) {
            Tribe tribe1 = getRandomTribe(tribes);
            Tribe tribe2 = getRandomTribe(tribes);
            if (tribe1 != tribe2) {
                if (Math.random() < 0.6) {
                    formAlliance(tribe1, tribe2);
                } else {
                    declareWar(tribe1, tribe2);
                }
            }
        }
    }

    private static Tribe getRandomTribe(List<Tribe> tribes) {
        return tribes.get((int) (Math.random() * tribes.size()));
    }

    private static void updateWarSituations() {
        wars.forEach((attacker, defenders) ->
            defenders.stream().forEach(defender -> {
                if (Math.random() < 0.1) {
                    battle(attacker, defender);
                }
            })
        );
    }

    // Battle methods

        private static void battle(Tribe attacker, Tribe defender) {
            World world = World.getInstance();
            Map<Sector, List<Digimon>> attackerForces = new HashMap<>();
            Map<Sector, List<Digimon>> defenderForces = new HashMap<>();
        
            // Organize forces by sector
            for (Sector sector : world.getSectors()) {
                List<Digimon> attackersInSector = sector.getDigimons().stream()
                        .filter(d -> d.getTribe() != null && d.getTribe().equals(attacker.getName()))
                        .collect(Collectors.toList());
                List<Digimon> defendersInSector = sector.getDigimons().stream()
                        .filter(d -> d.getTribe() != null && d.getTribe().equals(defender.getName()))
                        .collect(Collectors.toList());
        
                if (!attackersInSector.isEmpty()) {
                    attackerForces.put(sector, attackersInSector);
                }
                if (!defendersInSector.isEmpty()) {
                    defenderForces.put(sector, defendersInSector);
                }
            }
        
            // Rest of the battle method...


        // Battle in each sector
        for (Sector sector : world.getSectors()) {
            List<Digimon> sectorAttackers = attackerForces.getOrDefault(sector, new ArrayList<>());
            List<Digimon> sectorDefenders = defenderForces.getOrDefault(sector, new ArrayList<>());

            if (!sectorAttackers.isEmpty() && !sectorDefenders.isEmpty()) {
                int attackStrength = calculateForceStrength(sectorAttackers);
                int defenseStrength = calculateForceStrength(sectorDefenders);

                if (attackStrength > defenseStrength) {
                    // Attackers win in this sector
                    applyBattleDamage(sectorDefenders, 20);
                    SimulationSubject.getInstance().notifyEvent(attacker.getName() + " won a battle against " + defender.getName() + " in " + sector.getName() + "!", SimulationEvent.EventType.POLITICAL);
                } else {
                    // Defenders win in this sector
                    applyBattleDamage(sectorAttackers, 20);
                    SimulationSubject.getInstance().notifyEvent(defender.getName() + " successfully defended against " + attacker.getName() + " in " + sector.getName() + "!", SimulationEvent.EventType.POLITICAL);
                }
            }
        }
    }

    private static int calculateForceStrength(List<Digimon> force) {
        return force.stream()
                .mapToInt(digimon -> digimon.getHealth() + digimon.getAggression())
                .sum();
    }

    private static void applyBattleDamage(List<Digimon> force, int damage) {
        force.forEach(digimon -> {
            int newHealth = Math.max(0, digimon.getHealth() - damage);
            digimon.setHealth(newHealth);
            if (newHealth == 0) {
                SimulationSubject.getInstance().notifyEvent(digimon.getName() + " has been defeated in battle!", SimulationEvent.EventType.POLITICAL);
            }
        });
    }
}
