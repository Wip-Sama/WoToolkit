package org.wip.womtoolkit.view.components.notifications

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.css.PseudoClass
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.services.notification.NotificationData
import org.wip.womtoolkit.view.components.LocalizedLabel
import kotlin.String
import kotlin.properties.Delegates

class NotificationWindow() : BorderPane() {
	companion object {
		const val SUCCESS: String = "M12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2ZM15.2197 8.96967L10.75 13.4393L8.78033 11.4697C8.48744 11.1768 8.01256 11.1768 7.71967 11.4697C7.42678 11.7626 7.42678 12.2374 7.71967 12.5303L10.2197 15.0303C10.5126 15.3232 10.9874 15.3232 11.2803 15.0303L16.2803 10.0303C16.5732 9.73744 16.5732 9.26256 16.2803 8.96967C15.9874 8.67678 15.5126 8.67678 15.2197 8.96967Z"
		const val ERROR: String = "M10.0297 3.65893C10.8859 2.1111 13.1111 2.11108 13.9673 3.65888L21.7131 17.6603C22.5427 19.16 21.4581 20.9995 19.7443 20.9995H4.25323C2.53945 20.9995 1.45483 19.16 2.28438 17.6604L10.0297 3.65893ZM12.9973 17.0009C12.9973 16.4494 12.5502 16.0022 11.9987 16.0022C11.4471 16.0022 11 16.4494 11 17.0009C11 17.5524 11.4471 17.9996 11.9987 17.9996C12.5502 17.9996 12.9973 17.5524 12.9973 17.0009ZM12.7381 9.14764C12.6882 8.7816 12.3742 8.4997 11.9945 8.5C11.5802 8.50033 11.2447 8.83639 11.2451 9.2506L11.2487 13.7522L11.2556 13.854C11.3055 14.22 11.6196 14.5019 11.9993 14.5016C12.4135 14.5013 12.749 14.1652 12.7487 13.751L12.7451 9.2494L12.7381 9.14764Z"
		const val WARNING: String = "M12 2C17.523 2 22 6.478 22 12C22 17.522 17.523 22 12 22C6.477 22 2 17.522 2 12C2 6.478 6.477 2 12 2ZM12.0018 15.0037C11.4503 15.0037 11.0031 15.4508 11.0031 16.0024C11.0031 16.5539 11.4503 17.001 12.0018 17.001C12.5533 17.001 13.0005 16.5539 13.0005 16.0024C13.0005 15.4508 12.5533 15.0037 12.0018 15.0037ZM11.9996 7C11.4868 7.00018 11.0643 7.38638 11.0067 7.88374L11 8.00036L11.0018 13.0012L11.0086 13.1179C11.0665 13.6152 11.4893 14.0011 12.0022 14.0009C12.515 14.0007 12.9375 13.6145 12.9951 13.1171L13.0018 13.0005L13 7.99964L12.9932 7.88302C12.9353 7.3857 12.5125 6.99982 11.9996 7Z"
		const val INFO: String = "M12.0016 1.99902C17.5253 1.99902 22.0031 6.47687 22.0031 12.0006C22.0031 17.5243 17.5253 22.0021 12.0016 22.0021C6.47785 22.0021 2 17.5243 2 12.0006C2 6.47687 6.47785 1.99902 12.0016 1.99902ZM12 10.5C11.5858 10.5 11.25 10.8358 11.25 11.25V16.25C11.25 16.6642 11.5858 17 12 17C12.4142 17 12.75 16.6642 12.75 16.25V11.25C12.75 10.8358 12.4142 10.5 12 10.5ZM12 9C12.5523 9 13 8.55228 13 8C13 7.44772 12.5523 7 12 7C11.4477 7 11 7.44772 11 8C11 8.55228 11.4477 9 12 9Z"
	}

	@FXML lateinit var basePane: BorderPane
	@FXML lateinit var dismiss: Button
	@FXML lateinit var icon: SVGPath
	@FXML lateinit var label: LocalizedLabel
	@FXML lateinit var progressBar: ProgressBar
	@FXML lateinit var progressBarContainer: BorderPane

	var notificationData: NotificationData by Delegates.observable(NotificationData(
		localizedContent = "",
		type = NotificationTypes.INFO,
	)) { _, oldValue, newValue ->
		if (oldValue != newValue) {
			label.localizationKey = newValue.localizedContent
			updateTheme()
			if (newValue.autoDismiss) {
				animateProgressBar(newValue)
			}
		}
	}

	var onHide: EventHandler<Event>? = null

	val rectClip = Rectangle().apply {
		arcHeight = 18.0
		arcWidth = 18.0
	}

	constructor(notificationData: NotificationData) : this() {
		this.notificationData = notificationData
	}

	init {
		FXMLLoader(javaClass.getResource("/view/components/notification/notificationWindow.fxml")).apply {
			setRoot(this@NotificationWindow)
			setController(this@NotificationWindow)
			load()
		}

		rectClip.apply {
			heightProperty().bind(progressBarContainer.heightProperty())
			widthProperty().bind(progressBarContainer.widthProperty())
		}
		progressBarContainer.apply {
			clip = rectClip
		}
		dismiss.setOnAction {
			destroy()
		}
	}

	private fun updateTheme(theme: NotificationTypes = notificationData.type) {
		when (theme) {
			NotificationTypes.SUCCESS -> icon.content = SUCCESS
			NotificationTypes.ERROR -> icon.content = ERROR
			NotificationTypes.WARNING -> icon.content = WARNING
			NotificationTypes.INFO -> icon.content = INFO
		}
		pseudoClassStateChanged(PseudoClass.getPseudoClass("success"), theme == NotificationTypes.SUCCESS)
		pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), theme == NotificationTypes.ERROR)
		pseudoClassStateChanged(PseudoClass.getPseudoClass("warning"), theme == NotificationTypes.WARNING)
		pseudoClassStateChanged(PseudoClass.getPseudoClass("info"), theme == NotificationTypes.INFO)
	}

	fun animateProgressBar(notificationData: NotificationData) {
		Timeline(
			KeyFrame(Duration.ZERO, KeyValue(progressBar.progressProperty(), 0)),
			KeyFrame(Duration.millis(notificationData.autoDismissDelay), KeyValue(progressBar.progressProperty(), 1))
		).apply {
			onFinished = EventHandler {
				if (notificationData.autoDismiss) {
					destroy()
				}
			}
			play()
		}
	}

	fun destroy() {
		val time = when(ApplicationSettings.userSettings.disableAnimations.value) {
			true -> 1.0
			false -> 50.0
		}
		Timeline(
			KeyFrame(Duration.millis(time), KeyValue(basePane.opacityProperty(), 0.0))
		).apply {
			onFinished = EventHandler {
				onHide?.handle(Event(this@NotificationWindow, this@NotificationWindow, Event.ANY))
			}
			play()
		}
	}
}