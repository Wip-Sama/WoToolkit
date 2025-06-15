package org.wip.womtoolkit.components.collapsablesidebarmenu

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.io.IOException
import kotlin.properties.Delegates

//TODO: Add documentation
//TODO: when making the application fullscreen, the selected indicator is not correctly positioned if the last button is selected

class CollapsableSidebarMenu : AnchorPane() {
    @FXML lateinit var collapseToggle: Button
    @FXML lateinit var slicer: Button
    @FXML lateinit var converter: Button
    @FXML lateinit var settings: Button
    @FXML lateinit var selected_indicator: Pane

    var isCollapsed:  Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            slicer.text = if (new) "" else "Slicer"
            converter.text = if (new) "" else "Converter"
            settings.text = if (new) "" else "Settings"
        }
    }

    var isCollapsable: Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            collapseToggle.isVisible = new
            collapseToggle.isManaged = new
        }
    }

    var selected_button: Button? by Delegates.observable(null) { _, old, new ->
        if (new != null && new != old) {
            animateSelectedIndicator(new)
        }
        old?.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), selected_button == old)
        new?.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), selected_button == new)
    }

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/components/collapsablesidebarmenu/CollapsableSidebarMenu.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @FXML
    protected fun initialize() {
        this.heightProperty().addListener { _, _, _ ->
            selected_button?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        this.widthProperty().addListener { _, _, _ ->
            selected_button?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        slicer.text = if (isCollapsed) "" else "Slicer"
        converter.text = if (isCollapsed) "" else "Converter"
        settings.text = if (isCollapsed) "" else "Settings"
    }

    private fun computeSelectedIndicatorY(button: Button): Double {
        return button.layoutY + button.height / 2 - selected_indicator.height / 2 + button.parent.layoutY
    }

    private fun animateSelectedIndicator(button: Button) {
        if (selected_button == null) {
            selected_indicator.visibleProperty().set(false)
        } else {
            selected_indicator.visibleProperty().set(true)
        }
        val y = computeSelectedIndicatorY(button)
        val midY = (y + selected_indicator.layoutY - selected_indicator.height / 2 ) / 2
        val timeline = Timeline(
            KeyFrame(Duration.ZERO,
                KeyValue(selected_indicator.prefHeightProperty(), 24)
            ),
            KeyFrame(Duration.millis(50.0),
                KeyValue(selected_indicator.prefHeightProperty(), 48),
                KeyValue(selected_indicator.layoutYProperty(), midY)

            ),
            KeyFrame(Duration.millis(100.0),
                KeyValue(selected_indicator.prefHeightProperty(), 24),
                KeyValue(selected_indicator.layoutYProperty(), y)
            )
        )
        timeline.play()
    }

    @FXML
    fun onCollapseClick() {
        if (!isCollapsable) return
        val startWidth = this.width
        val endWidth = if (!isCollapsed) 60 else 150.0
        val timeline = Timeline(
            KeyFrame(
                Duration.millis(100.0),
                KeyValue(this.prefWidthProperty(), endWidth)
            )
        )
        timeline.play()
        isCollapsed = !isCollapsed
    }

    @FXML
    fun onSlicerClick() {
        selected_button = slicer
    }

    @FXML
    fun onConverterClick() {
        selected_button = converter

    }

    @FXML
    fun onSettingsClick() {
        selected_button = settings
        isCollapsable = !isCollapsable
    }
}
