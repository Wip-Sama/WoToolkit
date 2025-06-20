package org.wip.womtoolkit.view.settings

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.wip.womtoolkit.components.SettingElement
import org.wip.womtoolkit.components.Switch

/**
 * @author Wip
 * This class represents the general settings view in the application.
 * It contains: Accent color, Theme color, Language, Starting page.
 * */
class GeneralSettings : VBox() {
	@FXML lateinit var accentSetting: SettingElement
	@FXML lateinit var themeSetting: SettingElement
	@FXML lateinit var localizationSetting: SettingElement
	@FXML lateinit var startingPageSetting: SettingElement

	init {
		FXMLLoader(javaClass.getResource("/pages/settings/generalSettings.fxml")).apply {
			setRoot(this@GeneralSettings)
			setController(this@GeneralSettings)
			load()
		}
	}

	@FXML
	fun initialize() {
//		combotest.onMouseClicked = EventHandler {
//			combotest.hide()
//			Platform.runLater {
//				combotest.show()
//			}
//		}
		accentSetting.setTitle("Accent")
		accentSetting.quickSetting = Switch()
		accentSetting.expandableContent = Pane().apply {
			styleClass.add("-fx-background-color: #ff0000;")
			prefHeight = 100.0
		}
	}
}