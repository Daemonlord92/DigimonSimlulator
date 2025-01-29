package com.horrorcore;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VisualGUI extends Application implements SimulationObserver {
    private static final int MAX_EVENTS = 20;
    private static final int UPDATE_INTERVAL_MS = 100; // Update every 100ms
    private static VisualGUI instance;
    private World world;
    private Stage primaryStage;
    private static Map<String, TextArea> sectorPanels;
    private TextArea worldInfoArea;
    private TextArea attackEventArea;
    private TextArea politicalEventArea;
    private TextArea tribeInfoArea;
    private TextArea otherEventArea;
    private ScheduledExecutorService executor;
    private final ReadWriteLock worldLock = new ReentrantReadWriteLock();
    private boolean initialized = false;
    private int lastClearTime = 0;

    private VisualGUI(World world) {
        this.world = world;
        this.sectorPanels = new HashMap<>();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static VisualGUI getInstance(World world) {
        if (instance == null) {
            instance = new VisualGUI(world);
        }
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialize();
    }

    public void initialize() {
        if (initialized) {
            System.out.println("VisualGUI already initialized. Skipping initialization.");
            return;
        }

        Platform.runLater(() -> {
            primaryStage.setTitle("Digimon World Simulator");

            BorderPane root = new BorderPane();

            // Create menu bar
            MenuBar menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.setOnAction(e -> System.exit(0));
            fileMenu.getItems().add(exitItem);

            Menu viewMenu = new Menu("View");
            MenuItem refreshItem = new MenuItem("Refresh");
            refreshItem.setOnAction(e -> updateDisplay());
            viewMenu.getItems().add(refreshItem);

            Menu helpMenu = new Menu("Help");
            MenuItem aboutItem = new MenuItem("About");
            aboutItem.setOnAction(e -> showAboutDialog());
            helpMenu.getItems().add(aboutItem);

            menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);

            // Main panel (center)
            VBox mainPanel = new VBox(10);
            mainPanel.setPadding(new Insets(10));

            // World info area
            worldInfoArea = new TextArea();
            worldInfoArea.setEditable(false);
            worldInfoArea.setPrefRowCount(5);
            TitledPane worldInfoPane = new TitledPane("World Information", worldInfoArea);
            worldInfoPane.setCollapsible(false);

            // Sector panels
            GridPane sectorPanel = new GridPane();
            sectorPanel.setHgap(10);
            sectorPanel.setVgap(10);
            int col = 0, row = 0;
            for (Sector sector : world.getSectors()) {
                String sectorName = sector.getName();
                TextArea sectorArea = new TextArea();
                sectorArea.setEditable(false);
                sectorArea.setPrefRowCount(5);
                sectorArea.setPrefColumnCount(20);
                sectorPanels.put(sectorName, sectorArea);
                TitledPane sectorPane = new TitledPane(sectorName, sectorArea);
                sectorPane.setCollapsible(false);
                sectorPanel.add(sectorPane, col, row);
                if (++col == 3) {
                    col = 0;
                    row++;
                }
            }

            // Event areas
            attackEventArea = createEventArea("Attack Events");
            politicalEventArea = createEventArea("Political Events");
            otherEventArea = createEventArea("Other Events");

            HBox eventsPanel = new HBox(10);
            eventsPanel.getChildren().addAll(
                new TitledPane("Attack Events", attackEventArea),
                new TitledPane("Political Events", politicalEventArea),
                new TitledPane("Other Events", otherEventArea)
            );

            mainPanel.getChildren().addAll(worldInfoPane, sectorPanel, eventsPanel);

            // Tribe info area (right)
            tribeInfoArea = new TextArea();
            tribeInfoArea.setEditable(false);
            TitledPane tribeInfoPane = new TitledPane("Tribe Information", tribeInfoArea);
            tribeInfoPane.setCollapsible(false);

            root.setTop(menuBar);
            root.setCenter(mainPanel);
            root.setRight(tribeInfoPane);

            Scene scene = new Scene(root, 1440, 1020);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Start periodic updates
            startPeriodicUpdates();
        });

        // Ensure initial update
        updateDisplay();

        SimulationSubject.getInstance().addObserver(this);
        initialized = true;
    }

    private TextArea createEventArea(String title) {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setPrefRowCount(10);
        return area;
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Digimon World Simulator\nVersion 1.0");
        alert.showAndWait();
    }

    private void startPeriodicUpdates() {
        executor.scheduleAtFixedRate(this::updateDisplayAsync, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void updateDisplayAsync() {
        Platform.runLater(this::updateDisplay);
    }

    public void updateDisplay() {
        Platform.runLater(() -> {
            worldLock.readLock().lock();
            try {
                // Update sector panels
                for (Sector sector : world.getSectors()) {
                    String sectorName = sector.getName();
                    TextArea sectorArea = sectorPanels.get(sectorName);
                    if (sectorArea == null) {
                        System.err.println("Warning: No TextArea found for sector: " + sectorName);
                        continue;
                    }
                    StringBuilder sectorInfo = new StringBuilder();
                    sectorInfo.append("Digimons in ").append(sectorName).append(":\n");
                    
                    for (Digimon digimon : sector.getDigimons()) {
                        if (digimon == null) {
                            System.err.println("Warning: Null Digimon found in sector: " + sectorName);
                            continue;
                        }
                        sectorInfo.append(digimon.getStatusString()).append("\n");
                    }
                    sectorArea.setText(sectorInfo.toString());
                }

                // Update world info
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

                // Update tribe information
                StringBuilder tribeInfo = new StringBuilder();
                tribeInfo.append("Tribes:\n");
                for (Tribe tribe : Tribe.getAllTribes()) {
                    tribeInfo.append(String.format("- %s (Leader: %s)\n", 
                        tribe.getName(), 
                        tribe.getLeader() != null ? tribe.getLeader().getName() : "None"));
                    tribeInfo.append("  Members: ").append(tribe.getMembers().size()).append("\n");
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
        Platform.runLater(() -> {
            TextArea targetArea;
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

            String[] events = targetArea.getText().split("\n");
            StringBuilder newEvents = new StringBuilder();

            newEvents.append(event).append("\n");

            int endIndex = Math.min(events.length, MAX_EVENTS - 1);
            for (int i = 0; i < endIndex; i++) {
                newEvents.append(events[i]).append("\n");
            }

            targetArea.setText(newEvents.toString());
            targetArea.positionCaret(0);
        });
    }

    private void clearAllEvents() {
        Platform.runLater(() -> {
            attackEventArea.clear();
            politicalEventArea.clear();
            otherEventArea.clear();
        });
    }

    public void updateWorldInfo(World world) {
        Platform.runLater(() -> {
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
        });
    }

    public void updateSectorInfo(List<Sector> sectors) {
        Platform.runLater(() -> {
            for (Sector sector : sectors) {
                String sectorName = sector.getName();
                TextArea sectorArea = sectorPanels.get(sectorName);
                if (sectorArea == null) {
                    System.err.println("Warning: No TextArea found for sector: " + sectorName);
                    continue;
                }
                StringBuilder sectorInfo = new StringBuilder();
                sectorInfo.append("Digimons in ").append(sectorName).append(":\n");

                for (Digimon digimon : sector.getDigimons()) {
                    if (digimon == null) {
                        System.err.println("Warning: Null Digimon found in sector: " + sectorName);
                        continue;
                    }
                    sectorInfo.append(digimon.getStatusString()).append("\n");
                }
                sectorArea.setText(sectorInfo.toString());
            }
        });
    }

    public void shutdown() {
        SimulationSubject.getInstance().removeObserver(this);
        executor.shutdown();
    }

    @Override
    public void onSimulationEvent(SimulationEvent event) {
        addEvent(event.getMessage(), convertEventType(event.getType()));
    }

    @Override
    public void onWorldUpdate(World world) {
        updateDisplay();
    }

    private EventType convertEventType(SimulationEvent.EventType type) {
        return switch (type) {
            case ATTACK -> EventType.ATTACK;
            case POLITICAL -> EventType.POLITICAL;
            case OTHER -> EventType.OTHER;
        };
    }

    public enum EventType {
        ATTACK,
        POLITICAL,
        OTHER
    }
}
