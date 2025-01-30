package com.horrorcore;


public record SimulationEvent(String message, com.horrorcore.SimulationEvent.EventType type) {

    public enum EventType {
        ATTACK, POLITICAL, OTHER
    }
}



