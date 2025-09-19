package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardPage extends VBox {

    public DashboardPage() {
        setPadding(new Insets(20));
        setSpacing(10);

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50;");

        Label placeholder = new Label("This is the Dashboard page.");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");

        getChildren().addAll(title, placeholder);
    }
}
