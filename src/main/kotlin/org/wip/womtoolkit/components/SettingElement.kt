package org.wip.womtoolkit.components

import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane

class SettingElement : BorderPane() {
	init {
		FXMLLoader(javaClass.getResource("/components/settingElement.fxml")).apply {
			setRoot(this@SettingElement)
			setController(this@SettingElement)
			load()
		}
	}
}