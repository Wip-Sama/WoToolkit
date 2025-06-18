module org.wip.womtoolkit {
    requires java.logging;
    requires java.desktop;
    requires java.base;
    requires java.management;

    requires kotlin.stdlib;

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires nfx.core;

    //io.reactivex.rxjava3.core.Observable
    requires io.reactivex.rxjava3;

    opens org.wip.womtoolkit to javafx.graphics, javafx.fxml;
    opens org.wip.womtoolkit.view to javafx.fxml;
    opens org.wip.womtoolkit.components to javafx.fxml;
    opens org.wip.womtoolkit.components.collapsablesidebarmenu to javafx.fxml;
}

