package org.wip.womtoolkit.view.pages.settings

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox
import org.wip.womtoolkit.view.components.SettingElement

class HistorySettings: VBox() {
	@FXML lateinit var actionsSetting: SettingElement
	@FXML lateinit var notificationsSettings: SettingElement
	@FXML lateinit var logsSettings: SettingElement

	init {
		FXMLLoader(HistorySettings::class.java.getResource("/view/pages/settings/historySettings.fxml")).apply {
			setRoot(this@HistorySettings)
			setController(this@HistorySettings)
			load()
		}

		@FXML
		fun initialize() {

		}
	}
}