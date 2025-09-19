package gui;

import domein.CategoryController;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoursesPage extends VBox {

    private final CategoryController controller;
    private final VBox contentBox;
    private final HostServices hostServices;

    public CoursesPage(HostServices hostServices) {
        this.hostServices = hostServices;
        setSpacing(16);
        setPadding(new Insets(22));
        controller = new CategoryController();

        Label title = new Label("Courses");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #2c3e50; -fx-font-weight: 700;");

        Label subtitle = new Label("Expand a subject to view its tasks. Click a link to open it in your browser.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        contentBox = new VBox(14);
        contentBox.setPadding(new Insets(6));

        ScrollPane sp = new ScrollPane(contentBox);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");
        sp.setPrefHeight(520);

        getChildren().addAll(title, subtitle, sp);

        buildCourses();
    }

    private void buildCourses() {
        contentBox.getChildren().clear();
        List<String> categories = parseCategories(controller.categoriesToString());
        for (String category : categories) {
            contentBox.getChildren().add(createCategoryCard(category));
        }
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
        HBox headerRow = new HBox(8, header, spacer, meta);
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

        HBox header = new HBox(8, arrow, name);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox headerRow = new HBox(8, header, spacer, meta);
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
            boolean show = !tasksContainer.isVisible();
            toggleContainer(tasksContainer, show);
            arrow.setText(show ? "▼" : "▶");
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
        card.setStyle(
                "-fx-background-color: linear-gradient(to right, #ffffff, #f7fbff);" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: rgba(0,0,0,0.03);"
        );
        card.setEffect(new DropShadow(4, Color.rgb(10, 20, 40, 0.06)));

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

        Label dueLabel = new Label(info.due == null ? "No due" : "⏰ " + info.due);
        dueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e; -fx-font-weight: 600;");

        Label diffLabel = new Label("Difficulty: " + (info.difficulty == null ? "?" : info.difficulty));
        diffLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        metaBox.getChildren().addAll(dueLabel, diffLabel);

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        card.getChildren().addAll(infoBox, metaBox);

        wrapper.getChildren().add(card);
        return wrapper;
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

    private void toggleContainer(VBox container, boolean show) {
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

    private void showTaskDetails(String categoryName, String subjectName, String taskName) {
        String details;
        try {
            details = controller.getTaskLinks(categoryName, subjectName, taskName);
        } catch (Exception ex) {
            details = "Unable to load task details.";
        }
        if (details == null || details.isBlank()) details = "No links for this task.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Details");
        alert.setHeaderText(taskName);
        alert.setContentText(details);
        alert.showAndWait();
    }

    private List<String> safeGetSubjects(String categoryCandidate) {
        String canonical = findCanonicalCategory(categoryCandidate);
        if (canonical == null) return List.of();
        String raw = controller.subjectsToString(canonical);
        if (raw == null) return List.of();
        if (raw.startsWith("Category not found")) return List.of();
        return parseSubjects(raw);
    }

    private List<String> safeGetTasks(String categoryCandidate, String subjectCandidate) {
        String canonicalCat = findCanonicalCategory(categoryCandidate);
        if (canonicalCat == null) return List.of();
        String canonicalSub = findCanonicalSubject(canonicalCat, subjectCandidate);
        if (canonicalSub == null) return List.of();
        String raw = controller.tasksToString(canonicalCat, canonicalSub);
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

    private TaskInfo parseTaskLine(String line) {
        if (line == null || line.isBlank()) return new TaskInfo(line);

        String cleaned = line.replaceFirst("^\\d+\\.\\s*", "").trim();

        Pattern p = Pattern.compile("^Task\\[name='(.*?)', description='(.*?)', difficulty=(.*?), due=(.*?), completed=(true|false)(?: \\| Links: (.*))?\\]$");
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
        } else {
            return new TaskInfo(cleaned);
        }
    }

    private String findCanonicalCategory(String candidate) {
        if (candidate == null) return null;
        List<String> cats = parseCategories(controller.categoriesToString());
        String trimmed = candidate.trim();
        for (String c : cats) {
            if (c.equalsIgnoreCase(trimmed)) return c;
        }
        return null;
    }

    private String findCanonicalSubject(String canonicalCategory, String candidateSubject) {
        if (candidateSubject == null) return null;
        List<String> subs = parseSubjects(controller.subjectsToString(canonicalCategory));
        String trimmed = candidateSubject.trim();
        for (String s : subs) {
            if (s.equalsIgnoreCase(trimmed)) return s;
        }
        return null;
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
