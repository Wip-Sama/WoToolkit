package org.wip.womtoolkit.view.settings

import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox

class SlicerSettings : VBox() {
	init {
		FXMLLoader(javaClass.getResource("/pages/settings/slicerSettings.fxml")).apply {
			setRoot(this@SlicerSettings)
			setController(this@SlicerSettings)
			load()
		}
	}
}