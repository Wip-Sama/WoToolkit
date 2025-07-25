module org.wip.womtoolkit {
    requires java.logging;
    requires java.desktop;
    requires java.base;
    requires java.management;

    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires kotlinx.coroutines.core;
    requires kotlinx.coroutines.javafx;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires nfx.core;

    opens org.wip.womtoolkit to javafx.graphics, javafx.fxml;
    opens org.wip.womtoolkit.model to kotlin.reflect;
    opens org.wip.womtoolkit.model.database.entities to kotlin.reflect;

    opens org.wip.womtoolkit.view.components to javafx.fxml;
    opens org.wip.womtoolkit.view.components.collapsablesidebarmenu to javafx.fxml;
    opens org.wip.womtoolkit.view.components.colorpicker to javafx.fxml;
    opens org.wip.womtoolkit.view.components.notifications to javafx.fxml;

    opens org.wip.womtoolkit.view.pages to javafx.fxml;
    opens org.wip.womtoolkit.view.pages.settings to javafx.fxml;
}
