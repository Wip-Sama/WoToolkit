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
			track?.style = "-fx-background-color: linear-gradient(to right, -womt-accent ${((newValue.toDouble()-min)/(max-min)*100).toInt()}%, -womt-stroke-rest ${((newValue.toDouble()-min)/(max-min)*100).toInt()}%);"
		}
	}
}