package com.horrorcore;

import java.util.*;
import java.util.stream.Collectors;

public class Politics {
    private static Map<Tribe, Set<Tribe>> alliances = new HashMap<>();
    private static Map<Tribe, Set<Tribe>> wars = new HashMap<>();

    /**
     * Forms an alliance between two tribes.
     *
     * @param tribe1 The first tribe to form the alliance.
     * @param tribe2 The second tribe to form the alliance.
     */
    public static void formAlliance(Tribe tribe1, Tribe tribe2) {
        alliances.computeIfAbsent(tribe1, k -> new HashSet<>()).add(tribe2);
        alliances.computeIfAbsent(tribe2, k -> new HashSet<>()).add(tribe1);

        // Increase friendship between members of allied tribes
        for (Digimon digimon1 : tribe1.getMembers()) {
            for (Digimon digimon2 : tribe2.getMembers()) {
                digimon1.increaseFriendship(digimon2, 20);
                digimon2.increaseFriendship(digimon1, 20);
            }
        }

        // Other tribes might become more cautious
        for (Tribe otherTribe : World.getInstance().getTribes()) {
            if (otherTribe != tribe1 && otherTribe != tribe2) {
                otherTribe.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 10));
            }
        }

        VisualGUI.getInstance(null).addEvent(tribe1.getName() + " and " + tribe2.getName() + " have formed an alliance!", VisualGUI.EventType.POLITICAL);
    }

    /**
     * Declares war between two tribes.
     *
     * @param attacker The tribe declaring war.
     * @param defender The tribe being declared war upon.
     */
    public static void declareWar(Tribe attacker, Tribe defender) {
        wars.computeIfAbsent(attacker, k -> new HashSet<>()).add(defender);
        wars.computeIfAbsent(defender, k -> new HashSet<>()).add(attacker);

        // Increase aggression of members in both tribes
        attacker.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 75));
        defender.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 75));

        // Allied tribes join the war
        Set<Tribe> attackerAllies = alliances.getOrDefault(attacker, new HashSet<>());
        Set<Tribe> defenderAllies = alliances.getOrDefault(defender, new HashSet<>());

        for (Tribe ally : attackerAllies) {
            if (!wars.getOrDefault(ally, new HashSet<>()).contains(defender)) {
                declareWar(ally, defender);
            }
        }

        for (Tribe ally : defenderAllies) {
            if (!wars.getOrDefault(ally, new HashSet<>()).contains(attacker)) {
                declareWar(ally, attacker);
            }
        }

        // Other tribes react
        for (Tribe otherTribe : World.getInstance().getTribes()) {
            if (otherTribe != attacker && otherTribe != defender && !attackerAllies.contains(otherTribe) && !defenderAllies.contains(otherTribe)) {
                if (Math.random() < 0.3) { // 30% chance to join the war
                    declareWar(otherTribe, Math.random() < 0.5 ? attacker : defender);
                } else {
                    otherTribe.getMembers().forEach(digimon -> digimon.setAggression(digimon.getAggression() + 20));
                }
            }
        }

        VisualGUI.getInstance(null).addEvent(attacker.getName() + " has declared war on " + defender.getName() + "!", VisualGUI.EventType.POLITICAL);
    }

    /**
     * Converts a Digimon to a new tribe.
     *
     * @param digimon The Digimon to be converted.
     * @param newTribe The new tribe the Digimon is joining.
     */
    public static void convertDigimon(Digimon digimon, Tribe newTribe) {
        Tribe oldTribe = World.getInstance().getTribeByName(digimon.getTribe());
        if (oldTribe != null) {
            oldTribe.removeMember(digimon);
        }
        digimon.joinTribe(newTribe.getName());
        newTribe.addMember(digimon);

        // Update relationships
        for (Digimon tribeMember : newTribe.getMembers()) {
            if (tribeMember != digimon) {
                digimon.increaseFriendship(tribeMember, 30);
                tribeMember.increaseFriendship(digimon, 30);
            }
        }

        if (oldTribe != null) {
            for (Digimon oldMember : oldTribe.getMembers()) {
                oldMember.decreaseFriendship(digimon, 20);
            }
        }

        VisualGUI.getInstance(null).addEvent(digimon.getName() + " has converted to the " + newTribe.getName() + " tribe.", VisualGUI.EventType.POLITICAL);
    }

    /**
     * Checks and updates the political situation in the world.
     */
    public static void updatePoliticalSituation() {
        // Chance for random alliances or wars
        List<Tribe> tribes = World.getInstance().getTribes();
        if (tribes.size() >= 2) {
            if (Math.random() < 0.1) { // 10% chance for a political event
                Tribe tribe1 = tribes.get((int) (Math.random() * tribes.size()));
                Tribe tribe2 = tribes.get((int) (Math.random() * tribes.size()));
                if (tribe1 != tribe2) {
                    if (Math.random() < 0.6) { // 60% chance for alliance, 40% for war
                        formAlliance(tribe1, tribe2);
                    } else {
                        declareWar(tribe1, tribe2);
                    }
                }
            }
        }

        // Update war situations
        for (Map.Entry<Tribe, Set<Tribe>> entry : wars.entrySet()) {
            Tribe attacker = entry.getKey();
            for (Tribe defender : entry.getValue()) {
                if (Math.random() < 0.1) { // 10% chance for a battle
                    battle(attacker, defender);
                }
            }
        }
    }

    private static void battle(Tribe attacker, Tribe defender) {
        World world = World.getInstance();
        Map<Sector, List<Digimon>> attackerForces = new HashMap<>();
        Map<Sector, List<Digimon>> defenderForces = new HashMap<>();
    
        // Organize forces by sector
        for (Sector sector : world.getSectors()) {
            List<Digimon> attackersInSector = sector.getDigimons().stream()
                    .filter(d -> d.getTribe().equals(attacker.getName()))
                    .collect(Collectors.toList());
            List<Digimon> defendersInSector = sector.getDigimons().stream()
                    .filter(d -> d.getTribe().equals(defender.getName()))
                    .collect(Collectors.toList());
    
            if (!attackersInSector.isEmpty()) {
                attackerForces.put(sector, attackersInSector);
            }
            if (!defendersInSector.isEmpty()) {
                defenderForces.put(sector, defendersInSector);
            }
        }
    
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
                    VisualGUI.getInstance(null).addEvent(attacker.getName() + " won a battle against " + defender.getName() + " in " + sector.getName() + "!", VisualGUI.EventType.POLITICAL);
                } else {
                    // Defenders win in this sector
                    applyBattleDamage(sectorAttackers, 20);
                    VisualGUI.getInstance(null).addEvent(defender.getName() + " successfully defended against " + attacker.getName() + " in " + sector.getName() + "!", VisualGUI.EventType.POLITICAL);
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
                VisualGUI.getInstance(null).addEvent(digimon.getName() + " has been defeated in battle!", VisualGUI.EventType.POLITICAL);
            }
        });
    }
}
