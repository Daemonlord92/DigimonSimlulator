package com.horrorcore.gui;

import com.horrorcore.World;
import com.horrorcore.systems.events.SimulationEvent;

public interface SimulationObserver {
    void onSimulationEvent(SimulationEvent event);
    void onWorldUpdate(World world);
}