package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class Slicer {
    val root: Parent = FXMLLoader(javaClass.getResource("/pages/slicer.fxml")).load()

}