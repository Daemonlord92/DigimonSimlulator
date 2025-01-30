package com.horrorcore;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class GridInfoPanel extends VBox {
    private final Text cellTypeLabel;
    private final Text occupantLabel;
    private final Text buildingLabel;
    private final Text coordinatesLabel;

    public GridInfoPanel() {
        setStyle("-fx-background-color: #000000; -fx-padding: 10;");
        setPrefWidth(200);
        setSpacing(10);

        Label title = new Label("Cell Information");
        title.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");

        cellTypeLabel = createInfoText("Type: ");
        occupantLabel = createInfoText("Occupant: ");
        buildingLabel = createInfoText("Building: ");
        coordinatesLabel = createInfoText("Coordinates: ");

        getChildren().addAll(title, cellTypeLabel, occupantLabel, buildingLabel, coordinatesLabel);
    }

    private Text createInfoText(String prefix) {
        Text text = new Text(prefix + "None");
        text.setFill(Color.LIMEGREEN);
        return text;
    }

    public void updateInfo(GridCell cell) {
        if (cell != null) {
            cellTypeLabel.setText("Type: " + cell.getType());

            Digimon occupant = cell.getOccupant();
            if (occupant != null) {
                occupantLabel.setText("Occupant: " + occupant.getName() +
                        " (Health: " + occupant.getHealth() +
                        ", Stage: " + occupant.getStage() + ")");
            } else {
                occupantLabel.setText("Occupant: None");
            }

            Building building = cell.getBuilding();
            if (building != null) {
                buildingLabel.setText("Building: " + building.getType() +
                        " (Owner: " + (building.getOwner() != null ? building.getOwner().getName() : "None") + ")");
            } else {
                buildingLabel.setText("Building: None");
            }

            coordinatesLabel.setText(String.format("Coordinates: (%d, %d)", cell.getX(), cell.getY()));
        } else {
            clearInfo();
        }
    }

    public void clearInfo() {
        cellTypeLabel.setText("Type: None");
        occupantLabel.setText("Occupant: None");
        buildingLabel.setText("Building: None");
        coordinatesLabel.setText("Coordinates: None");
    }
}