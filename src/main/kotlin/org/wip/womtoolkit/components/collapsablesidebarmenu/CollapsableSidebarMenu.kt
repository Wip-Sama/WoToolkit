package org.wip.womtoolkit.components.collapsablesidebarmenu

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import org.wip.womtoolkit.model.LocalizationService
import java.io.IOException
import kotlin.properties.Delegates

//TODO: Add documentation
//TODO: when making the application fullscreen, the selected indicator is not correctly positioned if the last button is selected
//TODO: Generalize this component to allow for more buttons and different uses
class CollapsableSidebarMenu : AnchorPane() {
    @FXML lateinit var collapseToggle: Button
    @FXML lateinit var slicer: Button
    @FXML lateinit var converter: Button
    @FXML lateinit var settings: Button
    @FXML lateinit var testButton: Button
    @FXML lateinit var selected_indicator: Pane
    lateinit var selected_indicator_base_size: Pair<Double, Double>

    var isCollapsed:  Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            if (new)
                hideButtonsText()
            else
                showButtonsText()
        }
    }

    var isCollapsable: Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            collapseToggle.isVisible = new
            collapseToggle.isManaged = new
        }
    }

    private var selected_button: Button? by Delegates.observable(null) { _, old, new ->
        if (new != null && new != old) {
            animateSelectedIndicator(new)
            selectedButtonProperty.value = new
        }
        old?.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false)
        new?.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true)
    }
    val selectedButtonProperty: SimpleObjectProperty<Button> = SimpleObjectProperty<Button>().apply {
        addListener { _, _, newValue ->
            selected_button = newValue
        }
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

    private fun hideButtonsText() {
        slicer.textProperty().unbind()
        converter.textProperty().unbind()
        settings.textProperty().unbind()
        slicer.text = ""
        converter.text = ""
        settings.text = ""
    }

    private fun showButtonsText() {
        slicer.textProperty().bind(LocalizationService.lsb("menu.slicer"))
        converter.textProperty().bind(LocalizationService.lsb("menu.converter"))
        settings.textProperty().bind(LocalizationService.lsb("menu.settings"))
    }

    @FXML
    protected fun initialize() {
        this.heightProperty().addListener { _, _, _ ->
            selected_button?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        this.widthProperty().addListener { _, _, _ ->
            selected_button?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        if (isCollapsed) {
            hideButtonsText()
        } else {
            showButtonsText()
        }
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
        if (!::selected_indicator_base_size.isInitialized) {
            selected_indicator_base_size = Pair(selected_indicator.prefWidth, selected_indicator.prefHeight)
        }
        val y = computeSelectedIndicatorY(button)
        val midY = (y + selected_indicator.layoutY - selected_indicator.height / 2 ) / 2
        val timeline = Timeline(
            KeyFrame(Duration.ZERO,
                KeyValue(selected_indicator.prefHeightProperty(), selected_indicator_base_size.second),
            ),
            KeyFrame(Duration.millis(40.0),
                KeyValue(selected_indicator.prefHeightProperty(), selected_indicator_base_size.second*2),
                KeyValue(selected_indicator.layoutYProperty(), midY)
            ),
            KeyFrame(Duration.millis(80.0),
                KeyValue(selected_indicator.prefHeightProperty(), selected_indicator_base_size.second),
                KeyValue(selected_indicator.layoutYProperty(), y)
            )
        )
        timeline.play()
    }

    @FXML
    fun onCollapseClick() {
        if (!isCollapsable) return
        val startWidth = this.width
        val endWidth = if (!isCollapsed) 32.0+24 else 150.0
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
    }

    @FXML
    fun onTestClick() {
        if (LocalizationService.currentLocale == "jpJP") {
            LocalizationService.currentLocale = "itIT"
        } else {
            LocalizationService.currentLocale = "jpJP"
        }
    }
}
