package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SchedulePage extends VBox {

    public SchedulePage() {
        setPadding(new Insets(20));
        setSpacing(10);

        Label title = new Label("Schedule");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50;");

        Label placeholder = new Label("This is the Schedule page.");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");

        getChildren().addAll(title, placeholder);
    }
}
