package org.wip.womtoolkit.view.pages

import javafx.fxml.FXML
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

    @FXML
    fun initialize() {
    }
}