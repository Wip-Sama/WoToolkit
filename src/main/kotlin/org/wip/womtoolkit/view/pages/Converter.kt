package org.wip.womtoolkit.view.pages

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.services.notification.NotificationService
import org.wip.womtoolkit.model.services.notification.NotificationData
import java.util.Collections
import kotlin.collections.set

const val TAB_DRAG_KEY = "titledpane"

class Converter : BorderPane() {
	@FXML lateinit var buttonContainer: VBox

	private var draggingTab = SimpleObjectProperty<Button>()
	private val nodeTimelines = mutableMapOf<Node, Timeline>()
	private var lastTargetIndex: Int? = null


	init {
		FXMLLoader(javaClass.getResource("/view/pages/converter.fxml")).apply {
			setRoot(this@Converter)
			setController(this@Converter)
			load()
		}

		buttonContainer.children.forEach {
			it.onDragDropped = EventHandler<DragEvent?> { event ->
				if (event == null || it !is Button) return@EventHandler
				val db = event.dragboard
				var success = false
				if (db.hasString()) {
					val parent = it.parent as Pane
					val source = event.gestureSource
					val sourceIndex = parent.children.indexOf(source)
					val targetIndex = parent.children.indexOf(it)
					val copiedNodes = ArrayList(parent.children)
					if (sourceIndex < targetIndex) {
						Collections.rotate(
							copiedNodes.subList(sourceIndex, targetIndex + 1), -1
						)
					} else {
						Collections.rotate(
							copiedNodes.subList(targetIndex, sourceIndex + 1), 1
						)
					}
					parent.children.clear()
					parent.children.addAll(copiedNodes)
					success = true
				}
				event.isDropCompleted = success
				event.consume()
			}

			it.onDragDetected = EventHandler<MouseEvent?> { event ->
				if (event == null || it !is Button) return@EventHandler
				val dragBoard = it.startDragAndDrop(TransferMode.MOVE)
				val clipboardContent = ClipboardContent()
				clipboardContent.putString(TAB_DRAG_KEY)
				dragBoard.setContent(clipboardContent)
				val snapshot = it.snapshot(null, null)
				dragBoard.setDragView(snapshot, event.x, event.y)
				draggingTab.set(it as Button?)
				event.consume()
			}

			it.onDragOver = EventHandler<DragEvent?> { event ->
				if (event == null || it !is Button) return@EventHandler
				val dragBoard = event.dragboard
				val source = draggingTab.get()
				if (dragBoard.hasString()
					&& TAB_DRAG_KEY == dragBoard.string
					&& source != null
					&& source != it
				) {
					val parent = it.parent as VBox
					val targetIndex = parent.children.indexOf(it)
					val currentIndex = parent.children.indexOf(source)
					if (currentIndex != targetIndex && lastTargetIndex != targetIndex) {
						lastTargetIndex = targetIndex
						val nodesToAnimate = if (currentIndex < targetIndex) {
							parent.children.subList(currentIndex + 1, targetIndex + 1)
						} else {
							parent.children.subList(targetIndex, currentIndex)
						}.toList()

						fun animateNode(node: Node, deltaY: Double, onFinished: (() -> Unit)? = null) {
							nodeTimelines[node]?.stop()
							val animationDuration = if (ApplicationSettings.userSettings.disableAnimations.value) 1.0 else 100.0
							val timeline = Timeline(
								KeyFrame(Duration.ZERO, KeyValue(node.translateYProperty(), 0.0)),
								KeyFrame(Duration.millis(animationDuration), KeyValue(node.translateYProperty(), deltaY))
							).apply {
								setOnFinished {
									node.translateY = 0.0
									nodeTimelines.remove(node)
									onFinished?.invoke()
								}
							}
							nodeTimelines[node] = timeline
							timeline.play()
						}

						nodesToAnimate.forEach {
							val deltaY = if (currentIndex < targetIndex) -22.6 else 22.6
							animateNode(it, deltaY)
						}

						val deltaY = (it.localToScene(0.0, 0.0).y - source.localToScene(0.0, 0.0).y)
						animateNode(source, deltaY) {
							parent.children.remove(source)
							parent.children.add(targetIndex, source)
							nodesToAnimate.forEach { it.translateY = 0.0 }
						}
					}
					event.acceptTransferModes(TransferMode.MOVE)
					event.consume()
				}
			}
		}
	}

	fun printButtonOrder() {
		for (i in 0 until buttonContainer.children.size) {
			println("Button $i: ${buttonContainer.children[i]}")
		}
		NotificationService.addNotification(NotificationData(
			"enabled",
			NotificationTypes.INFO
		))
	}
}