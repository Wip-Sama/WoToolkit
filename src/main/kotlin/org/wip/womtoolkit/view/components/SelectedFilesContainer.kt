package org.wip.womtoolkit.view.components

import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane

class SelectedFilesContainer : BorderPane() {
	@FXML lateinit var selectedFilesList: ListView<String>
	@FXML lateinit var previewContainer: BorderPane
	@FXML lateinit var previewPane: ScrollPane
	@FXML lateinit var previewImage: ImageView

	private val dragOverPseudoClass = javafx.css.PseudoClass.getPseudoClass("drag-over")
	private val zoomProperty = SimpleDoubleProperty(1.0)
	private var draggingTab = SimpleObjectProperty<ListView<String>>()
	private val nodeTimelines = mutableMapOf<Node, Timeline>()
	private var lastTargetIndex: Int? = null

	init {
		FXMLLoader(javaClass.getResource("/view/components/selectedFilesContainer.fxml")).apply {
			setRoot(this@SelectedFilesContainer)
			setController(this@SelectedFilesContainer)
			load()
		}
	}

	@FXML
	private fun initialize() {
		initializeImageControls()

		selectedFilesList.apply {
			// Set properties for the ListView if needed
			isEditable = false
			placeholder = javafx.scene.control.Label("No files selected")
		}
		// add 10 elements numbered 1 to 10
		selectedFilesList.items.addAll((1..10).map { "File $it" })

		selectedFilesList.cellFactory = javafx.util.Callback {
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
						//check if element is not null and indices are valid
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
	}

	private fun initializeImageControls() {
		previewPane.isPannable = true
		previewPane.fitToWidthProperty().set(true)
		previewPane.fitToHeightProperty().set(true)
		previewPane.content = previewImage

		val imageHolder = StackPane(previewImage)
		imageHolder.alignment = javafx.geometry.Pos.CENTER
		previewPane.content = imageHolder

		previewImage.isPreserveRatio = true
		previewImage.onMouseEntered = EventHandler { e: MouseEvent? ->
			previewImage.cursor = Cursor.OPEN_HAND
		}
		previewImage.onMousePressed = EventHandler { e: MouseEvent? ->
			previewImage.cursor = Cursor.CLOSED_HAND
		}
		previewImage.onMouseReleased = EventHandler { e: MouseEvent? ->
			previewImage.cursor = Cursor.OPEN_HAND
		}
		previewImage.onMouseExited = EventHandler { e: MouseEvent? ->
			previewImage.cursor = Cursor.DEFAULT
		}
		previewImage.onScroll = EventHandler { e: javafx.scene.input.ScrollEvent ->
			if (e.isControlDown) {
				e.consume()
				val zoomFactor = if (e.deltaY > 0) 1.1 else 0.9
				val newZoom = zoomProperty.value * zoomFactor
				zoomProperty.value = newZoom.coerceIn(1.0, 10.0)
			}
		}

		fun updateImageHolderSize() {
			val img = previewImage.image
			if (img != null) {
				imageHolder.minWidth = 2 * previewPane.width
				imageHolder.minHeight = 2 * previewPane.height
				imageHolder.prefWidth = imageHolder.minWidth
				imageHolder.prefHeight = imageHolder.minHeight
			}
		}

		previewImage.imageProperty().addListener { _, _, _ -> updateImageHolderSize() }
		previewPane.widthProperty().addListener { _, _, _ -> updateImageHolderSize() }
		previewPane.heightProperty().addListener { _, _, _ -> updateImageHolderSize() }

		zoomProperty.addListener { _, _, newV ->
			fitImage(newV.toDouble())
		}

		Platform.runLater {
			previewPane.hvalue = 0.5
			previewPane.vvalue = 0.5
			fitImage()
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