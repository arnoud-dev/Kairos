package gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import domein.CategoryController;

public class KairosGui extends Application {

    private StackPane mainContent;
    private Map<String, Button> sidebarButtons = new HashMap<>();
    private Map<String, Node> pages = new HashMap<>();
    private String activePage = "";
    CategoryController controller = new CategoryController();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // ---------------- Sidebar ----------------
        VBox leftSidebar = new VBox(20);
        leftSidebar.setStyle("-fx-background-color: #2c3e50;");
        leftSidebar.setPadding(new Insets(20));
        leftSidebar.setPrefWidth(220);

        ImageView logoView;
        try {
            Image logo = new Image("file:resources/logo.png");
            logoView = new ImageView(logo);
        } catch (Exception e) {
            System.out.println("Logo not found, using placeholder.");
            logoView = new ImageView();
            logoView.setStyle("-fx-background-color: gray;");
        }
        logoView.setFitWidth(50);
        logoView.setFitHeight(50);
        logoView.setPreserveRatio(true);

        Label title = new Label("Kairos");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox logoTitleBox = new HBox(10, logoView, title);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);

        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.TOP_CENTER);

        String[] pageNames = {"Dashboard", "Progress", "Courses", "Schedule"};

        pages.put("Dashboard", new DashboardPage(controller));
        pages.put("Progress", new ProgressPage());
        pages.put("Courses", new CoursesPage(getHostServices(), controller));
        pages.put("Schedule", new SchedulePage());

        for (String page : pageNames) {
            Button btn = new Button(page);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px;");

            btn.setOnMouseEntered(e -> {
                if (!page.equals(activePage))
                    btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px;");
            });
            btn.setOnMouseExited(e -> {
                if (!page.equals(activePage))
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px;");
            });

            btn.setOnAction(e -> switchContent(page));

            sidebarButtons.put(page, btn);
            buttonsBox.getChildren().add(btn);
        }

        leftSidebar.getChildren().addAll(logoTitleBox, buttonsBox);

        // ---------------- Main Content ----------------
        mainContent = new StackPane();
        mainContent.setStyle("-fx-background-color: #ecf0f1;");

        root.setLeft(leftSidebar);
        root.setCenter(mainContent);

        switchContent("Dashboard");

        // ---------------- Scene & Stage ----------------
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Kairos Dashboard");

        try {
            Image appIcon = new Image("file:resources/logo.png");
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.out.println("App icon not found.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void switchContent(String page) {
        if (!activePage.isEmpty()) {
            Button old = sidebarButtons.get(activePage);
            if (old != null)
                old.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px;");
        }

        activePage = page;
        Button activeBtn = sidebarButtons.get(page);
        if (activeBtn != null)
            activeBtn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 16px;");

        mainContent.getChildren().clear();

        Node node = pages.get(page);
        if (node != null) {
            node.setOpacity(0);
            mainContent.getChildren().add(node);

            FadeTransition ft = new FadeTransition(Duration.millis(260), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
