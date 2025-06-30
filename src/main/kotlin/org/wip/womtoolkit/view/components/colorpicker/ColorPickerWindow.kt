package org.wip.womtoolkit.view.components.colorpicker

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
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
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.model.Lsp
import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates
/*
* TODO: - Rework components hiding
*  - Lock TextField to accept only number in theirs range
*  - Bind text fields vale / sliders / interactable canvas to the selecting color and update them on change
*  - SVGPAth are the wrong color
*  */

class ColorPickerWindow() : BorderPane() {
	object Constants {
		const val COLLAPSED: String =
			"M4.29289 8.29289C4.68342 7.90237 5.31658 7.90237 5.70711 8.29289L12 14.5858L18.2929 8.29289C18.6834 7.90237 19.3166 7.90237 19.7071 8.29289C20.0976 8.68342 20.0976 9.31658 19.7071 9.70711L12.7071 16.7071C12.3166 17.0976 11.6834 17.0976 11.2929 16.7071L4.29289 9.70711C3.90237 9.31658 3.90237 8.68342 4.29289 8.29289Z"
		const val EXPANDED: String =
			"M4.29289 15.7071C4.68342 16.0976 5.31658 16.0976 5.70711 15.7071L12 9.41421L18.2929 15.7071C18.6834 16.0976 19.3166 16.0976 19.7071 15.7071C20.0976 15.3166 20.0976 14.6834 19.7071 14.2929L12.7071 7.29289C12.3166 6.90237 11.6834 6.90237 11.2929 7.29289L4.29289 14.2929C3.90237 14.6834 3.90237 15.3166 4.29289 15.7071Z"
	}

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

	@FXML lateinit var colorPickerTitle: Label
	@FXML lateinit var advancedModeToggle: ToggleButton
	@FXML lateinit var advancedModeIndicator: SVGPath
	@FXML lateinit var advancedElementsContainer: VBox
	@FXML lateinit var modeSelector: ChoiceBox<String>

	@FXML lateinit var hexValue: TextField
	@FXML lateinit var firstValue: TextField
	@FXML lateinit var firstLabel: Label
	@FXML lateinit var secondValue: TextField
	@FXML lateinit var thirdLabel: Label
	@FXML lateinit var thirdValue: TextField
	@FXML lateinit var secondLabel: Label
	@FXML lateinit var alphaValue: TextField
	@FXML lateinit var alphaLabel: Label

	private val selectedColorProperty = SimpleObjectProperty<Color>(Color.RED)
	val selectedColor: Color
		get() = selectedColorProperty.value ?: Color.TRANSPARENT

	private val selectingColorProperty = SimpleObjectProperty<Color>()
	val selectingColor: Color
		get() = selectingColorProperty.value ?: Color.BLACK

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
		/* Borders for the canvas */
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

		if (isHueSelector) {
			hueColorSlider.valueProperty().addListener { _, _, _ -> updateBaseHue() }
			hueColorSlider.value = colorToInt(getBaseColor(selectedColorProperty.value)).toDouble()
			updateBaseHue()
			lastMousePositionProperty.value = getCoordinateFromColor(selectedColor)
		} else {
			pngDisplay.image = javaClass.getResourceAsStream("/images/color_wheel.png")?.let { Image(it) }
			brightnessSlider.valueProperty().addListener { _, _, _ -> getColor() }
		}

		if (isAlphaAvailable) {
			alphaSlider.valueProperty().addListener { _, _, _ -> getColor() }
		}

		modeSelector.onMouseClicked = EventHandler {
			modeSelector.hide()
			Platform.runLater {
				modeSelector.show()
			}
		}

		fun localizeModeSelector() {
			val index = modeSelector.selectionModel.selectedIndex
			modeSelector.items.clear()
			modeSelector.items.addAll(
				Lsp.lsb("colorPicker.advancedMode.rgb").value,
				Lsp.lsb("colorPicker.advancedMode.hue").value
			)
			modeSelector.selectionModel.select(index)
		}

		localizeModeSelector()
		modeSelector.value = Lsp.lsb("colorPicker.advancedMode.rgb").value

		LocalizationService.currentLocaleProperty.addListener { _, _, _ ->
			localizeModeSelector()
		}

		selectingColorProperty.addListener { _, _, _ ->
			// Update hex value
			hexValue.text = if (selectingColorProperty.value != null) {
				String.format("#%02X%02X%02X%02X",
					(selectingColorProperty.value!!.red * 255).toInt(),
					(selectingColorProperty.value!!.green * 255).toInt(),
					(selectingColorProperty.value!!.blue * 255).toInt(),
					(selectingColorProperty.value!!.opacity * 255).toInt()
				)
			} else {
				""
			}

			// Update RGB values
			if (modeSelector.selectionModel.selectedIndex == 0) {
				firstValue.text = ((selectingColorProperty.value?.red ?: 0.0) * 255).toInt().toString()
				secondValue.text = ((selectingColorProperty.value?.green ?: 0.0) * 255).toInt().toString()
				thirdValue.text = ((selectingColorProperty.value?.blue ?: 0.0) * 255).toInt().toString()
				firstLabel.text = Lsp.lsb("colorPicker.rgb.red").value
				secondLabel.text = Lsp.lsb("colorPicker.rgb.green").value
				thirdLabel.text = Lsp.lsb("colorPicker.rgb.blue").value
			} else {
				val hsb = RGBtoHSB(
					((selectingColorProperty.value?.red ?: 0.0) * 255).toInt(),
					((selectingColorProperty.value?.green ?: 0.0) * 255).toInt(),
					((selectingColorProperty.value?.blue ?: 0.0) * 255).toInt(),
					null
				)
				firstValue.text = hsb[0].toString()
				secondValue.text = (hsb[1] * 100).toInt().toString()
				thirdValue.text = (hsb[2] * 100).toInt().toString()
				firstLabel.text = Lsp.lsb("colorPicker.hsb.hue").value
				secondLabel.text = Lsp.lsb("colorPicker.hsb.saturation").value
				thirdLabel.text = Lsp.lsb("colorPicker.hsb.brightness").value
			}
			alphaValue.text = ((selectingColorProperty.value?.opacity ?: 1.0) * 100).toInt().toString()
			alphaLabel.text = Lsp.lsb("colorPicker.alpha").value

			drawSelectedColor(
				lastMousePositionProperty.value?.x ?: 0.0,
				lastMousePositionProperty.value?.y ?: 0.0,
				selectingColorProperty.value ?: Color.BLACK
			)
		}

		fun updateSelectingColorFromTextFields() {
			var newColor: Color
			if (modeSelector.selectionModel.selectedIndex == 0) {
				// RGB mode
				val red = firstValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
				val green = secondValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
				val blue = thirdValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
				val alpha = alphaValue.text.toDoubleOrNull()?.coerceIn(0.0, 100.0)?.div(100) ?: 1.0

				newColor = Color(red / 255.0, green / 255.0, blue / 255.0, alpha)
			} else {
				// HSB mode
				val hue = firstValue.text.toIntOrNull()?.coerceIn(0, 360) ?: 0
				val saturation = secondValue.text.toIntOrNull()?.coerceIn(0, 100)?.div(100.0) ?: 1.0
				val brightness = thirdValue.text.toIntOrNull()?.coerceIn(0, 100)?.div(100.0) ?: 1.0
				val alpha = alphaValue.text.toDoubleOrNull()?.coerceIn(0.0, 100.0)?.div(100) ?: 1.0

				newColor = Color.hsb(hue.toDouble(), saturation, brightness, alpha)
			}

			if (selectingColorProperty.value != newColor)
				selectingColorProperty.value = newColor
		}

		hexValue.textProperty().addListener { _, _, newValue -> }
		firstValue.textProperty().addListener { _, _, newValue -> updateSelectingColorFromTextFields() }
		secondValue.textProperty().addListener { _, _, newValue -> updateSelectingColorFromTextFields() }
		thirdValue.textProperty().addListener { _, _, newValue -> updateSelectingColorFromTextFields() }
		alphaValue.textProperty().addListener { _, _, newValue -> updateSelectingColorFromTextFields() }

		/* Save the mouse position when the user interact with the canvas and get the color */
		val storePoint = EventHandler<MouseEvent> { event ->
			lastMousePositionProperty.value = Point2D(event.x, event.y)
			getColor()
		}
		interactableCanvas.onMouseClicked = storePoint
		interactableCanvas.onMouseDragged = storePoint


		/* Auto update color preview */
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


		/* Show advanced settings */
		advancedModeToggle.selectedProperty().addListener { observable, oldValue, newValue ->
			if (newValue) {
				advancedModeIndicator.content = Constants.EXPANDED
			} else {
				advancedModeIndicator.content = Constants.COLLAPSED
			}
		}

		advancedElementsContainer.visibleProperty().bind(advancedModeToggle.selectedProperty())
		advancedElementsContainer.managedProperty().bind(advancedModeToggle.selectedProperty())


		/* Manage components visibility */
		hueColorSlider.isVisible = isHueSelector
		hueColorSlider.isManaged = isHueSelector
		canvasDisplay.isVisible = isHueSelector
		canvasDisplay.isManaged = isHueSelector

		pngDisplay.isVisible = !isHueSelector
		pngDisplay.isManaged = !isHueSelector
		brightnessSlider.isVisible = !isHueSelector
		brightnessSlider.isManaged = !isHueSelector

		alphaSlider.isVisible = isAlphaAvailable
		alphaSlider.isManaged = isAlphaAvailable


		/* Interactions */
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

	private fun updateBaseHue() {
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

	private fun getCoordinateFromColor(color: Color): Point2D {
		val baseColor = intToColor(hueColorSlider.value.toInt())
		val saturation = 1 - (color.red - baseColor.red) / (1 - baseColor.red)
		val brightness = 1 - (color.green - baseColor.green) / (1 - baseColor.green)

		val x = saturation * canvasDisplay.width
		val y = brightness * canvasDisplay.height

		return Point2D(x, y)
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