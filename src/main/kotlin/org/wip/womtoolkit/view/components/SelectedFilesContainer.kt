package org.wip.womtoolkit.view.components

import javafx.animation.Timeline
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Rectangle2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import java.io.FileInputStream
import kotlin.div
import kotlin.text.toDouble
import kotlin.times


class SelectedFilesContainer : BorderPane() {
	@FXML lateinit var selectedFilesList: ListView<String>
	@FXML lateinit var previewPane: ScrollPane
	@FXML lateinit var previewImage: ImageView
	@FXML lateinit var zoomSlider: SliderWithProgressColor

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
							style = "-fx-background-color: #cce5ff;" // evidenzia la cella
						}
						event.consume()
					}

					setOnDragExited { _ ->
						style = "" // rimuovi evidenziazione
					}

					setOnDragDropped { event ->
						val db = event.dragboard
						val draggedItem = db.string
						val items = listView.items
						val draggedIdx = items.indexOf(draggedItem)
						val thisIdx = index
						if (draggedIdx >= 0 && thisIdx >= 0 && draggedIdx != thisIdx) {
							items.removeAt(draggedIdx)
							items.add(thisIdx, draggedItem)
							listView.selectionModel.select(thisIdx)
						}
						event.isDropCompleted = true
						style = "" // rimuovi evidenziazione
						event.consume()
					}
				}

				override fun updateItem(item: String?, empty: Boolean) {
					super.updateItem(item, empty)
					text = if (empty || item == null) null else item
					if (empty) style = ""
				}
			}
		}

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
				val newZoom = zoomSlider.value * zoomFactor
				zoomSlider.value = newZoom.coerceIn(0.1, 10.0)
			}
		}

		previewPane.hvalue = 0.5
		previewPane.vvalue = 0.5


		zoomSlider.blockIncrement = 0.1

		zoomSlider.valueProperty().addListener { _, _, newV ->
			val x = previewPane.hvalue
			val y = previewPane.vvalue
			previewImage.image?.let { img ->
				val scale = newV.toDouble()
				previewImage.fitWidth = previewPane.width * scale
//				previewImage.fitHeight = previewPane.height * scale
			}
			previewPane.hvalue = x
			previewPane.vvalue = y
		}

//		zoomSlider.valueProperty().addListener { _, _, newV ->
//			previewImage.image?.let { img ->
//				val scale = newV.toDouble()
//				val viewWidth = img.width / scale
//				val viewHeight = img.height / scale
//				val x = (img.width - viewWidth) / 2
//				val y = (img.height - viewHeight) / 2
//				previewImage.viewport = Rectangle2D(x, y, viewWidth, viewHeight)
//			}
//		}
	}
}