import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public void initialize() {
    if (initialized) {
        System.out.println("VisualGUI already initialized. Skipping initialization.");
        return;
    }
    
    Platform.runLater(() -> {
        Stage stage = new Stage();
        stage.setTitle("Digimon World Simulator");
        stage.setWidth(1440);
        stage.setHeight(1020);

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(DigiviceStyle.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.setBackground(new Background(new BackgroundFill(DigiviceStyle.PRIMARY, CornerRadii.EMPTY, Insets.EMPTY)));
        
        Menu fileMenu = new Menu("File");
        fileMenu.setStyle("-fx-text-fill: " + DigiviceStyle.TEXT.toString().replace("0x", "#") + ";");
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setStyle("-fx-background-color: " + DigiviceStyle.SECONDARY.toString().replace("0x", "#") + ";" +
                          "-fx-text-fill: " + DigiviceStyle.TEXT.toString().replace("0x", "#") + ";");
        exitItem.setOnAction(e -> Platform.exit());
        
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);
        
        root.setTop(menuBar);

        // Main panel (center)
        GridPane mainPanel = new GridPane();
        mainPanel.setBackground(new Background(new BackgroundFill(DigiviceStyle.LIGHT_GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        mainPanel.setPadding(new Insets(10));
        mainPanel.setVgap(10);
        mainPanel.setHgap(10);

        // Create world info panel
        TextArea worldInfoArea = createStyledTextArea("World Information", DigiviceStyle.TITLE_FONT, DigiviceStyle.CONTENT_FONT, DigiviceStyle.BLUE, DigiviceStyle.WHITE);
        mainPanel.add(new ScrollPane(worldInfoArea), 0, 0, 3, 1);
        GridPane.setVgrow(worldInfoArea, Priority.ALWAYS);
        GridPane.setHgrow(worldInfoArea, Priority.ALWAYS);

        // Create sector panels
        GridPane sectorPanel = new GridPane();
        sectorPanel.setBackground(new Background(new BackgroundFill(DigiviceStyle.LIGHT_GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        sectorPanel.setHgap(10);
        sectorPanel.setVgap(10);
        int col = 0, row = 0;
        for (Sector sector : world.getSectors()) {
            String sectorName = sector.getName();
            TextArea sectorArea = new TextArea();
            sectorArea.setEditable(false);
            sectorArea.setFont(DigiviceStyle.CONTENT_FONT);
            sectorArea.setStyle("-fx-text-fill: black; -fx-control-inner-background: " + DigiviceStyle.WHITE.toString().replace("0x", "#") + ";");
            sectorArea.setWrapText(true);
            sectorPanels.put(sectorName, sectorArea);

            TitledPane sectorWrapper = new TitledPane(sectorName, new ScrollPane(sectorArea));
            sectorWrapper.setCollapsible(false);
            sectorWrapper.setStyle("-fx-text-fill: " + DigiviceStyle.ORANGE.toString().replace("0x", "#") + ";");

            sectorPanel.add(sectorWrapper, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
        mainPanel.add(sectorPanel, 0, 1, 3, 1);
        GridPane.setVgrow(sectorPanel, Priority.ALWAYS);
        GridPane.setHgrow(sectorPanel, Priority.ALWAYS);

        // Create event panels
        HBox eventsPanel = new HBox(10);
        eventsPanel.setBackground(new Background(new BackgroundFill(DigiviceStyle.LIGHT_GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        attackEventArea = createEventArea("Attack Events", DigiviceStyle.CONTENT_FONT, DigiviceStyle.BLUE, DigiviceStyle.WHITE);
        politicalEventArea = createEventArea("Political Events", DigiviceStyle.CONTENT_FONT, DigiviceStyle.BLUE, DigiviceStyle.WHITE);
        otherEventArea = createEventArea("Other Events", DigiviceStyle.CONTENT_FONT, DigiviceStyle.BLUE, DigiviceStyle.WHITE);
        eventsPanel.getChildren().addAll(
            new ScrollPane(attackEventArea),
            new ScrollPane(politicalEventArea),
            new ScrollPane(otherEventArea)
        );
        mainPanel.add(eventsPanel, 0, 2, 3, 1);
        GridPane.setVgrow(eventsPanel, Priority.ALWAYS);
        GridPane.setHgrow(eventsPanel, Priority.ALWAYS);

        root.setCenter(mainPanel);

        // Create tribe info panel (right side)
        tribeInfoArea = createStyledTextArea("Tribe Information", DigiviceStyle.TITLE_FONT, DigiviceStyle.CONTENT_FONT, DigiviceStyle.RED, DigiviceStyle.WHITE);
        root.setRight(new ScrollPane(tribeInfoArea));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Start periodic updates
        startPeriodicUpdates();
    });
    SimulationSubject.getInstance().addObserver(this);
    initialized = true;
}