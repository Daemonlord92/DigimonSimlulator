package com.horrorcore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulationEvent {
    private final String message;
    private final EventType type;

    public SimulationEvent(String message, EventType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() { return message; }
    public EventType getType() { return type; }

    public enum EventType {
        ATTACK, POLITICAL, OTHER
    }
}



