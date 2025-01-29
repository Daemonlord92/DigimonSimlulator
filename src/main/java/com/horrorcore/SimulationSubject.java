package com.horrorcore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulationSubject {
    private static SimulationSubject instance;
    private final List<SimulationObserver> observers = new CopyOnWriteArrayList<>();

    private SimulationSubject() {}

    public static SimulationSubject getInstance() {
        if (instance == null) {
            instance = new SimulationSubject();
        }
        return instance;
    }

    public void addObserver(SimulationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SimulationObserver observer) {
        observers.remove(observer);
    }

    public void notifyEvent(String message, SimulationEvent.EventType type) {
        SimulationEvent event = new SimulationEvent(message, type);
        for (SimulationObserver observer : observers) {
            observer.onSimulationEvent(event);
        }
    }

    public void notifyWorldUpdate(World world) {
        for (SimulationObserver observer : observers) {
            observer.onWorldUpdate(world);
        }
    }
}