package org.wip.womtoolkit.view.components.colorpicker

import javafx.event.EventHandler
import javafx.scene.paint.Color
import javafx.stage.Popup
import javafx.stage.Window

//TODO: Move the buttons here and transform this into a PopupWrapper
class ColorPickerPopup(
	color: Color,
	private val onColorSelected: (Color) -> Unit
) : Popup() {
	private val colorPickerWindow = ColorPickerWindow(color)
	var selectedColor: Color? = null
		private set

	init {
		isAutoFix = true
		isAutoHide = true
		isHideOnEscape = true
		content.add(colorPickerWindow)

		colorPickerWindow.confirmButton.onAction = EventHandler {
			selectedColor = colorPickerWindow.selectingColor
			onColorSelected(selectedColor!!)
			hide()
		}
		colorPickerWindow.cancelButton.onAction = EventHandler {
			hide()
		}
		colorPickerWindow.heightProperty().addListener { _, oldValue, newValue ->
			if (newValue != oldValue) {
				y -= (newValue.toDouble() - oldValue.toDouble())/2
			}
		}
		colorPickerWindow.widthProperty().addListener { _, oldValue, newValue ->
			if (newValue != oldValue) {
				x -= (newValue.toDouble() - oldValue.toDouble())/2
			}
		}
	}

	override fun show(owner: Window?, x: Double, y: Double) {
		super.show(owner, x, y)
	}
}