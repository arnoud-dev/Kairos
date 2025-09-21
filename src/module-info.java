module Kairos {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;

    exports gui;
    opens gui to javafx.fxml;
}