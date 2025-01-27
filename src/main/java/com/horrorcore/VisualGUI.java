package com.horrorcore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VisualGUI {
    private static final int MAX_EVENTS = 20;
    private static final int UPDATE_INTERVAL_MS = 100; // Update every 100ms
    private static VisualGUI instance;
    private World world;
    private JFrame frame;
    private static Map<String, JTextArea> sectorPanels;
    private JTextArea worldInfoArea;
    private JTextArea outputArea;
    private JTextArea attackEventArea;
    private JTextArea politicalEventArea;
    private JTextArea otherEventArea;
    private ScheduledExecutorService executor;
    private final ReadWriteLock worldLock = new ReentrantReadWriteLock();
    private boolean initialized = false;
    private int lastClearTime = 0;

    /**
     * Private constructor for the VisualGUI class.
     * This constructor initializes the VisualGUI with the given World object,
     * creates a new HashMap for sector panels, and sets up a single-threaded
     * scheduled executor service.
     *
     * @param world The World object representing the current state of the Digimon world.
     *              This world object is used to populate and update the GUI with relevant information.
     */
    private VisualGUI(World world) {
        this.world = world;
        this.sectorPanels = new HashMap<>();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Returns the singleton instance of the VisualGUI class.
     * If the instance doesn't exist, it creates a new one with the given World object.
     * This method ensures that only one instance of VisualGUI is created and used throughout the application.
     *
     * @param world The World object representing the current state of the Digimon world.
     *              This is used to initialize the VisualGUI if it hasn't been created yet.
     * @return The singleton instance of VisualGUI.
     */
    public static VisualGUI getInstance(World world) {
        if (instance == null) {
            instance = new VisualGUI(world);
        }
        return instance;
    }

    /**
     * Initializes the Visual GUI for the Digimon World Simulator.
     * This method sets up the main frame, creates panels for world information and sectors,
     * and initializes event areas for attack, political, and other events.
     * It also starts periodic updates for the GUI.
     * 
     * The method uses SwingUtilities.invokeLater to ensure that GUI operations
     * are performed on the Event Dispatch Thread.
     * 
     * If the GUI has already been initialized, this method will log a message and return
     * without performing any further actions.
     * 
     * This method does not take any parameters and does not return any value.
     * Its effects are visible through the creation and display of GUI components.
     */
    public void initialize() {
        if (initialized) {
            System.out.println("VisualGUI already initialized. Skipping initialization.");
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Digimon World Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1440, 1020);
            frame.setLayout(new BorderLayout());
    
            // Create world info panel
            JPanel worldInfoPanel = new JPanel(new BorderLayout());
            worldInfoArea = new JTextArea(5, 50);
            worldInfoArea.setEditable(false);
            JScrollPane worldInfoScrollPane = new JScrollPane(worldInfoArea);
            worldInfoPanel.add(new JLabel("World Information:"), BorderLayout.NORTH);
            worldInfoPanel.add(worldInfoScrollPane, BorderLayout.CENTER);
    
            JPanel sectorPanel = new JPanel(new GridLayout(0, 3, 10, 10));
    
            sectorPanel.add(worldInfoPanel, BorderLayout.WEST);
    
    
            frame.setVisible(true);
    
            for (Sector sector : world.getSectors()) {
                String sectorName = sector.getName();
                JTextArea sectorArea = new JTextArea();
                sectorArea.setEditable(false);
                sectorPanels.put(sectorName, sectorArea);
                JScrollPane scrollPane = new JScrollPane(sectorArea);
                scrollPane.setBorder(BorderFactory.createTitledBorder(sectorName));
                sectorPanel.add(scrollPane);
                System.out.println("Added sector to GUI: " + sectorName);
            }
    
            attackEventArea = new JTextArea();
            attackEventArea.setEditable(false);
            JScrollPane attackEventScrollPane = new JScrollPane(attackEventArea);
            attackEventScrollPane.setBorder(BorderFactory.createTitledBorder("Attack Events"));
    
            politicalEventArea = new JTextArea();
            politicalEventArea.setEditable(false);
            JScrollPane politicalEventScrollPane = new JScrollPane(politicalEventArea);
            politicalEventScrollPane.setBorder(BorderFactory.createTitledBorder("Political Events"));
    
            otherEventArea = new JTextArea();
            otherEventArea.setEditable(false);
            JScrollPane otherEventScrollPane = new JScrollPane(otherEventArea);
            otherEventScrollPane.setBorder(BorderFactory.createTitledBorder("Other Events"));
    
            JPanel eventsPanel = new JPanel(new GridLayout(1, 3));
            eventsPanel.add(attackEventScrollPane);
            eventsPanel.add(politicalEventScrollPane);
            eventsPanel.add(otherEventScrollPane);
    
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sectorPanel, eventsPanel);
            splitPane.setResizeWeight(0.7);
    
            frame.add(splitPane, BorderLayout.CENTER);
    
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitPane.setDividerLocation(0.7);
                }
            });
    
            frame.setVisible(true);
    
            // Start periodic updates
            startPeriodicUpdates();
        });
    
        initialized = true;
    }

    private void startPeriodicUpdates() {
        executor.scheduleAtFixedRate(this::updateDisplayAsync, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void updateDisplayAsync() {
        SwingUtilities.invokeLater(this::updateDisplay);
    }

    /**
     * Updates the display of the Digimon World Simulator GUI.
     * This method refreshes the information for each sector, including the list of Digimons,
     * and updates the world information panel with current statistics.
     * The method is designed to be run on the Event Dispatch Thread using SwingUtilities.invokeLater.
     * It uses a read lock to ensure thread-safe access to the world data.
     *
     * The method performs the following tasks:
     * 1. Updates each sector's panel with the current list of Digimons and their status.
     * 2. Calculates and displays the total number of Digimons and tribes across all sectors.
     * 3. Shows the current time, technology age, and time until the next technology age.
     * 4. Handles potential null references for sectors or Digimons, logging warnings as necessary.
     *
     * This method does not take any parameters and does not return any value.
     * Its effects are visible through updates to the GUI components.
     */
    public void updateDisplay() {
        SwingUtilities.invokeLater(() -> {
            worldLock.readLock().lock();
            try {
                for (Sector sector : world.getSectors()) {
                    String sectorName = sector.getName();
                    JTextArea sectorArea = sectorPanels.get(sectorName);
                    if (sectorArea == null) {
                        System.err.println("Warning: No JTextArea found for sector: " + sectorName);
                        continue;
                    }
                    StringBuilder sectorInfo = new StringBuilder();
                    sectorInfo.append("Digimons in ").append(sectorName).append(":\n");
                    
                    List<Digimon> digimonsCopy = new ArrayList<>(sector.getDigimons());
                    for (Digimon digimon : digimonsCopy) {
                        if (digimon == null) {
                            System.err.println("Warning: Null Digimon found in sector: " + sectorName);
                            continue;
                        }
                        sectorInfo.append(digimon.getStatusString()).append("\n");
                    }
                    sectorArea.setText(sectorInfo.toString());
                    int totalDigimon = world.getSectors().stream()
                            .mapToInt(s -> s.getDigimons().size())
                            .sum();
                    int timeToNextTechAge = world.getTimeToNextAge();
                    int totalBuildings = world.getBuildings();
                    worldInfoArea.setText(String.format(
                            "Time: %d\n" +
                                    "Technology Age: %s\n" +
                                    "Total Digimon: %d\n" +
                                    "Total Tribes: %d\n" +
                                    "Time To Next Tech Age: %d\n" +
                                    "Total Buildings: %d\n",
                            world.getTime(),
                            world.getTechnologySystem().getCurrentAge(),
                            totalDigimon,
                            Tribe.getAllTribes().size(),
                            timeToNextTechAge,
                            totalBuildings
                    ));
                }
            } finally {
                worldLock.readLock().unlock();
            }
        });
    }

    public void addEvent(String event, EventType type) {
        SwingUtilities.invokeLater(() -> {
            JTextArea targetArea;
            int currentTime = world.getTime();
            if (currentTime - lastClearTime >= 5) {
                clearAllEvents();
                lastClearTime = currentTime;
            }
            switch (type) {
                case ATTACK:
                    targetArea = attackEventArea;
                    break;
                case POLITICAL:
                    targetArea = politicalEventArea;
                    break;
                case OTHER:
                default:
                    targetArea = otherEventArea;
                    break;
            }

            // Check if it's time to clear events


            String[] events = targetArea.getText().split("\n");
            StringBuilder newEvents = new StringBuilder();

            // Add the new event at the top
            newEvents.append(event).append("\n");

            // Add the existing events, keeping only the most recent ones
            int endIndex = Math.min(events.length, MAX_EVENTS - 1);
            for (int i = 0; i < endIndex; i++) {
                newEvents.append(events[i]).append("\n");
            }

            targetArea.setText(newEvents.toString());
            targetArea.setCaretPosition(0); // Set caret to the top
        });
    }

    private void clearAllEvents() {
        SwingUtilities.invokeLater(() -> {
            attackEventArea.setText("");
            politicalEventArea.setText("");
            otherEventArea.setText("");
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

    public enum EventType {
        ATTACK,
        POLITICAL,
        OTHER
    }
}