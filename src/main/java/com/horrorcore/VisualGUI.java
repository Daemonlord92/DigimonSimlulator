package com.horrorcore;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class VisualGUI extends Application implements SimulationObserver {
    private static final int MAX_EVENTS = 20;
    private static final int UPDATE_INTERVAL_MS = 100;
    private Map<String, Canvas> sectorGridCanvases;
    private static final int CELL_SIZE = 20;
    private static final int GRID_SIZE = 20;// Update every 100ms
    private static VisualGUI instance;
    private World world;
    private Stage primaryStage;
    private static Map<String, TextArea> sectorPanels;
    private Text worldInfoArea;
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
        this.sectorGridCanvases = new HashMap<>();
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
            root.setStyle("-fx-background-color: #000000;");
    
            // Create the main display area
            VBox mainContent = new VBox(10);
            mainContent.setPadding(new Insets(20));
    
            // World info display
            Text worldInfoText = new Text();
            worldInfoText.setFont(Font.font("Courier New", 14));
            worldInfoText.setFill(Color.LIMEGREEN);
    
            // Create TabPane for different information sections
            TabPane infoTabs = new TabPane();
            infoTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    
            // Sector info tab
            Tab sectorsTab = new Tab("Sectors");
            TabPane sectorTabs = new TabPane();
            sectorTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            for (Sector sector : world.getSectors()) {
                // Create a VBox to hold both the grid and the text area
                VBox sectorContent = new VBox(10);
                sectorContent.setPadding(new Insets(10));

                // Create canvas for grid visualization
                Canvas gridCanvas = new Canvas(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
                gridCanvas.setStyle("-fx-background-color: #000000;");
                sectorGridCanvases.put(sector.getName(), gridCanvas);

                // Create text area for sector info
                TextArea sectorArea = new TextArea();
                sectorArea.setEditable(false);
                sectorArea.setPrefRowCount(5);
                sectorArea.setStyle("-fx-control-inner-background: #000000; -fx-text-fill: #00ff00;");
                sectorPanels.put(sector.getName(), sectorArea);

                // Add both to the VBox
                sectorContent.getChildren().addAll(gridCanvas, sectorArea);

                Tab tab = new Tab(sector.getName(), sectorContent);
                sectorTabs.getTabs().add(tab);
            }

            sectorsTab.setContent(sectorTabs);
    
            // Tribe info tab
            Tab tribesTab = new Tab("Tribes");
            tribeInfoArea = new TextArea();
            tribeInfoArea.setEditable(false);
            tribeInfoArea.setStyle("-fx-control-inner-background: #000000; -fx-text-fill: #00ff00;");
            tribesTab.setContent(tribeInfoArea);
    
            // Event info tab
            Tab eventsTab = new Tab("Events");
            VBox eventBox = new VBox(10);
            attackEventArea = createEventArea("Attack Events");
            politicalEventArea = createEventArea("Political Events");
            otherEventArea = createEventArea("Other Events");
            eventBox.getChildren().addAll(
                    new Label("Attack Events:"), attackEventArea,
                    new Label("Political Events:"), politicalEventArea,
                    new Label("Other Events:"), otherEventArea
            );
            eventsTab.setContent(eventBox);
    
            infoTabs.getTabs().addAll(sectorsTab, tribesTab, eventsTab);
    
            mainContent.getChildren().addAll(worldInfoText, infoTabs);
    
            root.setCenter(mainContent);
    
            Scene scene = new Scene(root, 1000, 800);
            if (getClass().getResource("/styles.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } else {
                System.err.println("Warning: styles.css not found");
            }
            primaryStage.setScene(scene);
            primaryStage.show();
    
            // Update references
            this.worldInfoArea = worldInfoText;
    
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
        area.setPrefRowCount(5);
        area.setStyle("-fx-control-inner-background: #000000; -fx-text-fill: #00ff00;");
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

    private void drawGrid(Canvas canvas, Sector sector) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw grid
        Grid grid = sector.getGrid();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                GridCell cell = grid.getCell(x, y);
                double xPos = x * CELL_SIZE;
                double yPos = y * CELL_SIZE;

                // Draw cell background
                switch (cell.getType()) {
                    case BORDER:
                        gc.setFill(Color.DARKGRAY);
                        break;
                    case BLOCKED:
                        gc.setFill(Color.RED);
                        break;
                    default:
                        gc.setFill(Color.BLACK);
                        break;
                }
                gc.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);

                // Draw grid lines
                gc.setStroke(Color.DARKGREEN);
                gc.strokeRect(xPos, yPos, CELL_SIZE, CELL_SIZE);

                // Draw occupants with labels
                if (cell.getOccupant() != null) {
                    gc.setFill(Color.GREEN);
                    gc.fillOval(xPos + 2, yPos + 2, CELL_SIZE - 4, CELL_SIZE - 4);

                    // Add a small "D" indicator
                    if(cell.getOccupant() instanceof Digimon) {
                        gc.setFill(Color.WHITE);
                        gc.setFont(new Font(10));
                        gc.fillText("D", xPos + 7, yPos + 14);
                    } else if (cell.getOccupant() instanceof CelestialDigimon) {
                        gc.setFill(Color.YELLOW);
                        gc.setFont(new Font(10));
                        gc.fillText("C", xPos + 7, yPos + 14);
                    }
                }

                // Draw buildings with type indicators
                if (cell.getBuilding() != null) {
                    gc.setFill(Color.BLUE);
                    gc.fillRect(xPos + 4, yPos + 4, CELL_SIZE - 8, CELL_SIZE - 8);

                    // Add building type indicator
                    gc.setFill(Color.WHITE);
                    gc.setFont(new Font(10));
                    String buildingLabel = switch(cell.getBuilding().getType()) {
                        case HOUSE -> "H";
                        case FARM -> "F";
                        case BARRACKS -> "B";
                        case CITY_CENTER -> "C";
                    };
                    gc.fillText(buildingLabel, xPos + 7, yPos + 14);
                }
            }
        }
    }

    public void updateDisplay() {
        Platform.runLater(() -> {
            worldLock.readLock().lock();
            try {
                // Update sector panels
                for (Sector sector : world.getSectors()) {
                    String sectorName = sector.getName();
                    TextArea sectorArea = sectorPanels.get(sectorName);
                    Canvas canvas = sectorGridCanvases.get(sector.getName());
                    if (sectorArea == null) {
                        System.err.println("Warning: No TextArea found for sector: " + sectorName);
                        continue;
                    }
                    StringBuilder sectorInfo = new StringBuilder();
                    sectorInfo.append("Digimons in ").append(sectorName).append(":\n");

                    if (canvas != null) {
                        drawGrid(canvas, sector);
                    }
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
