package org.wip.womtoolkit.view.components

import javafx.fxml.FXMLLoader
import javafx.scene.layout.GridPane

class SingleSettingGrid : GridPane() {
	init {
		FXMLLoader(javaClass.getResource("/view/components/settingGrid.fxml")).apply {
			setRoot(this@SingleSettingGrid)
			setController(this@SingleSettingGrid)
			load()
		}
	}
}