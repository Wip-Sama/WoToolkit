package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class Converter {
	val root: Parent = FXMLLoader(javaClass.getResource("/pages/converter.fxml")).load()
}