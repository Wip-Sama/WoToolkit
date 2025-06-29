package org.wip.womtoolkit.view.components.colorpicker

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.css.PseudoClass
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import kotlin.properties.Delegates

class ColorPickerButton() : AnchorPane() {

	val colorProperty = SimpleObjectProperty<Color>(Color.BLACK)

	var isColorPickerAvailable by Delegates.observable(false) { _, oldValue, newValue -> }
	var isSelectable by Delegates.observable(false) { _, oldValue, newValue -> }
	var isSelectedProperty = SimpleBooleanProperty(false)

	val onActionProperty: ObjectProperty<EventHandler<ActionEvent>> = SimpleObjectProperty()
	var onAction: EventHandler<ActionEvent>?
		get() = onActionProperty.get()
		set(value) = onActionProperty.set(value)

	constructor(color: Color) : this() {
		colorProperty.set(color)
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

		isSelectedProperty.addListener { _, oldValue, isSelected ->
			if (oldValue != isSelected) {
				pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), isSelected)
			}
		}

		onMouseClicked = EventHandler {
			if (isSelectable) {
				isSelectedProperty.value = !isSelectedProperty.value
			} else if (isColorPickerAvailable) {
				val cpp = ColorPickerPopup(colorProperty.value) { newColor ->
					colorProperty.value = newColor
				}
				val scene = scene
				val window = scene?.window
				if (window != null) {
					cpp.setOnShown {
						val centerX = window.x + window.width / 2 - cpp.width / 2
						val centerY = window.y + window.height / 2 - cpp.height / 2
						cpp.x = centerX
						cpp.y = centerY
					}
					cpp.show(this, window.x, window.y)
				}
			}
			onAction?.handle(ActionEvent(this, null))
		}
	}


	fun updateColor(color: Color = colorProperty.value) {
		style = "-fx-background-color: #${colorProperty.value.toString().removePrefix("0x")};"
				"-fx-border-color: #${colorProperty.value.toString().removePrefix("0x")};"
	}
}