package org.wip.womtoolkit.view.components.colorpicker

import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.util.StringConverter
import org.wip.womtoolkit.model.ApplicationData
import org.wip.womtoolkit.model.services.localization.LocalizationService
import org.wip.womtoolkit.model.services.localization.Lsp
import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import java.util.function.UnaryOperator
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/*
* TODO: - Rework components hiding
*  - Lock TextField to accept only number in theirs range
*  - Bind text fields vale / sliders / interactable canvas to the selecting color and update them on change
*  - SVGPAth are the wrong color
*  */

class ColorPickerWindow() : BorderPane() {
	companion object {
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

	private var isUpdatingFields = false

	private val selectedColorProperty = SimpleObjectProperty<Color>(Color.RED)
	val selectedColor: Color
		get() = selectedColorProperty.value ?: Color.TRANSPARENT

	private val selectingColorProperty = SimpleObjectProperty<Color>()
	val selectingColor: Color
		get() = selectingColorProperty.value ?: Color.BLACK

	private val lastMousePositionProperty = SimpleObjectProperty<Point2D?>(null)

	var isHueSelectorProperty: BooleanProperty = SimpleBooleanProperty(ApplicationData.userSettings.colorPickerSettings.selectorMode.value)
	var isAlphaAvailableProperty: BooleanProperty = SimpleBooleanProperty(ApplicationData.userSettings.colorPickerSettings.alphaAvailable.value)

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

		/* Sliders */
		initializeSliders()
		initializeColorPreview()
		initializeCanvases()
		initializeAdvancedMode()

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

	private fun initializeSliders() {
		/* Visibility */
		hueColorSlider.visibleProperty().bind(isHueSelectorProperty)
		hueColorSlider.managedProperty().bind(isHueSelectorProperty)
		brightnessSlider.visibleProperty().bind(isHueSelectorProperty.not())
		brightnessSlider.managedProperty().bind(isHueSelectorProperty.not())
		alphaSlider.visibleProperty().bind(isAlphaAvailableProperty)
		alphaSlider.managedProperty().bind(isAlphaAvailableProperty)
		hueColorTooltip.textProperty().bind(Lsp.lsb("colorPicker.hue.tooltip"))
		brightnessTooltip.textProperty().bind(Lsp.lsb("colorPicker.brightness.tooltip"))
		alphaTooltip.textProperty().bind(Lsp.lsb("colorPicker.alpha.tooltip"))

		/* Auto update */
		brightnessSlider.valueProperty().addListener { _, _, _ ->
			val newColor = getColorFromPngOrHue(customBrightness = brightnessSlider.value)
			if (newColor != null && newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		alphaSlider.valueProperty().addListener { _, _, _ ->
			val newColor = getColorFromPngOrHue()
			if (newColor != null && newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		hueColorSlider.valueProperty().addListener { _, _, _ ->
			updateBaseHue()
			val newColor = getColorFromHue()
			if (newColor != null && newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}

		/* Initialization */
		updateBaseHue()
		hueColorSlider.value = colorToInt(getBaseColor(selectedColorProperty.value)).toDouble()
		lastMousePositionProperty.value = getCoordinateFromColor(selectedColor)
	}

	private fun initializeColorPreview() {
		/* Initialization */
		oldColorPane.background = Background(BackgroundFill(selectedColorProperty.value, CornerRadii(0.0, 0.0, 8.0, 8.0, false), null))
		newColorPane.background = Background(BackgroundFill(selectingColorProperty.value, CornerRadii(8.0, 8.0, 0.0, 0.0, false), null))

		/* Auto update */
		selectingColorProperty.addListener { _, _, newValue ->
			newColorPane.background = Background(BackgroundFill(newValue, CornerRadii(8.0, 8.0, 0.0, 0.0, false), null))
		}
	}

	private fun initializeCanvases() {
		/* Initialization */
		pngDisplay.image = javaClass.getResourceAsStream("/images/color_wheel.png")?.let { Image(it) }

		/* Visibility */
		canvasDisplay.visibleProperty().bind(isHueSelectorProperty)
		canvasDisplay.managedProperty().bind(isHueSelectorProperty)
		pngDisplay.visibleProperty().bind(isHueSelectorProperty.not())
		pngDisplay.managedProperty().bind(isHueSelectorProperty.not())

		/* Mouse interactions */
		val storePoint = EventHandler<MouseEvent> { event ->
			lastMousePositionProperty.value = Point2D(event.x, event.y)
		}
		interactableCanvas.onMouseClicked = storePoint
		interactableCanvas.onMouseDragged = storePoint
		lastMousePositionProperty.addListener { _, oldValue, newValue ->
			if (newValue != null && oldValue != newValue) {
				val color = getColorFromPngOrHue()
				if (color != null && color != selectingColorProperty.value) {
					selectingColorProperty.value = color
					drawSelectedColor(newValue.x, newValue.y, color)
				}
			}
		}
		selectingColorProperty.addListener { _, _, newValue ->
			drawSelectedColor(lastMousePositionProperty.value?.x ?: 0.0, lastMousePositionProperty.value?.y ?: 0.0, newValue)
		}
	}

	private fun initializeAdvancedMode() {
		/* Visibility */
		advancedElementsContainer.visibleProperty().bind(advancedModeToggle.selectedProperty())
		advancedElementsContainer.managedProperty().bind(advancedModeToggle.selectedProperty())
		alphaValue.visibleProperty().bind(isAlphaAvailableProperty)
		alphaLabel.visibleProperty().bind(isAlphaAvailableProperty)
		alphaValue.managedProperty().bind(isAlphaAvailableProperty)
		alphaLabel.managedProperty().bind(isAlphaAvailableProperty)

		/* Color updater and text limiter */
		val limit0_360 = UnaryOperator { change: TextFormatter.Change? ->
			var newText = change!!.controlNewText
			if (newText.isEmpty()) {
				change.setText("0")
				return@UnaryOperator change
			}
			try {
				if (newText.length > 1) newText = newText.replace("^0+(?!$)".toRegex(), "")

				val value = newText.toInt()
				if (value >= 0 && value <= 360) {
					return@UnaryOperator change
				}
			} catch (e: NumberFormatException) {
				return@UnaryOperator null
			}
			null
		}
		val limit0_255 = UnaryOperator { change: TextFormatter.Change? ->
			var newText = change!!.controlNewText
			if (newText.isEmpty()) {
				change.setText("0")
				return@UnaryOperator change
			}
			try {
				if (newText.length > 1) newText = newText.replace("^0+(?!$)".toRegex(), "")

				val value = newText.toInt()
				if (value >= 0 && value <= 255) {
					return@UnaryOperator change
				}
			} catch (e: NumberFormatException) {
				return@UnaryOperator null
			}
			null
		}
		val limit0_100 = UnaryOperator { change: TextFormatter.Change? ->
			var newText = change!!.controlNewText
			if (newText.isEmpty()) {
				change.setText("0")
				return@UnaryOperator change
			}
			try {
				if (newText.length > 1) newText = newText.replace("^0+(?!$)".toRegex(), "")

				val value = newText.toInt()
				if (value >= 0 && value <= 100) {
					return@UnaryOperator change
				}
			} catch (e: NumberFormatException) {
				return@UnaryOperator null
			}
			null
		}

		val converter: StringConverter<Number?> = object : StringConverter<Number?>() {
			override fun toString(n: Number?): String {
				return n?.toInt()?.toString() ?: "0"
			}

			override fun fromString(s: String): Number {
				return if (s.isEmpty()) 0 else s.toInt()
			}
		}

		hexValue.textProperty().addListener { _, _, newValue ->
			val newColor = getColorFromHexTextField()
			if (newColor != null && newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		firstValue.textProperty().addListener { _, _, newValue ->
			if (!firstValue.isFocused) return@addListener
			val newColor = getColorFromTextFields()
			if (newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		secondValue.textProperty().addListener { _, _, newValue ->
			if (!secondValue.isFocused) return@addListener
			val newColor = getColorFromTextFields()
			if (newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		thirdValue.textProperty().addListener { _, _, newValue ->
			if (!thirdValue.isFocused) return@addListener
			val newColor = getColorFromTextFields()
			if (newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}
		alphaValue.textProperty().addListener { _, _, newValue ->
			if (!alphaValue.isFocused) return@addListener
			val newColor = getColorFromTextFields()
			if (newColor != selectingColorProperty.value) {
				selectingColorProperty.value = newColor
			}
		}

		/* Mode selector */
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
				Lsp.lsb("colorPicker.advancedMode.hsb").value
			)
			modeSelector.selectionModel.select(index)
		}
		localizeModeSelector()
		modeSelector.value = Lsp.lsb("colorPicker.advancedMode.rgb").value
		LocalizationService.currentLocaleProperty.addListener { _, _, _ ->
			localizeModeSelector()
		}

		/* */
		alphaValue.textFormatter = TextFormatter(converter, 100, limit0_100)
		alphaLabel.textProperty().bind(Lsp.lsb("colorPicker.advancedMode.alpha"))
		advancedModeToggle.selectedProperty().addListener { observable, oldValue, newValue ->
			if (newValue) {
				advancedModeIndicator.content = EXPANDED
			} else {
				advancedModeIndicator.content = COLLAPSED
			}
		}

		/* Auto update */
		fun updateFieldsFromSelectingColor() {
			if (isUpdatingFields) return
			isUpdatingFields = true
			if (selectingColorProperty.value != null) {
				val color = selectingColorProperty.value!!
				if (modeSelector.selectionModel.selectedIndex == 0) {
					// RGB mode
					firstValue.text = ((color.red * 255).toInt()).toString()
					secondValue.text = ((color.green * 255).toInt()).toString()
					thirdValue.text = ((color.blue * 255).toInt()).toString()
				} else {
					// HSB mode
					firstValue.text = color.hue.toInt().toString()
					secondValue.text = (color.saturation*100).toInt().toString()
					thirdValue.text = (color.brightness*100).toInt().toString()
				}
				alphaValue.text = (color.opacity * 100).toInt().toString()
				hexValue.text = "#${color.toString().substring(2, 8)}"
			}
			isUpdatingFields = false
		}
		selectingColorProperty.addListener {
			updateFieldsFromSelectingColor()
		}

		/* Fields mode change */
		fun updateLabelsAndTextFormatters() {
			if (modeSelector.selectionModel.selectedIndex == 0) {
				// RGB mode
				firstLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.rgb.red"))
				}
				secondLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.rgb.green"))
				}
				thirdLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.rgb.blue"))
				}

				firstValue.textFormatter = TextFormatter(converter, 0, limit0_255)
				secondValue.textFormatter = TextFormatter(converter, 0, limit0_255)
				thirdValue.textFormatter = TextFormatter(converter, 0, limit0_255)
			} else {
				// HSB mode
				firstLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.hsb.hue"))
				}
				secondLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.hsb.saturation"))
				}
				thirdLabel.textProperty().apply {
					unbind()
					bind(Lsp.lsb("colorPicker.hsb.brightness"))
				}

				firstValue.textFormatter = TextFormatter(converter, 0, limit0_360)
				secondValue.textFormatter = TextFormatter(converter, 0, limit0_100)
				thirdValue.textFormatter = TextFormatter(converter, 0, limit0_100)
			}
		}
		modeSelector.selectionModel.selectedIndexProperty().addListener { _, _, _ ->
			updateLabelsAndTextFormatters()
			updateFieldsFromSelectingColor()
		}

		/* Initialization */
		updateLabelsAndTextFormatters()
		updateFieldsFromSelectingColor()
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

	private fun getColorFromPng(customBrightness: Double? = null): Color? {
		if (lastMousePositionProperty.value == null) return null
		var x = lastMousePositionProperty.value!!.x
		var y = lastMousePositionProperty.value!!.y
		if (x.isNaN()) return null
		if (y.isNaN()) return null
		val middleX = interactableCanvas.width / 2
		val middleY = interactableCanvas.height / 2

		var color = pngDisplay.image.pixelReader.getColor(
			(x / 255 * pngDisplay.image.width).coerceIn(0.0, pngDisplay.image.width-1).toInt(),
			(y / 255 * pngDisplay.image.height).coerceIn(0.0, pngDisplay.image.height-1).toInt(),
		)

		val angle = atan2(middleY - y, middleX - x)
		while (color.opacity == 0.0) {
			x += cos(angle)
			y += sin(angle)

			color = pngDisplay.image.pixelReader.getColor(
				(x / interactableCanvas.width * pngDisplay.image.width).coerceIn(0.0, pngDisplay.image.width-1).toInt(),
				(y / interactableCanvas.width * pngDisplay.image.height).coerceIn(0.0, pngDisplay.image.height-1).toInt(),
			)
		}

		lastMousePositionProperty.value = Point2D(x, y)
		val hsb = RGBtoHSB((color.red*255).toInt(), (color.green*255).toInt(), (color.blue*255).toInt(), null)

		if (customBrightness != null) {
			hsb[2] = (customBrightness/100).toFloat()
		} else {
			brightnessSlider.value = hsb[2] * 100.0
		}


		val awtColor = java.awt.Color(HSBtoRGB(hsb[0], hsb[1], hsb[2]))
		return Color(awtColor.red / 255.0, awtColor.green / 255.0, awtColor.blue / 255.0, alphaSlider.value/100)
	}

	private fun getColorFromHue(): Color? {
		if (lastMousePositionProperty.value == null) return null
		var x = lastMousePositionProperty.value!!.x.coerceIn(0.0 , canvasDisplay.width)
		var y = lastMousePositionProperty.value!!.y.coerceIn(0.0 , canvasDisplay.height)
		x = if (x.isNaN()) 0.0 else x
		y = if (y.isNaN()) 0.0 else y

		val baseColor = intToColor(hueColorSlider.value.toInt())
		val saturation = 1 - (x / canvasDisplay.width)
		val brightness = 1 - (y / canvasDisplay.height)

		val r = ((1 - saturation) * baseColor.red + saturation ) * brightness
		val g = ((1 - saturation) * baseColor.green + saturation ) * brightness
		val b = ((1 - saturation) * baseColor.blue + saturation ) * brightness
		val a = alphaSlider.value / 100

		return Color(r,g,b,a)
	}

	private fun getColorFromTextFields(): Color {
		return if (modeSelector.selectionModel.selectedIndex == 0) {
			// RGB mode
			val red = firstValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
			val green = secondValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
			val blue = thirdValue.text.toIntOrNull()?.coerceIn(0, 255) ?: 0
			val alpha = alphaValue.text.toDoubleOrNull()?.coerceIn(0.0, 100.0)?.div(100) ?: 1.0

			Color(red / 255.0, green / 255.0, blue / 255.0, alpha)
		} else {
			// HSB mode
			val hue = firstValue.text.toIntOrNull()?.coerceIn(0, 360) ?: 0
			val saturation = secondValue.text.toIntOrNull()?.coerceIn(0, 100)?.div(100.0) ?: 1.0
			val brightness = thirdValue.text.toIntOrNull()?.coerceIn(0, 100)?.div(100.0) ?: 1.0
			val alpha = alphaValue.text.toDoubleOrNull()?.coerceIn(0.0, 100.0)?.div(100) ?: 1.0

			Color.hsb(hue.toDouble(), saturation, brightness, alpha)
		}
	}

	private fun getColorFromHexTextField(): Color? {
		val hex = hexValue.text.trim().removePrefix("#")
		if (hex.length == 6 || hex.length == 8) {
			// match with regex characters 0-9, a-f, A-F
			val hexRegex = Regex("^[0-9a-fA-F]+$")
			if (!hexRegex.matches(hex)) return null
			return Color.web(hexValue.text)
		}
		return null
	}

	private fun getColorFromPngOrHue(customBrightness: Double? = null): Color? {
		return if (isHueSelectorProperty.value) {
			getColorFromHue()
		} else {
			getColorFromPng(customBrightness)
		}
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
		val x = x.coerceIn(0.0, interactableCanvas.width - 1)
		val y = y.coerceIn(0.0, interactableCanvas.height - 1)
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
}