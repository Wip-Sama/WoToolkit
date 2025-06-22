package org.wip.womtoolkit.components.colorpicker

import javafx.event.EventHandler
import javafx.scene.paint.Color
import javafx.stage.Popup

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
	}

	override fun show(owner: javafx.stage.Window?, x: Double, y: Double) {
		super.show(owner, x, y)
	}
}