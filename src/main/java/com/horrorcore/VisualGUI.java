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
    private JTextArea tribeInfoArea;
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
            frame.setLayout(new GridBagLayout());
    
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 5);
    
            // Create world info panel
            worldInfoArea = new JTextArea(5, 50);
            worldInfoArea.setEditable(false);
            JScrollPane worldInfoScrollPane = new JScrollPane(worldInfoArea);
            worldInfoScrollPane.setBorder(BorderFactory.createTitledBorder("World Information"));
    
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.weightx = 1.0;
            gbc.weighty = 0.2;
            frame.add(worldInfoScrollPane, gbc);
    
            // Create sector panels
            JPanel sectorPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            for (Sector sector : world.getSectors()) {
                String sectorName = sector.getName();
                JTextArea sectorArea = new JTextArea();
                sectorArea.setEditable(false);
                sectorPanels.put(sectorName, sectorArea);
                JScrollPane scrollPane = new JScrollPane(sectorArea);
                scrollPane.setBorder(BorderFactory.createTitledBorder(sectorName));
                sectorPanel.add(scrollPane);
            }
    
            gbc.gridy = 1;
            gbc.weighty = 0.6;
            frame.add(sectorPanel, gbc);
    
            // Create event panels
            attackEventArea = createEventArea("Attack Events");
            politicalEventArea = createEventArea("Political Events");
            otherEventArea = createEventArea("Other Events");
    
            JPanel eventsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            eventsPanel.add(new JScrollPane(attackEventArea));
            eventsPanel.add(new JScrollPane(politicalEventArea));
            eventsPanel.add(new JScrollPane(otherEventArea));
    
            gbc.gridy = 2;
            gbc.weighty = 0.2;
            frame.add(eventsPanel, gbc);

            // Create tribe info panel
            tribeInfoArea = new JTextArea(10, 50);
            tribeInfoArea.setEditable(false);
            JScrollPane tribeInfoScrollPane = new JScrollPane(tribeInfoArea);
            tribeInfoScrollPane.setBorder(BorderFactory.createTitledBorder("Tribe Information"));

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 3;
            gbc.weighty = 0.2;
            frame.add(tribeInfoScrollPane, gbc);
    
            frame.setVisible(true);
    
            // Start periodic updates
            startPeriodicUpdates();
        });
    
        initialized = true;
    }
    
    private JTextArea createEventArea(String title) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBorder(BorderFactory.createTitledBorder(title));
        return area;
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

                // Update tribe information
                StringBuilder tribeInfo = new StringBuilder();
                tribeInfo.append("Tribes:\n");
                for (Tribe tribe : Tribe.getAllTribes()) {
                    tribeInfo.append(String.format("- %s (Leader: %s)\n", 
                        tribe.getName(), 
                        tribe.getLeader() != null ? tribe.getLeader().getName() : "None"));
                    tribeInfo.append("  Members:\n");
                    for (Digimon member : tribe.getMembers()) {
                        tribeInfo.append(String.format("    %s (%s)\n", 
                            member.getName(), 
                            member.getStage()));
                    }
                    tribeInfo.append("  Territory: ").append(tribe.getTechnologySystem().getTechnologyLevels()).append("\n");
                    tribeInfo.append("  Military: ").append(tribe.getMilitaryStrength()).append("\n");
                    tribeInfo.append("\n");
                }
                tribeInfoArea.setText(tribeInfo.toString());
            } finally {
                worldLock.readLock().unlock();
            }
        });
    }

    public void addEvent(String event, EventType type) {
        SwingUtilities.invokeLater(() -> {
            JTextArea targetArea;
            int currentTime = world.getTime();
            if (currentTime - lastClearTime >= 25) {
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