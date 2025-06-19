package org.wip.womtoolkit.view.settings

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.skin.ComboBoxListViewSkin
import javafx.scene.layout.VBox

/**
 * @author Wip
 * This class represents the general settings view in the application.
 * It contains: Accent color, Theme color, Language, Starting page.
 * */
class GeneralSettings : VBox() {
	@FXML lateinit var combotest: ComboBox<String>

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
		combotest.setOnMouseClicked {
			val skin = combotest.skin as? ComboBoxListViewSkin<*>
			val popup = skin?.popupContent as? ListView<*>
			if (popup != null) {
				val cellHeight = popup.fixedCellSize.takeIf { it > 0 } ?: popup.lookup(".list-cell")?.layoutBounds?.height ?: 0.0
				popup.scene.window.y = popup.scene.window.y - cellHeight * (combotest.selectionModel.selectedIndex + 1)
			}
		}
	}
}