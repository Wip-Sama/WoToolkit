package org.wip.womtoolkit.view.pages

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import kotlin.times

class Slicer : BorderPane() {
    object Constants {
        const val EXPANDED: String =
            "M4.29289 8.29289C4.68342 7.90237 5.31658 7.90237 5.70711 8.29289L12 14.5858L18.2929 8.29289C18.6834 7.90237 19.3166 7.90237 19.7071 8.29289C20.0976 8.68342 20.0976 9.31658 19.7071 9.70711L12.7071 16.7071C12.3166 17.0976 11.6834 17.0976 11.2929 16.7071L4.29289 9.70711C3.90237 9.31658 3.90237 8.68342 4.29289 8.29289Z"
        const val COLLAPSED: String =
            "M4.29289 15.7071C4.68342 16.0976 5.31658 16.0976 5.70711 15.7071L12 9.41421L18.2929 15.7071C18.6834 16.0976 19.3166 16.0976 19.7071 15.7071C20.0976 15.3166 20.0976 14.6834 19.7071 14.2929L12.7071 7.29289C12.3166 6.90237 11.6834 6.90237 11.2929 7.29289L4.29289 14.2929C3.90237 14.6834 3.90237 15.3166 4.29289 15.7071Z"
    }

    @FXML
    lateinit var advancedModeToggle: ToggleButton
    @FXML
    lateinit var advancedIndicator: SVGPath
    @FXML
    lateinit var advancedModeContainer: VBox
    @FXML
    lateinit var advancedModeContent: ScrollPane

    val rectClip = Rectangle().apply {
        arcHeight = 13.0
        arcWidth = 13.0
    }

    init {
        FXMLLoader(javaClass.getResource("/view/pages/slicer.fxml")).apply {
            setRoot(this@Slicer)
            setController(this@Slicer)
            load()
        }
    }

    @FXML
    fun initialize() {
        initializeAdvancedMode()
    }

    private fun initializeAdvancedMode() {
        advancedModeContainer.clip = rectClip
        rectClip.widthProperty().bind(advancedModeContainer.widthProperty())

        fun animateExpand() {
            val newHeight = if (advancedModeToggle.isSelected)
                (advancedModeToggle.height) +
                        (advancedModeContainer.spacing * (advancedModeContainer.children.size - 1)) +
                        80.0
            else
                advancedModeToggle.height

            val animationDuration = if (ApplicationSettings.userSettings.disableAnimations) 1.0 else 200.0
            Timeline(
                KeyFrame(
                    Duration.millis(animationDuration),
                    KeyValue(advancedModeContainer.prefHeightProperty(), newHeight),
                    KeyValue(advancedModeContainer.maxHeightProperty(), newHeight),
                    KeyValue(advancedModeContainer.minHeightProperty(), newHeight),
                ),
            ).play()
        }

        advancedModeToggle.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue != oldValue) {
                animateExpand()
                advancedIndicator.content = if (newValue) Constants.EXPANDED else Constants.COLLAPSED
            }
        }

        /* Initialize */
        advancedModeContent.isVisible = false
        advancedModeContent.isManaged = false
        rectClip.heightProperty().bind(advancedModeContainer.heightProperty())

        Platform.runLater {
            val newHeight = if (advancedModeToggle.isSelected)
                (advancedModeToggle.height) +
                        (advancedModeContainer.spacing * (advancedModeContainer.children.size - 1)) +
                        80.0
            else
                advancedModeToggle.height
            advancedModeContainer.prefHeight = newHeight
            advancedModeContainer.minHeight = newHeight
            advancedModeContainer.maxHeight = newHeight
            advancedModeContent.isVisible = true
            advancedModeContent.isManaged = true
        }
    }
}