package org.wip.womtoolkit.view.components.colorpicker

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import kotlin.properties.Delegates

class ColorPickerButton() : AnchorPane() {

	val colorProperty = SimpleObjectProperty<Color>(Color.BLACK)

	var isColorPickerAvailable by Delegates.observable(false) { _, oldValue, newValue -> }

	constructor(color: Color) : this() {
		colorProperty.set(color)
	}

	constructor(isColorPickerAvailable: Boolean) : this() {
		this.isColorPickerAvailable = isColorPickerAvailable
	}

	init {
		FXMLLoader(javaClass.getResource("/view/components/colorPickerButton.fxml")).apply {
			setRoot(this@ColorPickerButton)
			setController(this@ColorPickerButton)
			load()
		}
	}

	@FXML
	fun initialize() {
		colorProperty.addListener { _, _, color ->
			updateColor(color)
		}
		visibleProperty().addListener { _, _, visible ->
			updateColor()
		}

		onMouseClicked = EventHandler {
			if (isColorPickerAvailable) {
				val cpp = ColorPickerPopup(colorProperty.value) { newColor ->
					colorProperty.value = newColor
				}
				cpp.show(this, layoutX + width / 2, layoutY + height / 2)
			}
		}
	}


	fun updateColor(color: Color = colorProperty.value) {
		style = "-fx-background-color: #${colorProperty.value.toString().removePrefix("0x")};"
				"-fx-border-color: #${colorProperty.value.toString().removePrefix("0x")};"
	}
}