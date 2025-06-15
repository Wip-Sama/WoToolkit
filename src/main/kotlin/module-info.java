module org.wip.womtoolkit {
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires nfx.core;

    opens org.wip.womtoolkit to javafx.graphics, javafx.fxml;
    opens org.wip.womtoolkit.view to javafx.fxml;
    opens org.wip.womtoolkit.components.collapsablesidebarmenu to javafx.fxml;
}

