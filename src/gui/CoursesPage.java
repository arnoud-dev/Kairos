package gui;

import domein.CategoryController;
import domein.Difficulty;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoursesPage extends VBox {

    private final CategoryController controller;
    private final VBox contentBox;
    private final HostServices hostServices;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    public CoursesPage(HostServices hostServices) {
        this.hostServices = hostServices;
        setSpacing(18);
        setPadding(new Insets(26));
        setStyle("-fx-background-color: linear-gradient(to bottom, #f3f6f8, #eef3f6);");

        controller = new CategoryController();

        Label title = new Label("Courses");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #2c3e50; -fx-font-weight: 700;");

        HBox topRow = new HBox(12);
        topRow.setPadding(new Insets(6, 0, 6, 0));
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button refreshBtn = new Button("⟳ Refresh");
        stylePrimaryButtonSmall(refreshBtn);
        refreshBtn.setOnAction(e -> refresh());

        Button addCategoryBtn = new Button("➕ Add Category");
        styleAccentButton(addCategoryBtn);
        addCategoryBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Category");
            dialog.setHeaderText("Enter category name");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    controller.addCategory(input.trim());
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(title, spacer, refreshBtn, addCategoryBtn);

        contentBox = new VBox(16);
        contentBox.setPadding(new Insets(8));

        ScrollPane sp = new ScrollPane(contentBox);
        sp.setFitToWidth(true);
        sp.setPrefHeight(800);
        sp.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 8;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        sp.getStylesheets().add("data:text/css," +
            ".scroll-pane .viewport { -fx-background-color: transparent; }" +
            ".scroll-bar:vertical { -fx-background-color: transparent; -fx-pref-width: 12px; }" +
            ".scroll-bar .thumb { -fx-background-color: #1abc9c; -fx-background-radius: 6px; }" +
            ".scroll-bar .increment-button, .scroll-bar .decrement-button { -fx-background-color: transparent; }"
        );

        getChildren().addAll(topRow, sp);

        buildCourses();
    }

    private void buildCourses() {
        contentBox.getChildren().clear();
        List<String> categories = parseCategories(controller.categoriesToString());
        for (String category : categories) {
            contentBox.getChildren().add(createCategoryCard(category));
        }
    }
    
    private void styleAccentButton(Button b) {
        b.setStyle(
                "-fx-background-color: #2ea27a; -fx-text-fill: white; -fx-font-weight: 700; " +
                        "-fx-background-radius: 10; -fx-padding: 8 14;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: #34b988; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 8 14;"
        ));
        b.setOnMouseExited(e -> styleAccentButton(b));
    }
    
    private void stylePrimaryButtonSmall(Button b) {
        b.setStyle("-fx-background-color: transparent; -fx-border-color: #c7d2d9; -fx-text-fill: #2c3e50; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 6 10;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: rgba(0,0,0,0.04); -fx-border-color: #b5c2c9; -fx-text-fill: #2c3e50; -fx-font-weight: 600; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 6 10;"));
        b.setOnMouseExited(e -> stylePrimaryButtonSmall(b));
    }

    private VBox createCategoryCard(String categoryName) {
        VBox wrapper = new VBox(8);
        wrapper.setPadding(new Insets(6));

        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(255,255,255,0.03), rgba(255,255,255,0.01));" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.06);"
        );
        card.setEffect(new DropShadow(8, Color.rgb(20, 20, 20, 0.10)));

        Label name = new Label(categoryName);
        name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50; -fx-font-weight: 600;");

        int subjectCount = safeGetSubjects(categoryName).size();
        Label meta = new Label(subjectCount + " subjects");
        meta.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

        Label arrow = new Label("▶");
        arrow.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        HBox header = new HBox(12, arrow, name);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = makeMiniButton("➕", "#27ae60");
        addBtn.setTooltip(new Tooltip("Add subject"));
        addBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Subject");
            dialog.setHeaderText("Enter subject name for " + categoryName);
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    controller.addSubject(categoryName, input.trim());
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        });

        Button editBtn = makeMiniButton("🖉", "#2980b9");
        editBtn.setTooltip(new Tooltip("Edit category"));
        editBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(categoryName);
            dialog.setTitle("Edit Category");
            dialog.setHeaderText("Edit category name");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    controller.editCategory(categoryName, input.trim());
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        });

        Button deleteBtn = makeMiniButton("❌", "#c0392b");
        deleteBtn.setTooltip(new Tooltip("Delete category"));
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete category \"" + categoryName + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Delete Category");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        controller.removeCategory(categoryName);
                        refresh();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                }
            });
        });

        HBox actions = new HBox(6, addBtn, editBtn, deleteBtn);
        actions.setStyle("-fx-alignment: center-right;");

        HBox headerRow = new HBox(8, header, spacer, meta, actions);
        headerRow.setStyle("-fx-alignment: center-left; -fx-padding: 4 0 4 0;");

        VBox subjectsContainer = new VBox(10);
        subjectsContainer.setPadding(new Insets(8, 4, 4, 12));
        subjectsContainer.setVisible(false);
        subjectsContainer.setManaged(false);

        List<String> subjects = safeGetSubjects(categoryName);
        if (subjects.isEmpty()) {
            Label none = new Label("No subjects");
            none.setStyle("-fx-text-fill: #7f8c8d;");
            subjectsContainer.getChildren().add(none);
        } else {
            for (String subj : subjects) {
                subjectsContainer.getChildren().add(createSubjectCard(categoryName, subj));
            }
        }

        headerRow.setOnMouseClicked(ev -> {
            if (isEventFromAction(ev.getTarget(), actions)) return;
            boolean show = !subjectsContainer.isVisible();
            toggleContainer(subjectsContainer, show);
            arrow.setText(show ? "▼" : "▶");
        });

        card.getChildren().addAll(headerRow, subjectsContainer);
        wrapper.getChildren().add(card);
        return wrapper;
    }

    private VBox createSubjectCard(String categoryName, String subjectName) {
        VBox wrapper = new VBox(6);
        wrapper.setPadding(new Insets(2));

        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: #eff3f6; " +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(0,0,0,0.03);"
        );
        card.setEffect(new DropShadow(6, Color.rgb(20, 20, 20, 0.05)));

        Label name = new Label(subjectName);
        name.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e; -fx-font-weight: 600;");

        int taskCount = safeGetTasks(categoryName, subjectName).size();
        Label meta = new Label(taskCount + " tasks");
        meta.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

        Label arrow = new Label("▶");
        arrow.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13px;");

        Button addTaskBtn = makeMiniButton("➕", "#27ae60");
        addTaskBtn.setTooltip(new Tooltip("Add task"));
        Button editSubjectBtn = makeMiniButton("✏️", "#2980b9");
        editSubjectBtn.setTooltip(new Tooltip("Edit subject"));
        Button delSubjectBtn = makeMiniButton("❌", "#c0392b");
        delSubjectBtn.setTooltip(new Tooltip("Delete subject"));

        HBox actions = new HBox(6, addTaskBtn, editSubjectBtn, delSubjectBtn);
        actions.setStyle("-fx-alignment: center-right;");

        HBox header = new HBox(8, arrow, name);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox headerRow = new HBox(8, header, spacer, meta, actions);
        headerRow.setStyle("-fx-alignment: center-left;");

        VBox tasksContainer = new VBox(12);
        tasksContainer.setPadding(new Insets(8, 4, 4, 12));
        tasksContainer.setVisible(false);
        tasksContainer.setManaged(false);

        List<String> tasks = safeGetTasks(categoryName, subjectName);
        if (tasks.isEmpty()) {
            Label none = new Label("No tasks");
            none.setStyle("-fx-text-fill: #7f8c8d;");
            tasksContainer.getChildren().add(none);
        } else {
            for (String tline : tasks) {
                TaskInfo info = parseTaskLine(tline);
                tasksContainer.getChildren().add(createTaskCard(categoryName, subjectName, info));
            }
        }

        headerRow.setOnMouseClicked(ev -> {
            if (isEventFromAction(ev.getTarget(), actions)) return;
            boolean show = !tasksContainer.isVisible();
            toggleContainer(tasksContainer, show);
            arrow.setText(show ? "▼" : "▶");
        });

        addTaskBtn.setOnAction(ev -> showAddTaskDialog(categoryName, subjectName, null));

        editSubjectBtn.setOnAction(ev -> {
            TextInputDialog dialog = new TextInputDialog(subjectName);
            dialog.setTitle("Edit Subject");
            dialog.setHeaderText("Enter new subject name");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(newName -> {
                try {
                    controller.editSubject(categoryName, subjectName, newName.trim());
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                }
            });
        });

        delSubjectBtn.setOnAction(ev -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete subject \"" + subjectName + "\" from category \"" + categoryName + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Delete Subject");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    try {
                        controller.removeSubject(categoryName, subjectName);
                        refresh();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
                    }
                }
            });
        });

        card.getChildren().addAll(headerRow, tasksContainer);
        wrapper.getChildren().add(card);
        return wrapper;
    }

    private VBox createTaskCard(String categoryName, String subjectName, TaskInfo info) {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(6));

        HBox card = new HBox(12);
        card.setPadding(new Insets(10));
        String baseBackground = "-fx-background-color: linear-gradient(to right, #ffffff, #f7fbff);";
        String completedBackground = "-fx-background-color: linear-gradient(to right, #e6fff0, #d5f8df);";
        card.setStyle(
                baseBackground +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: rgba(0,0,0,0.03);"
        );
        card.setEffect(new DropShadow(4, Color.rgb(10, 20, 40, 0.06)));

        if (info.completed) {
            card.setStyle(completedBackground + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(0,0,0,0.03);");
        }

        VBox infoBox = new VBox(6);
        Label name = new Label(info.name);
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #2c3e50;");
        Label desc = new Label(info.description != null && !info.description.isBlank() ? info.description : "No description");
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #566573;");
        desc.setWrapText(true);
        desc.setMaxWidth(520);

        infoBox.getChildren().addAll(name, desc);

        if (info.links != null && !info.links.isEmpty()) {
            VBox linksBox = new VBox(4);
            linksBox.setPadding(new Insets(6, 0, 0, 0));
            for (Map.Entry<String, String> e : info.links.entrySet()) {
                String linkName = e.getKey();
                String url = e.getValue();
                Hyperlink link = new Hyperlink(linkName);
                link.setTooltip(new Tooltip(url));
                link.setStyle("-fx-font-size: 12px; -fx-text-fill: #2e86c1;");
                link.setOnAction(ev -> openUrl(url));
                Label urlLabel = new Label(url);
                urlLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                VBox row = new VBox(2, link, urlLabel);
                linksBox.getChildren().add(row);
            }
            infoBox.getChildren().add(linksBox);
        }

        VBox metaBox = new VBox(6);
        metaBox.setPadding(new Insets(2, 0, 0, 0));
        metaBox.setPrefWidth(160);

        Label dueLabel = new Label(info.due == null ? "" : "⏰ " + info.due);
        dueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e; -fx-font-weight: 600;");

        Label diffLabel = new Label("Difficulty: " + (info.difficulty == null ? "?" : info.difficulty));
        diffLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        metaBox.getChildren().addAll(dueLabel, diffLabel);

        VBox actions = new VBox(8);
        actions.setPrefWidth(140);
        Button editBtn = new Button("Edit");
        editBtn.setMinHeight(36);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 6;");
        editBtn.setOnAction(e -> showAddTaskDialog(categoryName, subjectName, info));

        Button removeBtn = new Button("Delete");
        removeBtn.setMinHeight(36);
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 6;");
        removeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete task \"" + info.name + "\"?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Delete Task");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try {
                        controller.removeTask(categoryName, subjectName, info.name);
                        refresh();
                    } catch (Exception ex) {
                        String stored = controller.tasksToString(categoryName, subjectName);
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Failed to delete task");
                        a.setHeaderText("Could not delete task: " + info.name);
                        a.setContentText(ex.getMessage() == null ? "(no message)" : ex.getMessage());
                        TextArea area = new TextArea(stored == null ? "" : stored);
                        area.setEditable(false);
                        area.setWrapText(true);
                        a.getDialogPane().setExpandableContent(area);
                        a.showAndWait();
                    }
                }
            });
        });

        ToggleButton finishBtn = new ToggleButton(info.completed ? "Finished" : "Finish");
        finishBtn.setMinHeight(36);
        finishBtn.setMaxWidth(Double.MAX_VALUE);
        finishBtn.setStyle(info.completed ? "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6;" :
                "-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-background-radius: 6;");
        finishBtn.setSelected(info.completed);
        finishBtn.setOnAction(ev -> {
            boolean nowFinished = finishBtn.isSelected();
            if (nowFinished) {
                card.setStyle("-fx-background-color: linear-gradient(to right, #e6fff0, #d5f8df);" +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(0,0,0,0.03);");
                finishBtn.setText("Finished");
                finishBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6;");
            } else {
                card.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #f7fbff);" +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(0,0,0,0.03);");
                finishBtn.setText("Finish");
                finishBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-background-radius: 6;");
            }
        });

        actions.getChildren().addAll(editBtn, removeBtn, finishBtn);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(infoBox, metaBox, actions);

        wrapper.getChildren().add(card);
        return wrapper;
    }

    private void showAddTaskDialog(String categoryName, String subjectName, TaskInfo prefill) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(prefill == null ? "Add Task" : "Edit Task");
        dlg.setHeaderText((prefill == null ? "Create a new task for " : "Edit task \"") + subjectName + (prefill == null ? "\"" : "\""));

        TextField nameField = new TextField();
        nameField.setPromptText("Task name");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description (optional)");
        descArea.setPrefRowCount(4);

        ChoiceBox<Difficulty> diffChoice = new ChoiceBox<>();
        diffChoice.getItems().addAll(Difficulty.values());
        diffChoice.getSelectionModel().selectFirst();

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Due date (optional)");

        TextField timeField = new TextField();
        timeField.setPromptText("Time (HH:mm) optional");

        VBox linksList = new VBox(6);
        linksList.setPadding(new Insets(4));
        Button addLinkRowBtn = new Button("+ Add link");
        addLinkRowBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;");
        addLinkRowBtn.setOnAction(e -> linksList.getChildren().add(makeLinkRow("", "")));

        if (prefill != null) {
            nameField.setText(prefill.name);
            descArea.setText(prefill.description);
            if (prefill.difficulty != null) {
                try { diffChoice.setValue(Difficulty.valueOf(prefill.difficulty)); } catch (Exception ignored) {}
            }
            if (prefill.due != null) {
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
                    LocalDateTime dt = LocalDateTime.parse(prefill.due, f);
                    datePicker.setValue(dt.toLocalDate());
                    timeField.setText(dt.toLocalTime().getHour() + ":" + String.format("%02d", dt.toLocalTime().getMinute()));
                } catch (Exception ignored) { }
            }
            if (prefill.links != null && !prefill.links.isEmpty()) {
                for (Map.Entry<String, String> en : prefill.links.entrySet()) {
                    linksList.getChildren().add(makeLinkRow(en.getKey(), en.getValue()));
                }
            }
        } else {
            linksList.getChildren().add(makeLinkRow("", ""));
        }

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(8);
        grid.setPadding(new Insets(8));
        grid.add(new Label("Name*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description"), 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(new Label("Difficulty"), 0, 2);
        grid.add(diffChoice, 1, 2);
        grid.add(new Label("Due date"), 0, 3);
        HBox dateRow = new HBox(8, datePicker, timeField);
        grid.add(dateRow, 1, 3);
        grid.add(new Label("Links"), 0, 4);
        VBox linksArea = new VBox(6, linksList, addLinkRowBtn);
        grid.add(linksArea, 1, 4);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Node okButton = dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        nameField.textProperty().addListener((obs, o, n) -> okButton.setDisable(n == null || n.trim().isEmpty()));

        Optional<ButtonType> result = dlg.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            Difficulty diff = diffChoice.getValue();
            LocalDate date = datePicker.getValue();
            LocalTime time = null;
            if (timeField.getText() != null && !timeField.getText().trim().isEmpty()) {
                try {
                    time = LocalTime.parse(timeField.getText().trim(), TIME_FMT);
                } catch (DateTimeParseException ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid time format. Use H:mm (e.g. 9:30 or 14:05).", ButtonType.OK).showAndWait();
                    return;
                }
            }
            LocalDateTime due = null;
            if (date != null) {
                if (time == null) time = LocalTime.of(23, 59);
                due = LocalDateTime.of(date, time);
            }

            Map<String,String> links = new LinkedHashMap<>();
            for (Node n : linksList.getChildren()) {
                if (n instanceof HBox) {
                    HBox row = (HBox) n;
                    TextField nameFld = (TextField) row.getChildren().get(0);
                    TextField urlFld = (TextField) row.getChildren().get(1);
                    String ln = nameFld.getText().trim();
                    String u  = urlFld.getText().trim();
                    if (!ln.isEmpty() && !u.isEmpty()) {
                        links.put(ln, u);
                    }
                }
            }

            try {
                if (prefill == null) {
                    controller.addTask(categoryName, subjectName, name, desc, diff, due);
                    for (Map.Entry<String,String> en : links.entrySet()) {
                        controller.addTaskLink(categoryName, subjectName, name, en.getKey(), en.getValue());
                    }
                } else {
                    controller.editTask(categoryName, subjectName, prefill.name, name, desc, diff, due);
                    Map<String,String> existing = parseLinksString(controller.getTaskLinks(categoryName, subjectName, name));
                    for (String ln : existing.keySet()) {
                        try { controller.removeTaskLink(categoryName, subjectName, name, ln); } catch (Exception ignored) {}
                    }
                    for (Map.Entry<String,String> en : links.entrySet()) {
                        controller.addTaskLink(categoryName, subjectName, name, en.getKey(), en.getValue());
                    }
                }
                refresh();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }

    private HBox makeLinkRow(String linkName, String url) {
        TextField nameFld = new TextField(linkName);
        nameFld.setPromptText("Link name");
        TextField urlFld = new TextField(url);
        urlFld.setPromptText("https://...");
        Button rm = new Button("✖");
        rm.setStyle("-fx-background-color: transparent; -fx-text-fill: #c0392b;");
        rm.setOnAction(e -> ((VBox) nameFld.getParent().getParent()).getChildren().remove(nameFld.getParent()));
        HBox row = new HBox(8, nameFld, urlFld, rm);
        row.setPrefWidth(560);
        HBox.setHgrow(nameFld, Priority.ALWAYS);
        HBox.setHgrow(urlFld, Priority.ALWAYS);
        return row;
    }

    private boolean isEventFromAction(Object target, HBox actions) {
        if (!(target instanceof Node)) return false;
        Node n = (Node) target;
        while (n != null) {
            if (n == actions) return true;
            if (n.getParent() == null) break;
            n = n.getParent();
        }
        return false;
    }

    private void toggleContainer(VBox container, boolean show) {
        if (container == null) return;
        if (show) {
            container.setManaged(true);
            container.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(220), container);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            TranslateTransition tt = new TranslateTransition(Duration.millis(220), container);
            tt.setFromY(-6);
            tt.setToY(0);
            tt.play();
        } else {
            FadeTransition ft = new FadeTransition(Duration.millis(160), container);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(e -> {
                container.setVisible(false);
                container.setManaged(false);
            });
            ft.play();
            TranslateTransition tt = new TranslateTransition(Duration.millis(160), container);
            tt.setFromY(0);
            tt.setToY(-6);
            tt.play();
        }
    }

    private TaskInfo parseTaskLine(String line) {
        if (line == null || line.isBlank()) return new TaskInfo(line);
        String cleaned = line.replaceFirst("^\\d+\\.\\s*", "").trim();
        Pattern p = Pattern.compile(
                "^Task\\[name='(.*?)', description='(.*?)', difficulty=([^,\\]]+), due=([^,\\]]+), completed=(true|false)(?: \\| Links: (.*?))?(?:,.*)?\\]$"
        );
        Matcher m = p.matcher(cleaned);
        if (m.find()) {
            String name = m.group(1);
            String desc = m.group(2);
            String diff = m.group(3);
            String due = m.group(4);
            String completed = m.group(5);
            String linksRaw = m.group(6);

            Map<String, String> links = new LinkedHashMap<>();
            if (linksRaw != null && !linksRaw.isBlank()) {
                String[] parts = linksRaw.split(";");
                for (String ppart : parts) {
                    String trimmed = ppart.trim();
                    if (trimmed.isEmpty()) continue;
                    String[] kv = trimmed.split("\\s*->\\s*", 2);
                    if (kv.length == 2) links.put(kv[0].trim(), kv[1].trim());
                }
            }

            if ("none".equalsIgnoreCase(due)) due = null;

            return new TaskInfo(name, desc, diff, due, Boolean.parseBoolean(completed), links);
        }

        Matcher nameOnly = Pattern.compile("name='(.*?)'").matcher(cleaned);
        if (nameOnly.find()) {
            String name = nameOnly.group(1);
            return new TaskInfo(name, "", null, null, false, Map.of());
        }
        return new TaskInfo(cleaned);
    }

    private Map<String,String> parseLinksString(String raw) {
        Map<String,String> out = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) return out;
        Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(line -> {
                    String[] kv = line.split("\\s*->\\s*", 2);
                    if (kv.length == 2) out.put(kv[0].trim(), kv[1].trim());
                });
        return out;
    }

    private List<String> safeGetSubjects(String categoryCandidate) {
        String raw = controller.subjectsToString(categoryCandidate);
        if (raw == null) return List.of();
        if (raw.startsWith("Category not found")) return List.of();
        return parseSubjects(raw);
    }

    private List<String> safeGetTasks(String categoryCandidate, String subjectCandidate) {
        String raw = controller.tasksToString(categoryCandidate, subjectCandidate);
        if (raw == null) return List.of();
        if (raw.startsWith("Subject not found")) return List.of();
        return parseTasks(raw);
    }

    private List<String> parseCategories(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(line -> {
                    int idx = line.indexOf(" (");
                    return idx > 0 ? line.substring(0, idx).trim() : line;
                })
                .collect(Collectors.toList());
    }

    private List<String> parseSubjects(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(line -> {
                    int idx = line.indexOf(" (");
                    return idx > 0 ? line.substring(0, idx).trim() : line;
                })
                .collect(Collectors.toList());
    }

    private List<String> parseTasks(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Button makeMiniButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 3 8;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: derive(" + color + ",20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 3 8;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 3 8;"
        ));
        return btn;
    }

    private void openUrl(String url) {
        if (url == null || url.isBlank()) {
            new Alert(Alert.AlertType.INFORMATION, "Invalid URL", ButtonType.OK).showAndWait();
            return;
        }
        try {
            if (hostServices != null) {
                hostServices.showDocument(url);
            } else {
                new Alert(Alert.AlertType.INFORMATION, url, ButtonType.OK).showAndWait();
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.INFORMATION, url, ButtonType.OK).showAndWait();
        }
    }

    private static class TaskInfo {
        final String name;
        final String description;
        final String difficulty;
        final String due;
        final boolean completed;
        final Map<String, String> links;

        TaskInfo(String raw) {
            this.name = raw;
            this.description = "";
            this.difficulty = null;
            this.due = null;
            this.completed = false;
            this.links = Map.of();
        }

        TaskInfo(String name, String description, String difficulty, String due, boolean completed, Map<String, String> links) {
            this.name = Objects.requireNonNullElse(name, "");
            this.description = Objects.requireNonNullElse(description, "");
            this.difficulty = difficulty;
            this.due = due;
            this.completed = completed;
            this.links = links == null ? Map.of() : new LinkedHashMap<>(links);
        }
    }

    public void refresh() {
        buildCourses();
    }
}
