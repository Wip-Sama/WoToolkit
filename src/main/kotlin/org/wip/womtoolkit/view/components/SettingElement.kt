package org.wip.womtoolkit.view.components

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.css.PseudoClass
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.util.Duration
import org.wip.womtoolkit.model.Globals
import kotlin.properties.Delegates

/**
 * @author Wip
 * Exposes: image|svg / name/ description/ switch|choice|custom? / expandable
 */
class SettingElement() : AnchorPane() {
    object Constants {
        const val COLLAPSED: String =
            "M4.29289 8.29289C4.68342 7.90237 5.31658 7.90237 5.70711 8.29289L12 14.5858L18.2929 8.29289C18.6834 7.90237 19.3166 7.90237 19.7071 8.29289C20.0976 8.68342 20.0976 9.31658 19.7071 9.70711L12.7071 16.7071C12.3166 17.0976 11.6834 17.0976 11.2929 16.7071L4.29289 9.70711C3.90237 9.31658 3.90237 8.68342 4.29289 8.29289Z"
        const val EXPANDED: String =
            "M4.29289 15.7071C4.68342 16.0976 5.31658 16.0976 5.70711 15.7071L12 9.41421L18.2929 15.7071C18.6834 16.0976 19.3166 16.0976 19.7071 15.7071C20.0976 15.3166 20.0976 14.6834 19.7071 14.2929L12.7071 7.29289C12.3166 6.90237 11.6834 6.90237 11.2929 7.29289L4.29289 14.2929C3.90237 14.6834 3.90237 15.3166 4.29289 15.7071Z"
    }

    @FXML lateinit var title: Label
    @FXML lateinit var description: Label
    @FXML lateinit var expandedIndicator: SVGPath
    @FXML lateinit var expandablePane: BorderPane
    @FXML lateinit var imageContainer: BorderPane
    @FXML lateinit var displayPane: BorderPane
    @FXML lateinit var rightContainer: HBox

    val expandedProperty = SimpleBooleanProperty(false)

    var expandableContent: Pane? by Delegates.observable(null) { _, oldValue, newValue ->
        expandedIndicator.isManaged = newValue != null
        expandedIndicator.isVisible = newValue != null
        expandablePane.top = newValue
        displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), newValue != null)
    }

    var quickSetting: Node? by Delegates.observable(null) { _, oldValue, newValue ->
        rightContainer.children.remove(oldValue)
        rightContainer.children.add(0, newValue)
        newValue?.hoverProperty()?.addListener { _, _, hover ->
            if (expandableContent == null)
                return@addListener
            if (hover) {
                displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), false)
            } else {
                displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), true)
            }
        }
    }

    init {
        FXMLLoader(javaClass.getResource("/components/settingElement.fxml")).apply {
            setRoot(this@SettingElement)
            setController(this@SettingElement)
            load()
        }

        expandablePane.managedProperty().bind(expandedProperty)
        expandablePane.visibleProperty().bind(expandedProperty)

        expandedProperty.addListener { _, _, newValue ->
            if (expandableContent == null) {
                Globals.logger?.warning("Expandable content is null, cannot expand setting element")
                expandedProperty.set(false)
                return@addListener
            }
            animateExpand()
            if (newValue) {
                expandedIndicator.content = Constants.EXPANDED
            } else {
                expandedIndicator.content = Constants.COLLAPSED
            }
        }
    }

    @FXML
    fun initialize() {
        val rectClip = Rectangle().apply {
            arcHeight = 13.0
            arcWidth = 13.0
        }
        clip = rectClip
        layoutBoundsProperty().addListener { _, _, bounds ->
            rectClip.width = bounds.width
            rectClip.height = bounds.height
        }


        displayPane.onMouseClicked = EventHandler { evt ->
            if (quickSetting != null && isDescendantOf(evt.target as? Node, quickSetting!!)) {
                return@EventHandler
            }
            if (expandableContent != null) {
                expandedProperty.set(!expandedProperty.get())
            }
        }
        expandedIndicator.isManaged = false
        expandedIndicator.isVisible = false
    }

    private fun isDescendantOf(node: Node?, ancestor: Node): Boolean {
        var current = node
        while (current != null) {
            if (current == ancestor) return true
            current = current.parent
        }
        return false
    }

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun animateExpand() {
        val displayHeight = displayPane.height
        if (expandedProperty.get())
            pseudoClassStateChanged(PseudoClass.getPseudoClass("expanded"), expandedProperty.get())
        Timeline(
            KeyFrame(
                Duration.millis(100.0),
                KeyValue(prefHeightProperty(),
                    if (expandedProperty.get())
                        (expandableContent?.prefHeight ?: 0.0) +
                        (expandablePane.padding?.top ?: 12.0) +
                        (expandablePane.padding?.bottom ?: 12.0) +
                        displayHeight
                    else
                        displayHeight
                    )
                ),
            KeyFrame(Duration.millis(100.0),
                { pseudoClassStateChanged(PseudoClass.getPseudoClass("expanded"), expandedProperty.get()) }
            )
        ).play()
    }
}