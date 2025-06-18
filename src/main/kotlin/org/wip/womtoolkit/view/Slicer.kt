package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane

class Slicer : BorderPane() {
    init {
        FXMLLoader(javaClass.getResource("/pages/slicer.fxml")).apply {
            setRoot(this@Slicer)
            setController(this@Slicer)
            load()
        }
    }
}