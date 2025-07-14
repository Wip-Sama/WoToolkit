package org.wip.womtoolkit.view.components.collapsablesidebarmenu

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import kotlin.properties.Delegates

//TODO: Add documentation
//TODO: when making the application fullscreen, the selected indicator is not correctly positioned if the last button is selected
//TODO: Generalize this component to allow for more buttons and different uses
open class CollapsableSidebarMenu : AnchorPane() {
    enum class Positions {
        TOP, MIDDLE, BOTTOM
    }

    object Contents {
        const val COLLAPSE: String =
            "M3 17H21C21.5523 17 22 17.4477 22 18C22 18.5128 21.614 18.9355 21.1166 18.9933L21 19H3C2.44772 19 2 18.5523 2 18C2 17.4872 2.38604 17.0645 2.88338 17.0067L3 17H21H3ZM2.99988 11L20.9999 10.9978C21.5522 10.9978 22 11.4454 22 11.9977C22 12.5105 21.6141 12.9333 21.1167 12.9911L21.0001 12.9978L3.00012 13C2.44784 13.0001 2 12.5524 2 12.0001C2 11.4873 2.38594 11.0646 2.88326 11.0067L2.99988 11L20.9999 10.9978L2.99988 11ZM3 5H21C21.5523 5 22 5.44772 22 6C22 6.51284 21.614 6.93551 21.1166 6.99327L21 7H3C2.44772 7 2 6.55228 2 6C2 5.48716 2.38604 5.06449 2.88338 5.00673L3 5H21H3Z"
    }

    private var collapseToggle: CollapsableItem = CollapsableComponent().apply {
        id = "collapseToggle"
        icon.content = Contents.COLLAPSE
        selectable = false
    }
    @FXML private lateinit var selected_indicator: Pane

    @FXML private lateinit var topSection: VBox
    @FXML private lateinit var middleSection: VBox
    @FXML private lateinit var bottomSection: VBox

    lateinit var selectedIndicatorOriginalSize: Pair<Double, Double>

    var isCollapsed:  Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            if (new)
                collapseItems()
            else
                expandItems()
        }
    }
    var isUserCollapsable: Boolean by Delegates.observable(true) { _, old, new ->
        if (new != old) {
            (collapseToggle as Pane).isVisible = new
            (collapseToggle as Pane).isManaged = new
        }
    }

    private var selectedItem: CollapsableItem? by Delegates.observable(null) { _, old, new ->
        if (new != null && new != old) {
            animateSelectedIndicator(new)
            selectedItemProperty.value = new
        }

        old?.deselect()
        new?.select()
    }
    val selectedItemProperty: SimpleObjectProperty<CollapsableItem> = SimpleObjectProperty<CollapsableItem>().apply {
        addListener { _, _, newValue ->
            selectedItem = newValue
        }
    }

    init {
        FXMLLoader(javaClass.getResource("/view/components/collapsablesidebarmenu/CollapsableSidebarMenu.fxml")).apply {
            setRoot(this@CollapsableSidebarMenu)
            setController(this@CollapsableSidebarMenu)
            load()
        }
    }

    @FXML
    protected fun initialize() {
        this.heightProperty().addListener { _, _, _ ->
            selectedItem?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        this.widthProperty().addListener { _, _, _ ->
            selectedItem?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
        }
        addComponent(collapseToggle, Positions.TOP)
        collapseToggle.onActionProperty.addListener { _, _, _ ->
            val endWidth = if (!isCollapsed) 32.0+24 else 150.0
            val animationDuration = if (ApplicationSettings.userSettings.disableAnimations.value) 1.0 else 100.0
            Timeline(
                KeyFrame(
                    Duration.millis(animationDuration),
                    KeyValue(prefWidthProperty(), endWidth)
                )
            ).apply {
                play()
            }
            isCollapsed = !isCollapsed
        }

        if (isCollapsed)
            collapseItems()
        else
            expandItems()

        selected_indicator.parent.layoutBoundsProperty().addListener { _, _, _ ->
            Platform.runLater {
                selectedItem?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
            }
        }
        selected_indicator.layoutBoundsProperty().addListener { _, _, _ ->
            Platform.runLater {
                selectedItem?.let { selected_indicator.layoutY = computeSelectedIndicatorY(it) }
            }
        }

        val width = if (!isCollapsed) 32.0+24 else 150.0
        this.prefWidthProperty().set(width)
    }

    private fun collapseItems() {
        listOf(topSection, middleSection, bottomSection).forEach {
            it.children.forEach { child ->
                if (child is CollapsableItem) {
                    child.expand()
                }
            }
        }
    }

    private fun expandItems() {
        listOf(topSection, middleSection, bottomSection).forEach {
            it.children.forEach { child ->
                if (child is CollapsableItem) {
                    child.expand()
                }
            }
        }
    }

    private fun computeSelectedIndicatorY(item: CollapsableItem): Double {
        val i: Pane = item as Pane
        return i.layoutY + i.height / 2 - selected_indicator.height / 2 + i.parent.layoutY
    }

    private fun animateSelectedIndicator(item: CollapsableItem) {
        selected_indicator.toFront()
        if (selectedItem == null) {
            selected_indicator.visibleProperty().set(false)
        } else {
            selected_indicator.visibleProperty().set(true)
        }
        if (!::selectedIndicatorOriginalSize.isInitialized) {
            if (selected_indicator.prefWidth < 0 || selected_indicator.prefHeight < 0) {
                return
            }
            selectedIndicatorOriginalSize = Pair(selected_indicator.prefWidth, selected_indicator.prefHeight)
        }

        val y = computeSelectedIndicatorY(item)
        val midY = (y + selected_indicator.layoutY - selected_indicator.height / 2) / 2
        val animationDuration = if (ApplicationSettings.userSettings.disableAnimations.value) 1.0 else 40.0
        Timeline(
            KeyFrame(Duration.ZERO,
                KeyValue(selected_indicator.prefHeightProperty(), selectedIndicatorOriginalSize.second),
            ),
            KeyFrame(Duration.millis(animationDuration),
                KeyValue(selected_indicator.prefHeightProperty(), selectedIndicatorOriginalSize.second * 2),
                KeyValue(selected_indicator.layoutYProperty(), midY)
            ),
            KeyFrame(Duration.millis(animationDuration*2),
                KeyValue(selected_indicator.prefHeightProperty(), selectedIndicatorOriginalSize.second),
                KeyValue(selected_indicator.layoutYProperty(), y)
            )
        ).apply {
            play()
        }
    }

    fun addComponent(component: CollapsableItem, position: Positions = Positions.TOP) {
        val c: Pane = when (component) {
            is Pane -> component
            else -> throw IllegalArgumentException("Component must be a Pane or a subclass of Pane")
        }
        when (position) {
            Positions.TOP -> topSection.children.add(c)
            Positions.MIDDLE -> middleSection.children.add(c)
            Positions.BOTTOM -> bottomSection.children.add(c)
        }
        component.onActionProperty.addListener { _, _, _ ->
            if (component.selectable) {
                selectedItem = component
            }
        }
        if (isCollapsed) {
            collapseItems()
        } else {
            expandItems()
        }
    }
}
