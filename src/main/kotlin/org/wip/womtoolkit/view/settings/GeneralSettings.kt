package org.wip.womtoolkit.view.settings

import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox
import org.wip.womtoolkit.view.settings.AboutSettings

/**
 * @author Wip
 * This class represents the general settings view in the application.
 * It contains: Accent color, Theme color, Language, Starting page.
 * */
class GeneralSettings : VBox() {
	init {
		FXMLLoader(javaClass.getResource("/pages/settings/generalSettings.fxml")).apply {
			setRoot(this@GeneralSettings)
			setController(this@GeneralSettings)
			load()
		}
	}
}