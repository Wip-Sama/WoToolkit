package org.wip.womtoolkit.view.components

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
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
		get() = 2 * Math.PI * radius

	val sizeAnimationSpeedProperty: SimpleDoubleProperty = SimpleDoubleProperty(4.0)
	var sizeAnimationSpeed: Double
		get() = sizeAnimationSpeedProperty.value
		set(value) { sizeAnimationSpeedProperty.value = value }

	val offsetAnimationSpeedProperty: SimpleDoubleProperty = SimpleDoubleProperty(2.0)
	var offsetAnimationSpeed: Double
		get() = offsetAnimationSpeedProperty.value
		set(value) { offsetAnimationSpeedProperty.value = value }

	val continuousAnimationSpeedProperty: SimpleDoubleProperty = SimpleDoubleProperty(4.0)
	var continuousAnimationSpeed: Double
		get() = continuousAnimationSpeedProperty.value
		set(value) { continuousAnimationSpeedProperty.value = value }


	var animateSize: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
		if (newValue != oldValue) {
			if (indeterminate) {
				if (newValue)
					sizeTimeline.play()
				else {
					sizeTimeline.stop()
					updateBarSize(progress*perimeter)
				}
			}
		}
	}
	var animateOffset: Boolean by Delegates.observable(true) { _, oldValue, newValue ->
		if (newValue != oldValue) {
			if (indeterminate) {
				if (newValue)
					offsetTimeline.play()
				else {
					offsetTimeline.stop()
					barOffset = 0.0
				}
				updateBarSize(_animatedBarSize.value)
			}
		}
	}
	var animateContinuous: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
		if (newValue != oldValue) {
			if (newValue)
				continuousTimeline.play()
			else {
				continuousTimeline.stop()
				barOffset = 0.0
				updateBarSize(progress*perimeter)
			}
		}
	}

	val offsetTimeline by lazy {
		Timeline(
			KeyFrame(
				Duration.ZERO,
				KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value)
			),
			KeyFrame(
				Duration.seconds(offsetAnimationSpeed),
				KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value - perimeter)
			),
		).apply {
			cycleCount = Timeline.INDEFINITE
			radiusProperty.addListener { _, _, newValue ->
				keyFrames[1] = KeyFrame(
					Duration.seconds(offsetAnimationSpeed),
					KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value - perimeter)
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
			offsetAnimationSpeedProperty.addListener { _, _, newValue ->
				keyFrames[1] = KeyFrame(
					Duration.seconds(newValue.toDouble()),
					KeyValue(bar.strokeDashOffsetProperty(), barOffsetProperty.value - perimeter)
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
			barOffsetProperty.addListener { _, _, newValue ->
				keyFrames[0] = KeyFrame(
					Duration.ZERO,
					KeyValue(bar.strokeDashOffsetProperty(), newValue.toDouble())
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
		}
	}
	val sizeTimeline by lazy {
		Timeline(
			KeyFrame(Duration.ZERO, KeyValue(_animatedBarSize, perimeter/10)),
			KeyFrame(Duration.seconds(sizeAnimationSpeed), KeyValue(_animatedBarSize, perimeter/2)),
			KeyFrame(Duration.seconds(sizeAnimationSpeed*2), KeyValue(_animatedBarSize, perimeter/10))
		).apply {
			cycleCount = Timeline.INDEFINITE
			radiusProperty.addListener { _, _, newValue ->
				keyFrames[0] = KeyFrame(
					Duration.ZERO,
					KeyValue(_animatedBarSize, perimeter / 10)
				)
				keyFrames[1] = KeyFrame(
					Duration.seconds(sizeAnimationSpeed),
					KeyValue(_animatedBarSize, perimeter / 2)
				)
				keyFrames[2] = KeyFrame(
					Duration.seconds(sizeAnimationSpeed * 2),
					KeyValue(_animatedBarSize, perimeter / 10)
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
			sizeAnimationSpeedProperty.addListener { _, _, newValue ->
				keyFrames[1] = KeyFrame(
					Duration.seconds(newValue.toDouble()),
					KeyValue(_animatedBarSize, perimeter / 2)
				)
				keyFrames[2] = KeyFrame(
					Duration.seconds(newValue.toDouble() * 2),
					KeyValue(_animatedBarSize, perimeter / 10)
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
		}
	}
	val continuousTimeline by lazy {
		Timeline(
			KeyFrame(Duration.ZERO, KeyValue(progressProperty, 0.0)),
			KeyFrame(Duration.seconds(continuousAnimationSpeed), KeyValue(progressProperty, 1.0)),
			KeyFrame(Duration.ZERO, KeyValue(progressProperty, -1.0)),
			KeyFrame(Duration.seconds(continuousAnimationSpeed), KeyValue(progressProperty, 0.0))
		).apply {
			cycleCount = Timeline.INDEFINITE
			continuousAnimationSpeedProperty.addListener { _, _, newValue ->
				keyFrames[1] = KeyFrame(
					Duration.seconds(newValue.toDouble()),
					KeyValue(progressProperty, 1.0)
				)
				keyFrames[3] = KeyFrame(
					Duration.seconds(newValue.toDouble() * 2),
					KeyValue(progressProperty, 0.0)
				)
				(status == Animation.Status.RUNNING).let {
					if (it) {
						stop()
						play()
					}
				}
			}
		}
	}

	var radiusProperty: SimpleDoubleProperty = SimpleDoubleProperty(10.0).apply {
		addListener { _, _, newValue ->
			if (newValue.toDouble() <= 0) {
				set(1.0)
				return@addListener
			}
		}
	}
	var radius: Double
		get() = radiusProperty.value
		private set(value) { radiusProperty.value = value }

	val indeterminateProperty: SimpleBooleanProperty = SimpleBooleanProperty(false).apply {
		addListener { _, oldValue, newValue ->
			if (newValue) {
				if (!oldValue) {
					offsetTimeline.play()
					if (animateSize)
						sizeTimeline.play()
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

	val progressProperty: SimpleDoubleProperty = SimpleDoubleProperty(0.5).apply {
		addListener { _, _, newValue ->
			symmetricMod(newValue.toDouble(), 1.0).let {
				if (it != newValue.toDouble()) {
					set(0.0)
					return@addListener
				}
			}
			(newValue.toDouble()*perimeter).let { size ->
				if (size > 0) {
					barOffset = 0.0
					updateBarSize(size.absoluteValue)
				} else {
					barOffset = size.absoluteValue
					updateBarSize(size.absoluteValue)
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
			updateBarSize(newValue.toDouble())
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
		bar.radiusProperty().bind(radiusProperty)
		bar.visibleProperty().bind(progressProperty.isNotEqualTo(0.0.toLong()).or(indeterminateProperty))
//		bar.opacityProperty().bind(progressProperty.map { if (it == 0.0) 0.0 else 1.0 })
		track.radiusProperty().bind(radiusProperty)
		radiusProperty.addListener { _, _, newValue ->
			updateBarSize(
				if (indeterminate)
					_animatedBarSize.value
				else
					perimeter * progress
			)
		}
		widthProperty().addListener { _, _, newValue ->
			updateRadius()
		}
		heightProperty().addListener { _, _, newValue ->
			updateRadius()
		}
	}

	private fun updateRadius() {
		if (width <= 0 || height <= 0) {
			radius = 1.0
			return
		}
		radius = ((width-bar.strokeWidth)/2).coerceIn(1.0, (height-bar.strokeWidth)/2)
	}

	private fun updateBarSize(size: Double) {
		bar.strokeDashArray.setAll(size, perimeter - size)
	}

	private fun symmetricMod(x: Double, m: Double): Double {
		val r = x % m
		return if (r < 0) r else r
	}

	override fun computePrefWidth(height: Double): Double = (radius+(bar.strokeWidth/2)) * 2 //5 = half stroke
	override fun computePrefHeight(width: Double): Double = (radius+(bar.strokeWidth/2)) * 2
}