package org.wip.womtoolkit.view.components

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import javafx.util.Duration
import kotlin.math.absoluteValue
import kotlin.properties.Delegates

class CircularProgressBar: StackPane() {
	@FXML lateinit var track: Circle
	@FXML lateinit var bar: Circle
	@FXML lateinit var label: Label

	val perimeter: Double
		get() = if (::bar.isInitialized) 2 * Math.PI * bar.radius else 0.0

	var animateSize: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
		if (newValue != oldValue) {
			if (indeterminate) {
				if (newValue) sizeTimeline.play() else sizeTimeline.stop()
			}
		}
	}

	val indeterminateProperty: SimpleBooleanProperty = SimpleBooleanProperty(false).apply {
		addListener { _, oldValue, newValue ->
			if (newValue) {
				if (!oldValue) {
					offsetTimeline.play()
					if (animateSize) sizeTimeline.play()
				}
			} else {
				if (oldValue) {
					offsetTimeline.stop()
					sizeTimeline.stop()
					bar.strokeDashOffset = barOffsetProperty.value
					_animatedBarSize.value = perimeter * progressProperty.value
					barOffset = 0.0
				}
			}
		}
	}
	var indeterminate: Boolean
		get() = indeterminateProperty.value
		set(value) { indeterminateProperty.value = value }

	val progressProperty: SimpleDoubleProperty = SimpleDoubleProperty(0.0).apply {
		addListener { _, _, newValue ->
			symmetricMod(newValue.toDouble(), 1.0).let {
				if (it != newValue.toDouble()) {
					set(0.0)
					return@addListener
				}
			}
			(newValue.toDouble()*perimeter).let { size ->
				if (size > 0) {
					bar.strokeDashArray.setAll(size, perimeter - size)
					barOffset = 0.0
				} else {
					barOffset = size.absoluteValue
					bar.strokeDashArray.setAll(size.absoluteValue, perimeter - size.absoluteValue)
				}
			}
		}
	}
	var progress: Double
		get() = progressProperty.value
		set(value) { progressProperty.value = value }

	val barOffsetProperty: SimpleDoubleProperty = SimpleDoubleProperty(0.0).apply {
		addListener { _, _, newValue ->
			newValue.toDouble().mod(perimeter).let {
				if (it != newValue.toDouble()) {
					set(it)
					return@addListener
				}
			}
			bar.strokeDashOffset = newValue.toDouble()
		}
	}
	private var barOffset: Double
		get() = barOffsetProperty.value
		set(value) { barOffsetProperty.value = value }

	private val _animatedBarSize: SimpleDoubleProperty = SimpleDoubleProperty(perimeter/10).apply {
		addListener { _, _, newValue ->
			newValue.toDouble().coerceIn(0.0, perimeter).let {
				if (it != newValue.toDouble()) {
					set(it)
					return@addListener
				}
			}
			bar.strokeDashArray.setAll(newValue.toDouble(), perimeter - newValue.toDouble())
		}
	}

	val offsetTimeline by lazy {
		Timeline(
			KeyFrame(
				Duration.ZERO,
				KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value - 0.0)
			),
			KeyFrame(
				Duration.millis(1000.0),
				KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value - perimeter)
			),
		).apply {
			cycleCount = Timeline.INDEFINITE
		}
	}
	val sizeTimeline by lazy {
		Timeline(
			KeyFrame(Duration.ZERO, KeyValue(_animatedBarSize, perimeter/10)),
			KeyFrame(Duration.seconds(1.0), KeyValue(_animatedBarSize, perimeter/2)),
			KeyFrame(Duration.seconds(2.0), KeyValue(_animatedBarSize, perimeter/10))
		).apply {
			cycleCount = Timeline.INDEFINITE
			onFinished = EventHandler {

			}
		}
	}

	init {
		FXMLLoader(CircularProgressBar::class.java.getResource("/view/components/circularProgressBar.fxml")).apply {
			setRoot(this@CircularProgressBar)
			setController(this@CircularProgressBar)
			load()
		}
	}

	@FXML
	fun initialize() {
		bar.strokeDashOffset = barOffsetProperty.value
		updateBarSize(
			if (indeterminate)
				_animatedBarSize.value
			else
				perimeter * progress
		)
	}

	private fun updateBarSize(size: Double) {
		bar.strokeDashArray.setAll(size, perimeter - size)
	}

	private fun symmetricMod(x: Double, m: Double): Double {
		val r = x % m
		return if (r < 0) r else r
	}
}