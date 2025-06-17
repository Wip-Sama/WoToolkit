package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class Settings {
	val root: Parent = FXMLLoader(javaClass.getResource("/pages/settings.fxml")).load()
}