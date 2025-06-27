package org.wip.womtoolkit.view.pages.settings

import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox

class ConverterSettings : VBox() {
	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/converterSettings.fxml")).apply {
			setRoot(this@ConverterSettings)
			setController(this@ConverterSettings)
			load()
		}
	}
}