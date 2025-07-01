package org.wip.womtoolkit

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TitledPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.DataManager
import org.wip.womtoolkit.view.pages.MainWindow
import java.util.*

class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		DataManager.init()
		MainWindow()
	}
	override fun stop() {
		DataManager.close()
	}
}

fun main() {
	try {
		Application.launch(WomToolkit::class.java)
	} catch (e: Exception) {
		e.printStackTrace()
	}
}

const val TAB_DRAG_KEY = "titledpane"
private var draggingTab: ObjectProperty<Pane?>? = null

/* Stolen and modified from stackovwerflow*/
@Throws(java.lang.Exception::class)
fun main2(primaryStage: Stage) {
	draggingTab = SimpleObjectProperty<Pane?>()
	val vbox = VBox()
	val nodeTimelines = mutableMapOf<Node, Timeline>()
	var lastTargetIndex: Int? = null


	for (i in 0..3) {
		val draggablePane = Pane()
//		pane.text = "pane" + (i + 1)
		draggablePane.prefHeight = 100.0
		draggablePane.prefWidth = 200.0
		draggablePane.id = "pane" + (i + 1)
		draggablePane.style = "-fx-background-color: #${Integer.toHexString(Random().nextInt(0x1000000))};"
		vbox.children.add(draggablePane)

		draggablePane.onMouseClicked = EventHandler {
			println("clicked!")
		}

		draggablePane.onDragDropped = EventHandler<DragEvent?> {
			val db = it.dragboard
			var success = false
			if (db.hasString()) {
				val parent = draggablePane.parent as Pane
				val source = it.gestureSource
				val sourceIndex = parent.children.indexOf(source)
				val targetIndex = parent.children.indexOf(draggablePane)
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
			it.isDropCompleted = success
			it.consume()
		}

		draggablePane.onDragDetected = EventHandler<MouseEvent?> { event ->
			val dragBoard = draggablePane.startDragAndDrop(TransferMode.MOVE)
			val clipboardContent = ClipboardContent()
			clipboardContent.putString(TAB_DRAG_KEY)
			dragBoard.setContent(clipboardContent)
			val snapshot = draggablePane.snapshot(null, null)
			dragBoard.setDragView(snapshot, event.x, event.y)
			draggingTab!!.set(draggablePane)
			event.consume()
		}

		draggablePane.onDragOver = EventHandler<DragEvent?> {
			val dragBoard = it.dragboard
			val source = draggingTab!!.get()
			if (dragBoard.hasString()
				&& TAB_DRAG_KEY == dragBoard.string
				&& source != null
				&& source != draggablePane
			) {
				val parent = draggablePane.parent as VBox
				val targetIndex = parent.children.indexOf(draggablePane)
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
						val animationDuration = if (ApplicationSettings.userSettings.disableAnimations) 1.0 else 100.0
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
						val deltaY = if (currentIndex < targetIndex) -draggablePane.height else draggablePane.height
						animateNode(it, deltaY)
					}

					val deltaY = (draggablePane.localToScene(0.0, 0.0).y - source.localToScene(0.0, 0.0).y)
					animateNode(source, deltaY) {
						parent.children.remove(source)
						parent.children.add(targetIndex, source)
						nodesToAnimate.forEach { it.translateY = 0.0 }
					}
				}
				it.acceptTransferModes(TransferMode.MOVE)
				it.consume()
			}
		}

	}
	val pane = TitledPane("MAIN", vbox)
	primaryStage.scene = Scene(pane, 890.0, 570.0)
	primaryStage.show()
}
