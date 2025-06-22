module org.wip.womtoolkit {
    requires java.logging;
    requires java.desktop;
    requires java.base;
    requires java.management;

    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires kotlinx.coroutines.javafx;

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires nfx.core;

    opens org.wip.womtoolkit to javafx.graphics, javafx.fxml;
    opens org.wip.womtoolkit.view to javafx.fxml;
    opens org.wip.womtoolkit.components to javafx.fxml;
    opens org.wip.womtoolkit.components.collapsablesidebarmenu to javafx.fxml;
    opens org.wip.womtoolkit.components.colorpicker to javafx.fxml;
    opens org.wip.womtoolkit.view.settings to javafx.fxml;
}

