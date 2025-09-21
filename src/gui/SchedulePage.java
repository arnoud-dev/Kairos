package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SchedulePage extends BorderPane {

    private LocalDate currentWeekStart;
    private GridPane weekGrid;

    public SchedulePage() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom, #f4f7fb, #e9eef4);");
        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        HBox header = new HBox(20);
        header.setPadding(new Insets(10, 0, 20, 0));
        header.setAlignment(Pos.CENTER);

        Button prevWeekBtn = new Button("← Previous Week");
        Button nextWeekBtn = new Button("Next Week →");
        Label weekLabel = new Label();
        weekLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        prevWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            updateWeekView(weekLabel);
        });

        nextWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            updateWeekView(weekLabel);
        });

        header.getChildren().addAll(prevWeekBtn, weekLabel, nextWeekBtn);
        setTop(header);

        weekGrid = new GridPane();
        weekGrid.setHgap(5);
        weekGrid.setVgap(2);
        weekGrid.setGridLinesVisible(true);
        weekGrid.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(weekGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        setCenter(scrollPane);
        updateWeekView(weekLabel);
    }

    private void updateWeekView(Label weekLabel) {
        weekGrid.getChildren().clear();

        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
        weekLabel.setText("Week of " + currentWeekStart.format(fmt) + " - " + weekEnd.format(fmt));

        weekGrid.add(new Label("Time"), 0, 0);
        for (int d = 0; d < 7; d++) {
            LocalDate day = currentWeekStart.plusDays(d);
            Label dayLabel = new Label(day.getDayOfWeek().toString() + "\n" + day.format(fmt));
            dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            weekGrid.add(dayLabel, d + 1, 0);
        }

        for (int h = 8; h <= 20; h++) {
            String hourLabel = String.format("%02d:00", h);
            weekGrid.add(new Label(hourLabel), 0, h - 7);

            for (int d = 0; d < 7; d++) {
                Pane slot = new Pane();
                slot.setPrefSize(120, 50);
                slot.setStyle("-fx-background-color: white;");
                weekGrid.add(slot, d + 1, h - 7);
            }
        }

        addActivity("Math Lecture", currentWeekStart.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), Color.LIGHTBLUE);
        addActivity("Project Work", currentWeekStart.plusDays(3), LocalTime.of(14, 0), LocalTime.of(16, 0), Color.LIGHTGREEN);
        addActivity("Chemistry Lab", currentWeekStart.plusDays(5), LocalTime.of(9, 0), LocalTime.of(11, 0), Color.PINK);
    }

    private void addActivity(String title, LocalDate date, LocalTime start, LocalTime end, Color color) {
        int dayIndex = (int) java.time.temporal.ChronoUnit.DAYS.between(currentWeekStart, date);
        if (dayIndex < 0 || dayIndex > 6) return;

        int startRow = start.getHour() - 7;
        int duration = end.getHour() - start.getHour();

        VBox block = new VBox();
        block.setPrefHeight(50 * duration);
        block.setStyle("-fx-background-color: " + toRgbString(color) + "; -fx-background-radius: 6;");
        block.setPadding(new Insets(5));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        block.getChildren().add(titleLabel);

        GridPane.setRowSpan(block, duration);
        weekGrid.add(block, dayIndex + 1, startRow);
    }

    private String toRgbString(Color c) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255),
                c.getOpacity());
    }
}
