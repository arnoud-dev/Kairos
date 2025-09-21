package gui;

import domein.CategoryController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardPage extends VBox {
	private final CategoryController controller;

    public DashboardPage(CategoryController controller) {
		setPadding(new Insets(50));
        setSpacing(40);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #f5f7fa, #e4ebf5);");
        Label title = new Label("Dashboard");
        title.setFont(Font.font("Segoe UI", 36));
        title.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        this.controller = controller;
        
        HBox cardsContainer = new HBox(30);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(0, 30, 0, 30));
        cardsContainer.getChildren().addAll(
                createCard("Total Categories", "12", "#1abc9c"),
                createCard("Total Subjects", "34", "#3498db"),
                createCard("Pending Tasks", "7", "#e67e22"),
                createCard("Random task", controller.getRandomTask().getName(), "#9b59b6")
        );

        PieChart categoriesChart = new PieChart(FXCollections.observableArrayList(
                new PieChart.Data("Science", 5),
                new PieChart.Data("Math", 7),
                new PieChart.Data("History", 4),
                new PieChart.Data("Art", 3)
        ));
        categoriesChart.setTitle("Tasks by Category");
        categoriesChart.setLegendVisible(true);
        categoriesChart.setLabelsVisible(true);
        categoriesChart.setPrefSize(500, 350);

        HBox chartSection = new HBox(categoriesChart);
        chartSection.setAlignment(Pos.CENTER);
        VBox recentActivity = createInfoPanel("Recent Activity",
                createActivityRow("Added new subject: Physics", "Today, 10:24 AM"),
                createActivityRow("Completed task: Write essay", "Yesterday, 2:15 PM"),
                createActivityRow("Added new category: Science", "2 days ago"),
                createActivityRow("Reviewed pending tasks", "3 days ago")
        );
        VBox upcomingDeadlines = createInfoPanel("Upcoming Deadlines",
                createActivityRow("Math Homework", "Due: Tomorrow"),
                createActivityRow("Physics Lab Report", "Due: 3 days"),
                createActivityRow("History Essay", "Due: 1 week")
        );
        VBox quickNotes = createInfoPanel("Quick Notes",
                createActivityRow("📌 Remember to revise algebra", ""),
                createActivityRow("📌 Finish science project slides", ""),
                createActivityRow("📌 Prepare reading list for history", "")
        );

        HBox bottomPanels = new HBox(30, recentActivity, upcomingDeadlines, quickNotes);
        bottomPanels.setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(title, cardsContainer, chartSection, bottomPanels);
    }

    private VBox createCard(String label, String value, String colorHex) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(250);
        card.setPrefHeight(200);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorHex + "55, " + colorHex + "aa);" +
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;"
        );
        card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.1)));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Segoe UI", 16));
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: 600;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", 32));
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        card.getChildren().addAll(nameLabel, valueLabel);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.05);
            card.setScaleY(1.05);
            card.setEffect(new DropShadow(25, Color.rgb(0, 0, 0, 0.15)));
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
            card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.1)));
        });
        return card;
    }

    private VBox createInfoPanel(String title, HBox... rows) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-border-color: rgba(0,0,0,0.05);"
        );
        panel.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.08)));
        panel.setPrefWidth(300);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 20));
        titleLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        VBox rowsBox = new VBox(12, rows);

        panel.getChildren().addAll(titleLabel, rowsBox);
        return panel;
    }

    private HBox createActivityRow(String action, String time) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label actionLabel = new Label(action);
        actionLabel.setFont(Font.font("Segoe UI", 14));
        actionLabel.setStyle("-fx-text-fill: #34495e;");

        Label timeLabel = new Label(time);
        timeLabel.setFont(Font.font("Segoe UI", 12));
        timeLabel.setStyle("-fx-text-fill: #95a5a6;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(actionLabel, spacer, timeLabel);
        return row;
    }
}
