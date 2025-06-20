package org.wip.womtoolkit.view.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.VBox
import kotlin.compareTo

/**
 * @author Wip
 * This class represents the general settings view in the application.
 * It contains: Accent color, Theme color, Language, Starting page.
 * */
class GeneralSettings : VBox() {
	@FXML lateinit var combotest: ChoiceBox<String>

	init {
		FXMLLoader(javaClass.getResource("/pages/settings/generalSettings.fxml")).apply {
			setRoot(this@GeneralSettings)
			setController(this@GeneralSettings)
			load()
		}
	}


	@FXML
	fun initialize() {
		combotest.items.add("Test1")
		combotest.items.add("Test2")
		combotest.items.add("Test3")
		combotest.selectionModel.select(0)
		combotest.onMouseClicked = EventHandler {
			combotest.hide()
			Platform.runLater {
				combotest.show()
			}
		}
	}
}