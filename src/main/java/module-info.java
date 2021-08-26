module ixidev.javafxtemplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires javafx.graphics;
    opens ixidev.javafxtemplate to javafx.fxml;
    exports ixidev.javafxtemplate;
}