package org.wip.womtoolkit.view.components.notifications

import javafx.animation.FadeTransition
import javafx.animation.TranslateTransition
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.asFlow
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.services.localization.Lsp
import org.wip.womtoolkit.model.services.notifications.NotificationService
import org.wip.womtoolkit.model.services.notifications.NotificationData
import java.util.PriorityQueue
import kotlin.collections.any

class NotificationController: BorderPane() {
	@FXML lateinit var notificationContainer: VBox
	@FXML lateinit var notificationDispenser: NotificationDispenserWindow

	private val availableNotificationSlots = 2
	val scope = MainScope()

	init {
		FXMLLoader(javaClass.getResource("/view/components/notification/notificationController.fxml")).apply {
			setRoot(this@NotificationController)
			setController(this@NotificationController)
			load()
		}
	}

	@FXML
	fun initialize() {
		notificationContainer.children.addListener(ListChangeListener { change ->
			while (change.next()) {
				if (change.wasRemoved()) {
					notificationScroller()
				}
			}
		})

		notificationDispenser.label.textProperty().bind(
			Lsp.lsb("notificationDispenser.remainingNotifications", NotificationService.sizeProperty.asString())
		)
		notificationDispenser.dismissAll.setOnAction {
			NotificationService.queue.value.clear()
			notificationContainer.children.clear()
			NotificationService.sizeProperty.value = 0
		}

		NotificationService.sizeProperty.addListener { _, oldValue, newValue ->
			val old: Boolean = oldValue != 0
			val new: Boolean = newValue != 0

			if (new && !old) {
				showDispenserWithAnimation()
			} else if (!new) {
				hideDispenserWithAnimation()
			}
		}

		hideDispenserWithAnimation()

		scope.launch {
			NotificationService.queue.collect { notifications ->
				with(Dispatchers.JavaFx) {
					notificationScroller(notifications)
				}
			}
		}
	}

	fun notificationScroller(notifications: PriorityQueue<NotificationData> = NotificationService.queue.value) {
		val notificationList = notifications.toList()
		notificationList.forEach { notification ->
			if (notificationContainer.children.size >= availableNotificationSlots) {
				return@forEach
			}

			notificationContainer.children.any { child ->
				child is NotificationWindow && child.notificationData === notification
			}.let { exist ->
				if (exist)
					return@let
				addWithAnimation(NotificationWindow(notification).apply {
					onHide = EventHandler { event ->
						event.consume()
						removeWithAnimation(this)
					}
					NotificationService.removeNotification(notification)
					animateProgressBar(notification)
				})
			}
		}
	}

	fun addWithAnimation(node: Node) {
		node.opacity = 0.0
		node.translateY = 20.0
		notificationContainer.children.add(node)

		FadeTransition(Duration.millis(200.0), node).apply {
			toValue = 1.0
		}.play()

		TranslateTransition(Duration.millis(200.0), node).apply {
			toY = 0.0
		}.play()
	}

	fun removeWithAnimation(node: Node) {
		FadeTransition(Duration.millis(200.0), node).apply {
			toValue = 0.0
			setOnFinished {
				notificationContainer.children.remove(node)
			}
		}.play()

		TranslateTransition(Duration.millis(200.0), node).apply {
			toY = 20.0
		}.play()
	}

	fun showDispenserWithAnimation() {
		FadeTransition(Duration.millis(200.0), notificationDispenser).apply {
			toValue = 1.0
			onFinished = EventHandler { event ->
				notificationDispenser.isVisible = true
				notificationDispenser.isManaged = true
			}
			play()
		}

		TranslateTransition(Duration.millis(200.0), notificationContainer).apply {
			toY = 0.0
			play()
		}

		notificationDispenser.toFront()
	}

	fun hideDispenserWithAnimation() {
		FadeTransition(Duration.millis(200.0), notificationDispenser).apply {
			toValue = 0.0
			onFinished = EventHandler { event ->
				notificationDispenser.isVisible = false
				notificationDispenser.isManaged = false
			}
			play()
		}

		TranslateTransition(Duration.millis(200.0), notificationContainer).apply {
			toY = 0.0 // Riallinea il container in alto
			play()
		}
	}
}