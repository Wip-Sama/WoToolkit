package org.wip.womtoolkit.view.pages.settings

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.VBox
import org.wip.womtoolkit.view.components.SettingElement
import java.util.concurrent.Executor

class SlicerSettings : VBox() {

	@FXML lateinit var heightSetting: SettingElement
	@FXML lateinit var subFolderSetting: SettingElement
	@FXML lateinit var archiveSetting: SettingElement
	@FXML lateinit var parallelExecution: SettingElement
	@FXML lateinit var outputFormatSetting: SettingElement
	@FXML lateinit var cutToleranceSetting: SettingElement
	@FXML lateinit var searchDirectionSetting: SettingElement
	@FXML lateinit var lineEvaluationSetting: SettingElement

	init {
		FXMLLoader(javaClass.getResource("/view/pages/settings/slicerSettings.fxml")).apply {
			setRoot(this@SlicerSettings)
			setController(this@SlicerSettings)
			load()
		}
	}

	@FXML
	fun initialize() {

	}
}
