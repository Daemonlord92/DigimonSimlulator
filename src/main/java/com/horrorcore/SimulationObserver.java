package com.horrorcore;

public interface SimulationObserver {
    void onSimulationEvent(SimulationEvent event);
    void onWorldUpdate(World world);
}