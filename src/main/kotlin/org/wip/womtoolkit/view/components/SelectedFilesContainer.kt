package org.wip.womtoolkit.view.components

import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.util.Callback
import org.wip.womtoolkit.model.Lsp
import kotlin.div
import kotlin.properties.Delegates
import kotlin.times

//TODO: the should zoom from mouse position not relative to the top left corner
class SelectedFilesContainer : BorderPane() {
	@FXML lateinit var selectedFilesList: ListView<String>
	@FXML lateinit var previewContainer: BorderPane
	@FXML lateinit var previewPane: ScrollPane
	@FXML lateinit var previewImage: ImageView

	@FXML lateinit var inputPathLabel: Label
	@FXML lateinit var mediumHeight: Label
	@FXML lateinit var mediumWidth: Label
	@FXML lateinit var fileWeight: Label
	@FXML lateinit var fileSize: Label
	@FXML lateinit var zoomLevel: Label

	private val imageHolder = StackPane()
	private val dragOverPseudoClass = javafx.css.PseudoClass.getPseudoClass("drag-over")
	private val zoomProperty = SimpleDoubleProperty(1.0)
	private var mousePosition = SimpleObjectProperty<Pair<Double, Double>>()
	private var draggingTab = SimpleObjectProperty<ListView<String>>()
	private val nodeTimelines = mutableMapOf<Node, Timeline>()
	private var lastTargetIndex: Int? = null

	var inputPath: String? by Delegates.observable(null) { _, _, newValue ->
			inputPathLabel.text = newValue
		}
	var outputPath: String? = null
	var fileList: List<String> by Delegates.observable(emptyList()) { _, _, newValue ->
		selectedFilesList.items.setAll(newValue)
		updateImageListInfo()
		if (newValue.isNotEmpty()) {
			changeImage(Image("file:${newValue.first()}"))
		} else {
			changeImage(null)
		}
	}

	init {
		FXMLLoader(javaClass.getResource("/view/components/selectedFilesContainer.fxml")).apply {
			setRoot(this@SelectedFilesContainer)
			setController(this@SelectedFilesContainer)
			load()
		}
	}

	@FXML
	private fun initialize() {
		initializeDefaults()
		initializeImageControls()
		initializeSelectedFilesList()
	}

	private fun initializeDefaults() {
		zoomLevel.textProperty()
			.bind(Lsp.lsb("selectedFilesContainer.zoomLevel", zoomProperty.multiply(100).asString("%.0f")))
	}

	private fun initializeImageControls() {
		previewImage.apply {
			isPreserveRatio = true
			imageProperty().addListener { _, _, _ -> updateImageHolderSizeAnsPosition() }
		}

		imageHolder.apply {
			children.add(previewImage)
			alignment = javafx.geometry.Pos.CENTER
			onScroll = EventHandler { e: javafx.scene.input.ScrollEvent ->
				if (e.isControlDown) {
					e.consume()
					val zoomFactor = if (e.deltaY > 0) 1.1 else 0.9
					val newZoom = zoomProperty.value * zoomFactor
					zoomProperty.value = newZoom.coerceIn(1.0, 10.0)
					mousePosition.value = Pair(e.x, e.y)
				}
			}
			onMouseEntered = EventHandler { cursor = Cursor.OPEN_HAND }
			onMousePressed = EventHandler { cursor = Cursor.CLOSED_HAND }
			onMouseReleased = EventHandler { cursor = Cursor.OPEN_HAND }
			onMouseExited = EventHandler { cursor = Cursor.DEFAULT }
		}

		previewPane.apply {
			isPannable = true
			fitToWidthProperty().set(true)
			fitToHeightProperty().set(true)
			content = imageHolder
			widthProperty().addListener { _, _, _ -> updateImageHolderSizeAnsPosition() }
			heightProperty().addListener { _, _, _ -> updateImageHolderSizeAnsPosition() }
		}

		zoomProperty.addListener { _, _, newV ->
			updateImageHolderSizeAnsPosition(mousePosition.value?.first, mousePosition.value?.second)
			fitImage(newV.toDouble())
		}

		Platform.runLater {
			previewPane.hvalue = 0.5
			previewPane.vvalue = 0.5
			fitImage()
		}
	}

	private fun updateImageHolderSizeAnsPosition(mouseX: Double? = null, mouseY: Double? = null) {
		val img = previewImage.image
		if (img != null) {
			// Salva la posizione relativa corrente
			val currentRelativeX = previewPane.hvalue
			val currentRelativeY = previewPane.vvalue

			val oldW = imageHolder.width
			val oldH = imageHolder.height

			val newHeight = 2 * previewPane.width * zoomProperty.value
			val newWidth = 2 * previewPane.height * zoomProperty.value

			imageHolder.minWidth = newWidth
			imageHolder.minHeight = newHeight
			imageHolder.prefWidth = imageHolder.minWidth
			imageHolder.prefHeight = imageHolder.minHeight

			if (oldW > 0 && oldH > 0) {
				val scaleFactorX = newWidth / oldW
				val scaleFactorY = newHeight / oldH

				previewPane.hvalue = (currentRelativeX * scaleFactorX).coerceIn(0.0, 1.0)
				previewPane.vvalue = (currentRelativeY * scaleFactorY).coerceIn(0.0, 1.0)
			}
		}
	}

	private fun initializeSelectedFilesList() {
		selectedFilesList.apply {
			isEditable = false
			placeholder = LabelWithLocalization().apply { localizationKey = "selectedFilesContainer.noSelectedFiles" }
		}

		selectedFilesList.cellFactory = Callback {
			object : ListCell<String>() {
				init {
					setOnDragDetected { event ->
						if (item == null) return@setOnDragDetected
						val db = startDragAndDrop(TransferMode.MOVE)
						val content = ClipboardContent()
						content.putString(item)
						db.setContent(content)
						event.consume()
					}

					setOnDragOver { event ->
						if (event.gestureSource != this && event.dragboard.hasString()) {
							event.acceptTransferModes(TransferMode.MOVE)
							pseudoClassStateChanged(dragOverPseudoClass, true)
						}
						event.consume()
					}

					setOnDragExited { _ ->
						pseudoClassStateChanged(dragOverPseudoClass, false)
					}

					setOnDragDropped { event ->
						val db = event.dragboard
						val draggedItem = db.string
						val items = listView.items
						val draggedIdx = items.indexOf(draggedItem)
						val thisIdx = index
						//check if the element is not null and indices are valid
						if (draggedIdx >= 0 && thisIdx >= 0 && draggedIdx != thisIdx && thisIdx < items.size) {
							items.removeAt(draggedIdx)
							items.add(thisIdx, draggedItem)
							listView.selectionModel.select(thisIdx)
						}
						event.isDropCompleted = true
						event.consume()
					}
				}
				override fun updateItem(item: String?, empty: Boolean) {
					super.updateItem(item, empty)
					text = if (empty || item == null) null else item
					if (empty) pseudoClassStateChanged(dragOverPseudoClass, false)
				}
			}
		}

		selectedFilesList.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
			if (newValue != null) {
				changeImage(Image("file:${inputPath}${if (inputPath?.isEmpty() ?: true) "" else "\\"}${newValue}"))
			} else {
				changeImage(null)
			}
		}
	}

	private fun changeImage(image: Image?) {
		previewImage.image = image
		if (image != null) {
			fitImage()
		} else {
			previewImage.fitWidth = 0.0
			previewImage.fitHeight = 0.0
			previewPane.hvalue = 0.5
			previewPane.vvalue = 0.5
		}
		updateImageInfo(image)
	}

	private fun updateImageListInfo() {
		Thread {
			var mH = 0.0
			var mW = 0.0

			fileList.forEach { item ->
				val img = Image("file:${inputPath}${if (inputPath?.isEmpty() ?: true) "" else "\\"}${item}")
				if (img.isError) return@forEach
				if (img.height > mH) mH = img.height
				if (img.width > mW) mW = img.width
			}

			mH.div(fileList.size.toDouble())
			mW.div(fileList.size.toDouble())

			Platform.runLater {
				mediumWidth.textProperty().unbind()
				mediumHeight.textProperty().unbind()
				mediumWidth.textProperty().bind(Lsp.lsb("selectedFilesContainer.mediumWidth", SimpleStringProperty(mW.toString())))
				mediumHeight.textProperty().bind(Lsp.lsb("selectedFilesContainer.mediumHeight", SimpleStringProperty(mH.toString())))
			}
		}.start()
	}

	private fun updateImageInfo(image: Image?) {
		if (image != null) {
			fileWeight.textProperty().unbind()
			fileSize.textProperty().unbind()
			fileWeight.textProperty().bind(Lsp.lsb("selectedFilesContainer.fileWeight", image.progressProperty().multiply(100).asString("%.0f")))
			fileSize.textProperty().bind(Lsp.lsb("selectedFilesContainer.fileSize",
				image.widthProperty().asString("%.0f"),
				image.heightProperty().asString("%.0f"))
			)
		} else {
			fileWeight.textProperty().unbind()
			fileSize.textProperty().unbind()
			fileWeight.textProperty().bind(Lsp.lsb("selectedFilesContainer.fileWeight", SimpleStringProperty("N/A")))
			fileSize.textProperty().bind(Lsp.lsb("selectedFilesContainer.fileSize",
				SimpleStringProperty("N/A"),
				SimpleStringProperty("N/A"))
			)
		}
	}

	private fun fitImage(scale: Double = zoomProperty.value) {
		val x = previewPane.hvalue
		val y = previewPane.vvalue

		previewImage.image?.let { img ->
			if (img.height > img.width) {
				previewImage.fitHeight = previewPane.height * scale
				previewImage.fitWidth = previewImage.fitHeight * (img.width / img.height)
			} else {
				previewImage.fitWidth = previewPane.width * scale
				previewImage.fitHeight = previewImage.fitWidth * (img.height / img.width)
			}
		}
		previewPane.hvalue = x
		previewPane.vvalue = y
	}
}