package org.wip.womtoolkit.view.components.colorpicker

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import org.wip.womtoolkit.model.Lsp
import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class ColorPickerWindow() : BorderPane() {
	@FXML lateinit var pngDisplay: ImageView
	@FXML lateinit var brightnessSlider: Slider

	@FXML lateinit var hueColorSlider: Slider
	@FXML lateinit var canvasDisplay: Canvas

	@FXML lateinit var interactableCanvas: Canvas
	@FXML lateinit var alphaSlider: Slider

	@FXML lateinit var oldColorPane: Pane
	@FXML lateinit var newColorPane: Pane
	@FXML lateinit var displayContainer: AnchorPane

	@FXML lateinit var hueColorTooltip: Tooltip
	@FXML lateinit var brightnessTooltip: Tooltip
	@FXML lateinit var alphaTooltip: Tooltip

	@FXML lateinit var cancelButton: Button
	@FXML lateinit var confirmButton: Button

	private val selectedColorProperty = SimpleObjectProperty<Color>(Color.RED)
	private val selectingColorProperty = SimpleObjectProperty<Color>()

	val selectingColor: Color
		get() = selectingColorProperty.value ?: Color.BLACK

	val selectedColor: Color
		get() = selectedColorProperty.value ?: Color.TRANSPARENT

	private val lastMousePositionProperty = SimpleObjectProperty<Point2D?>()

	var isHueSelector: Boolean by Delegates.observable(true) { _, old, new ->
		if (new != old) {
			hueColorSlider.isVisible = new
			hueColorSlider.isManaged = new
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
	var isAdvancedMode: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
		}
	}
	var showRGB: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
		}
	}
	var showHSB: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
		}
	}
	var showHex: Boolean by Delegates.observable(false) { _, old, new ->
		if (new != old) {
		}
	}

	constructor(color: Color) : this() {
		selectedColorProperty.value = color
	}

	init {
		FXMLLoader(javaClass.getResource("/view/components/colorPickerWindow.fxml")).apply {
			setRoot(this@ColorPickerWindow)
			setController(this@ColorPickerWindow)
			load()
		}
	}

	@FXML
	fun initialize() {
		val rectClip = Rectangle().apply {
			arcHeight = 18.0
			arcWidth = 18.0
		}
		displayContainer.clip = rectClip
		displayContainer.layoutBoundsProperty().addListener { _, _, bounds ->
			rectClip.width = bounds.width
			rectClip.height = bounds.height
		}

		hueColorTooltip.textProperty().bind(Lsp.lsb("colorPicker.hue.tooltip"))
		brightnessTooltip.textProperty().bind(Lsp.lsb("colorPicker.brightness.tooltip"))
		alphaTooltip.textProperty().bind(Lsp.lsb("colorPicker.alpha.tooltip"))

		pngDisplay.image = javaClass.getResourceAsStream("/images/color_wheel.png")?.let { Image(it) }

		// Update the display canvas when the base color changes
		hueColorSlider.valueProperty().addListener { observable, oldValue, newValue ->
			updateHue()
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
			val radii = CornerRadii(0.0, 0.0, 8.0, 8.0, false)
			if (newColor != null) {
				oldColorPane.background = Background(BackgroundFill(newColor, radii, null))
			} else {
				oldColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, radii, null))
			}
		}
		selectingColorProperty.addListener { _, _, newColor ->
			val radii = CornerRadii(8.0, 8.0, 0.0, 0.0, false)
			if (newColor != null) {
				newColorPane.background = Background(BackgroundFill(newColor, radii, null))
			} else {
				newColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, radii, null))
			}
		}

		if (isHueSelector) {
			hueColorSlider.isVisible = true
			hueColorSlider.isManaged = true
			canvasDisplay.isVisible = true
			canvasDisplay.isManaged = true

			pngDisplay.isVisible = false
			pngDisplay.isManaged = false
			brightnessSlider.isVisible = false
			brightnessSlider.isManaged = false

			hueColorSlider.value = colorToInt(getBaseColor(selectedColorProperty.value)).toDouble()
			updateHue()
			//TODO: the cursor is still not showing
			lastMousePositionProperty.value = getCoordinateFromColor(selectedColor)
		} else {
			hueColorSlider.isVisible = false
			hueColorSlider.isManaged = false
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

		confirmButton.onAction = EventHandler {
			selectedColorProperty.value = selectingColorProperty.value ?: Color.BLACK
			Platform.runLater {
				updateColorPreview()
			}
		}

		cancelButton.onAction = EventHandler {
			selectingColorProperty.value = null
			lastMousePositionProperty.value = null
			Platform.runLater {
				updateColorPreview()
			}
		}

		Platform.runLater {
			updateColorPreview()
		}
	}

	private fun updateColorPreview() {
		val downCorners = CornerRadii(0.0, 0.0, 8.0, 8.0, false)
		if (selectedColorProperty.value != null) {
			oldColorPane.background = Background(BackgroundFill(selectedColorProperty.value, downCorners, null))
		} else {
			oldColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, downCorners, null))
		}
		val upCorners = CornerRadii(8.0, 8.0, 0.0, 0.0, false)
		if (selectingColorProperty.value != null) {
			newColorPane.background = Background(BackgroundFill(selectingColorProperty.value, upCorners, null))
		} else {
			newColorPane.background = Background(BackgroundFill(Color.TRANSPARENT, upCorners, null))
		}
	}

	private fun updateHue() {
		val gc = canvasDisplay.graphicsContext2D

		//Base color
		gc.fill = intToColor(hueColorSlider.value.toInt())
		gc.fillRect(0.0, 0.0, canvasDisplay.width, canvasDisplay.height)

		//Brightness
		val verticalGradient = LinearGradient(
			0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
			listOf(Stop(0.0, Color.TRANSPARENT), Stop(1.0, Color.BLACK))
		)

		//Saturation
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

	private fun getColor() {
		if (isHueSelector) {
			getColorFromHue()
		} else {
			getColorFromPng()
		}
	}

	private fun getColorFromPng() {
		if (lastMousePositionProperty.value == null) return
		var x = lastMousePositionProperty.value!!.x
		var y = lastMousePositionProperty.value!!.y
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

	private fun getCoordinateFromColor(color: Color): Point2D {
		val baseColor = intToColor(hueColorSlider.value.toInt())
		val saturation = 1 - (color.red - baseColor.red) / (1 - baseColor.red)
		val brightness = 1 - (color.green - baseColor.green) / (1 - baseColor.green)

		val x = saturation * canvasDisplay.width
		val y = brightness * canvasDisplay.height

		return Point2D(x, y)
	}

	private fun getColorFromHue() {
		if (lastMousePositionProperty.value == null) return
		val x = clamp(lastMousePositionProperty.value!!.x, 0.0, canvasDisplay.width)
		val y = clamp(lastMousePositionProperty.value!!.y, 0.0, canvasDisplay.height)

		val baseColor = intToColor(hueColorSlider.value.toInt())
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

	private fun colorToInt(color: Color): Int {
		val hue = color.hue / 360.0 * 65535.0
		return hue.toInt()
	}

	private fun getBaseColor(color: Color): Color {
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