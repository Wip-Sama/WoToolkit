package org.wip.womtoolkit.view.pages.settings

import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox

/**
 * @author Wip
 * This is not really a settings class, it only contains information about the application
 * */
class AboutSettings : VBox() {
	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/aboutSettings.fxml")).apply {
			setRoot(this@AboutSettings)
			setController(this@AboutSettings)
			load()
		}
	}
}