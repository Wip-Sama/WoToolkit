package org.wip.womtoolkit.view.pages.settings

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox
import org.wip.womtoolkit.view.components.SettingElement
import org.wip.womtoolkit.view.components.VersionDisplay

class ModuleSettings : VBox() {

	@FXML lateinit var slicerSetting: SettingElement

	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/moduleSettings.fxml")).apply {
			setRoot(this@ModuleSettings)
			setController(this@ModuleSettings)
			load()
		}
	}

	@FXML fun initialize() {
		slicerSetting.apply {
			quickSetting = VersionDisplay()
		}
	}
}