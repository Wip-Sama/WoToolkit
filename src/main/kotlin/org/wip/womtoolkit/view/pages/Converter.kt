package org.wip.womtoolkit.view.pages

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane

class Converter : BorderPane() {
	init {
		FXMLLoader(javaClass.getResource("/view/pages/converter.fxml")).apply {
			setRoot(this@Converter)
			setController(this@Converter)
			load()
		}
	}
}