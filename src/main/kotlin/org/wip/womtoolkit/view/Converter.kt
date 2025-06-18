package org.wip.womtoolkit.view

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane

class Converter : BorderPane() {
	init {
		FXMLLoader(javaClass.getResource("/pages/converter.fxml")).apply {
			setRoot(this@Converter)
			setController(this@Converter)
			load()
		}
	}
}