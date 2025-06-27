package org.wip.womtoolkit.view.components

import javafx.scene.control.Slider
import javafx.scene.layout.Region

class SliderWithProgressColor : Slider {
	constructor() : super() {
		initialize()
	}

	constructor(min: Double, max: Double, value: Double) : super(min, max, value) {
		initialize()
	}

	private fun initialize() {
		valueProperty().addListener { _, _, newValue ->
			val track = lookup(".track") as? Region
			track?.style = "-fx-background-color: linear-gradient(to right, -womt-accent ${newValue.toInt()}%, -womt-stroke-rest ${newValue.toInt()}%);"
		}
	}
}