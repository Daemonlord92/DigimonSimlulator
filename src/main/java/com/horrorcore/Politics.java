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
        VisualGUI.getInstance(null).addEvent(tribe1.getName() + " and " + tribe2.getName() + " have formed an alliance!", VisualGUI.EventType.POLITICAL);
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
                if (Math.random() < 0.3) {
                    declareWar(tribe, Math.random() < 0.5 ? attacker : defender);
                } else {
                    tribe.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 20));
                }
            });
    }

    private static void announceWar(Tribe attacker, Tribe defender) {
        VisualGUI.getInstance(null).addEvent(attacker.getName() + " has declared war on " + defender.getName() + "!", VisualGUI.EventType.POLITICAL);
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
        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has converted to the " + newTribe.getName() + " tribe.", VisualGUI.EventType.POLITICAL);
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
            defenders.parallelStream().forEach(defender -> {
                if (Math.random() < 0.1) {
                    battle(attacker, defender);
                }
            })
        );
    }

    // Battle methods
    private static void battle(Tribe attacker, Tribe defender) {
        World world = World.getInstance();
        Map<Sector, List<Digimon>> attackerForces = organizeForces(world, attacker);
        Map<Sector, List<Digimon>> defenderForces = organizeForces(world, defender);

        world.getSectors().forEach(sector -> 
            battleInSector(sector, attackerForces.get(sector), defenderForces.get(sector), attacker, defender)
        );
    }

    private static Map<Sector, List<Digimon>> organizeForces(World world, Tribe tribe) {
        return world.getSectors().stream()
            .collect(Collectors.toMap(
                sector -> sector,
                sector -> sector.getDigimons().stream()
                    .filter(d -> d.getTribe().equals(tribe.getName()))
                    .collect(Collectors.toList()),
                (v1, v2) -> v1,
                HashMap::new
            ));
    }

    private static void battleInSector(Sector sector, List<Digimon> attackers, List<Digimon> defenders, Tribe attacker, Tribe defender) {
        if (attackers != null && !attackers.isEmpty() && defenders != null && !defenders.isEmpty()) {
            int attackStrength = calculateForceStrength(attackers);
            int defenseStrength = calculateForceStrength(defenders);

            if (attackStrength > defenseStrength) {
                applyBattleDamage(defenders, 20);
                announceBattleResult(attacker, defender, sector, true);
            } else {
                applyBattleDamage(attackers, 20);
                announceBattleResult(attacker, defender, sector, false);
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
                VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been defeated in battle!", VisualGUI.EventType.POLITICAL);
            }
        });
    }

    private static void announceBattleResult(Tribe attacker, Tribe defender, Sector sector, boolean attackerWon) {
        String message = attackerWon
            ? attacker.getName() + " won a battle against " + defender.getName() + " in " + sector.getName() + "!"
            : defender.getName() + " successfully defended against " + attacker.getName() + " in " + sector.getName() + "!";
        VisualGUI.getInstance(null).addEvent(message, VisualGUI.EventType.POLITICAL);
    }
}