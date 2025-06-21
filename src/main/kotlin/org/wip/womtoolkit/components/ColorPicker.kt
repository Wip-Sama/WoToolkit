package org.wip.womtoolkit.components

import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.properties.Delegates

class ColorPicker: BorderPane() {
	@FXML lateinit var pngDisplay: ImageView
	@FXML lateinit var brightnessSlider: Slider

	@FXML lateinit var baseColorSlider: Slider
	@FXML lateinit var canvasDisplay: Canvas

	@FXML lateinit var interactableCanvas: Canvas
	@FXML lateinit var alphaSlider: Slider

	@FXML lateinit var oldColorPane: Pane
	@FXML lateinit var newColorPane: Pane

	private val selectedColorProperty = SimpleObjectProperty<Color>()
	private val selectingColorProperty = SimpleObjectProperty<Color>()

	private val lastMousePositionProperty = SimpleObjectProperty<Point2D>(Point2D(0.0, 0.0))

	var isHueSelector: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
			baseColorSlider.isVisible = new
			baseColorSlider.isManaged = new
			canvasDisplay.isVisible = new
			canvasDisplay.isManaged = new

			pngDisplay.isVisible = !new
			pngDisplay.isManaged = !new
			brightnessSlider.isVisible = !new
			brightnessSlider.isManaged = !new
		}
	}

	var isAlphaAvailable: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
			alphaSlider.isVisible = new
			alphaSlider.isManaged = new
		}
	}

	init {
		FXMLLoader(javaClass.getResource("/components/colorPicker.fxml")).apply {
			setRoot(this@ColorPicker)
			setController(this@ColorPicker)
			load()
		}
	}

	@FXML
	fun initialize() {
		pngDisplay.image = javaClass.getResourceAsStream("/images/color_wheel.png")?.let { Image(it) }

		// Update the display canvas when the base color changes
		baseColorSlider.valueProperty().addListener { observable, oldValue, newValue ->
			val gc = canvasDisplay.graphicsContext2D
			gc.fill = intToColor(newValue.toInt())
			gc.fillRect(0.0, 0.0, canvasDisplay.width, canvasDisplay.height)
			val verticalGradient = LinearGradient(
				0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
				listOf(Stop(0.0, Color.TRANSPARENT), Stop(1.0, Color.BLACK))
			)
			val horizontalGradient = LinearGradient(
				0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
				listOf(Stop(0.0, Color.WHITE), Stop(1.0, Color.TRANSPARENT))
			)
			gc.fill = horizontalGradient
			gc.fillRect(0.0, 0.0, canvasDisplay.width, canvasDisplay.height)
			gc.fill = verticalGradient
			gc.fillRect(0.0, 0.0, canvasDisplay.width, canvasDisplay.height)
			getColor()
		}

		alphaSlider.valueProperty().addListener { observable, oldValue, newValue ->
			getColor()
		}

		brightnessSlider.valueProperty().addListener { observable, oldValue, newValue ->
			getColor()
		}

		val storePoint = EventHandler<MouseEvent> { event ->
			lastMousePositionProperty.value = Point2D(event.x, event.y)
			getColor()
		}
		interactableCanvas.onMouseClicked = storePoint
		interactableCanvas.onMouseDragged = storePoint

		selectedColorProperty.addListener { _, _, newColor ->
			if (newColor != null) {
				oldColorPane.background = Background(BackgroundFill(newColor, null, null))
			} else {
				oldColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, null, null))
			}
		}
		selectingColorProperty.addListener { _, _, newColor ->
			if (newColor != null) {
				newColorPane.background = Background(BackgroundFill(newColor, null, null))
			} else {
				newColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, null, null))
			}
		}

		if (isHueSelector) {
			baseColorSlider.isVisible = true
			baseColorSlider.isManaged = true
			canvasDisplay.isVisible = true
			canvasDisplay.isManaged = true

			pngDisplay.isVisible = false
			pngDisplay.isManaged = false
			brightnessSlider.isVisible = false
			brightnessSlider.isManaged = false
		} else {
			baseColorSlider.isVisible = false
			baseColorSlider.isManaged = false
			canvasDisplay.isVisible = false
			canvasDisplay.isManaged = false

			pngDisplay.isVisible = true
			pngDisplay.isManaged = true
			brightnessSlider.isVisible = true
			brightnessSlider.isManaged = true
		}

		if (isAlphaAvailable) {
			alphaSlider.isVisible = true
			alphaSlider.isManaged = true
		} else {
			alphaSlider.isVisible = false
			alphaSlider.isManaged = false
		}
	}

	private fun getColor() {
		if (isHueSelector) {
			getColorFromHue()
		} else {
			getColorFromPng()
		}
	}

	private fun getColorFromPng() {
		var x = lastMousePositionProperty.value.x
		var y = lastMousePositionProperty.value.y
		val middleX = interactableCanvas.width / 2
		val middleY = interactableCanvas.height / 2

		var color = pngDisplay.image.pixelReader.getColor(
			clamp((x / 255 * pngDisplay.image.width), 0.0, pngDisplay.image.width-1).toInt(),
			clamp((y / 255 * pngDisplay.image.height), 0.0, pngDisplay.image.height-1).toInt(),
		)

		val angle = atan2(middleY - y, middleX - x)
		while (color.opacity == 0.0) {
			x += cos(angle)
			y += sin(angle)
			color = pngDisplay.image.pixelReader.getColor(
				clamp((x / interactableCanvas.width * pngDisplay.image.width), 0.0, pngDisplay.image.width-1).toInt(),
				clamp((y / interactableCanvas.width * pngDisplay.image.height), 0.0, pngDisplay.image.height-1).toInt(),
			)
		}

		val hsb = RGBtoHSB((color.red*255).toInt(), (color.green*255).toInt(), (color.blue*255).toInt(), null)
		val awtColor = java.awt.Color(HSBtoRGB(hsb[0], hsb[1], (brightnessSlider.value/100).toFloat()))
		selectingColorProperty.value = Color(awtColor.red / 255.0, awtColor.green / 255.0, awtColor.blue / 255.0, alphaSlider.value/100)
		drawSelectedColor(x, y, selectingColorProperty.value)
	}

	private fun getColorFromHue() {
		val x = clamp(lastMousePositionProperty.value.x, 0.0, canvasDisplay.width)
		val y = clamp(lastMousePositionProperty.value.y, 0.0, canvasDisplay.height)

		val baseColor = intToColor(baseColorSlider.value.toInt())
		val saturation = 1 - (x / canvasDisplay.width)
		val brightness = 1 - (y / canvasDisplay.height)

		val r = ((1 - saturation) * baseColor.red + saturation ) * brightness
		val g = ((1 - saturation) * baseColor.green + saturation ) * brightness
		val b = ((1 - saturation) * baseColor.blue + saturation ) * brightness
		val a = alphaSlider.value / 100

		selectingColorProperty.value = Color(r,g,b,a)
		drawSelectedColor(x, y, selectingColorProperty.value)
	}

	private fun drawSelectedColor(x: Double, y: Double, color: Color, size: Double = 5.0) {
		var x = clamp(x, 0.0, interactableCanvas.width - 1)
		var y = clamp(y, 0.0, interactableCanvas.height - 1)
		val gc = interactableCanvas.graphicsContext2D
		gc.clearRect(0.0, 0.0, interactableCanvas.width, interactableCanvas.height)
		gc.fill = Color.BLACK
		gc.strokeOval(x-size, y-size, size*2, size*2)
		gc.fill = color
		gc.fillOval(x-(size-1), y-(size-1), (size-1)*2, (size-1)*2)
	}

	private fun intToColor(base: Int): Color {
		val hue = (base.toDouble() / 65535.0) * 360.0
		val saturation = 1.0
		val brightness = 1.0
		return Color.hsb(hue, saturation, brightness)
	}

	private fun colorToBaseColor(color: Color): Color {
		val awtColor = java.awt.Color(HSBtoRGB(RGBtoHSB((color.red*255).toInt(), (color.green*255).toInt(), (color.blue*255).toInt(), null)[0], 1.0F, 1.0F))
		return Color(
			awtColor.red / 255.0,
			awtColor.green / 255.0,
			awtColor.blue / 255.0,
			1.0
		)
	}

	private fun clamp(value: Double, min: Double, max: Double): Double {
		return when {
			value < min -> min
			value > max -> max
			else -> value
		}
	}
}