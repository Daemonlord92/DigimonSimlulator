package com.horrorcore.systems.events;


public record SimulationEvent(String message, SimulationEvent.EventType type) {

    public enum EventType {
        ATTACK, POLITICAL, OTHER
    }
}



