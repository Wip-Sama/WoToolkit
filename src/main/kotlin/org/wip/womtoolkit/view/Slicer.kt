package org.wip.womtoolkit.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Slider
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region

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