package org.wip.womtoolkit.view.settings

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.VBox

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
		combotest.items.addAll("Item 1", "Item 2", "Item 3")
		combotest.selectionModel.select(0)
		combotest.onMouseClicked = EventHandler {
			combotest.hide()
			Platform.runLater {
				combotest.show()
			}
		}
	}
}