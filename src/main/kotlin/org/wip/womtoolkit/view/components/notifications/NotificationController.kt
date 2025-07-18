package org.wip.womtoolkit.view.components.notifications

import javafx.animation.FadeTransition
import javafx.animation.TranslateTransition
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.services.localization.Lsp
import org.wip.womtoolkit.model.services.notification.NotificationService
import org.wip.womtoolkit.model.services.notification.NotificationData
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
			Lsp.lsb("notificationDispenser.remainingNotifications", SimpleStringProperty("0"))
		)
		notificationDispenser.dismissAll.setOnAction {
			NotificationService.clearNotifications()
			notificationContainer.children.clear()
		}

		var oldValue: Int = 0
		scope.launch {
			NotificationService.size.collect { newValue ->
				with(Dispatchers.JavaFx) {
					val old: Boolean = oldValue > 0
					val new: Boolean = newValue > 0

					if (new && !old && notificationContainer.children.size >= availableNotificationSlots) {
						showDispenserWithAnimation()
					} else if (!new) {
						hideDispenserWithAnimation()
					}
					notificationDispenser.label.textProperty().apply {
						unbind()
						bind(Lsp.lsb("notificationDispenser.remainingNotifications", SimpleIntegerProperty(newValue).asString()))
					}

					oldValue = newValue
				}
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
					if (notification.autoDismiss)
						animateProgressBar(notification)
				})
			}
		}
	}

	fun addWithAnimation(node: Node) {
		node.opacity = 0.0
		notificationContainer.children.add(node)

		val time = when(ApplicationSettings.userSettings.disableAnimations.value) {
			true -> 1.0
			false -> 200.0
		}

		FadeTransition(Duration.millis(time), node).apply {
			toValue = 1.0
		}.play()
	}

	fun removeWithAnimation(node: Node) {
		val time = when(ApplicationSettings.userSettings.disableAnimations.value) {
			true -> 1.0
			false -> 200.0
		}

		FadeTransition(Duration.millis(time), node).apply {
			toValue = 0.0
			setOnFinished {
				notificationContainer.children.remove(node)
			}
		}.play()
	}

	fun showDispenserWithAnimation() {
		val time = when(ApplicationSettings.userSettings.disableAnimations.value) {
			true -> 1.0
			false -> 200.0
		}

		FadeTransition(Duration.millis(time), notificationDispenser).apply {
			toValue = 1.0
			onFinished = EventHandler { event ->
				notificationDispenser.isVisible = true
				notificationDispenser.isManaged = true
			}
			play()
		}

		TranslateTransition(Duration.millis(time), notificationContainer).apply {
			toY = 0.0
			play()
		}

		notificationDispenser.toFront()
	}

	fun hideDispenserWithAnimation() {
		val time = when(ApplicationSettings.userSettings.disableAnimations.value) {
			true -> 1.0
			false -> 200.0
		}

		FadeTransition(Duration.millis(time), notificationDispenser).apply {
			toValue = 0.0
			onFinished = EventHandler { event ->
				notificationDispenser.isVisible = false
				notificationDispenser.isManaged = false
			}
			play()
		}

		TranslateTransition(Duration.millis(time), notificationContainer).apply {
			toY = 0.0
			play()
		}
	}
}