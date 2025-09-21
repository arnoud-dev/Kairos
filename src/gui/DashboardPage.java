package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class DashboardPage extends VBox {

    public DashboardPage() {
        setPadding(new Insets(30));
        setSpacing(20);
        setStyle("-fx-background-color: linear-gradient(to bottom, #f3f6f8, #eef3f6);");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #2c3e50; -fx-font-weight: 700;");

        VBox statsContainer = new VBox(14);
        statsContainer.setPadding(new Insets(10));
        statsContainer.setAlignment(Pos.TOP_LEFT);

        // Example cards
        statsContainer.getChildren().addAll(
                createCard("Total Categories", "12"),
                createCard("Total Subjects", "34"),
                createCard("Pending Tasks", "7"),
                createCard("Completed Tasks", "18")
        );

        getChildren().addAll(title, statsContainer);
    }

    private VBox createCard(String label, String value) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: linear-gradient(to right, #ffffff, #f9fbfd);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: rgba(34,47,60,0.05);"
        );
        card.setEffect(new DropShadow(10, Color.rgb(18, 28, 36, 0.08)));

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7b8a93; -fx-font-weight: 600;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #2c3e50; -fx-font-weight: 700;");

        card.getChildren().addAll(nameLabel, valueLabel);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            card.setEffect(new DropShadow(14, Color.rgb(10, 20, 35, 0.12)));
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setEffect(new DropShadow(10, Color.rgb(18, 28, 36, 0.08)));
        });

        return card;
    }
}
