package org.wip.womtoolkit.view.components

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
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
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.Lsp
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

    private val _contentProperty: SimpleStringProperty = SimpleStringProperty("")
    fun getContent(): String = _contentProperty.get()
    fun setContent(value: String) = _contentProperty.set(value)

    private val _titleLocalizationProperty = SimpleStringProperty("")
    fun getTitleLocalization(): String = _titleLocalizationProperty.get()
    fun setTitleLocalization(value: String) = _titleLocalizationProperty.set(value)

    private val _descriptionLocalizationProperty = SimpleStringProperty("")
    fun getDescriptionLocalization(): String = _descriptionLocalizationProperty.get()
    fun setDescriptionLocalization(value: String) = _descriptionLocalizationProperty.set(value)

    val expandedProperty = SimpleBooleanProperty(false)

    var _expandableContent: Pane? by Delegates.observable(null) { _, oldValue, newValue ->
        expandedIndicator.isManaged = newValue != null
        expandedIndicator.isVisible = newValue != null
        expandablePane.top = newValue
        displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), newValue != null)
    }
    fun getExpandableContent(): Pane? = _expandableContent
    fun setExpandableContent(value: Pane?) { _expandableContent = value }

    // Aggiungi questo per SceneBuilder
    fun getExpandableChildren(): javafx.collections.ObservableList<Node> {
        if (_expandableContent == null) {
            _expandableContent = Pane()
        }
        return _expandableContent!!.children
    }

    fun getExpandableChild(): Node? = _expandableContent?.children?.firstOrNull()
    fun setExpandableChild(value: Node?) {
        if (_expandableContent == null) {
            _expandableContent = Switch()
        }
        _expandableContent!!.children.clear()
        if (value != null) {
            _expandableContent!!.children.add(value)
        }
    }

    var quickSetting: Node? by Delegates.observable(null) { _, oldValue, newValue ->
        rightContainer.children.remove(oldValue)
        rightContainer.children.add(0, newValue)
        newValue?.hoverProperty()?.addListener { _, _, hover ->
            if (_expandableContent == null)
                return@addListener
            if (hover) {
                displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), false)
            } else {
                displayPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("expandable"), true)
            }
        }
    }
//    fun getQuickSetting(): Node? = quickSetting
//    fun setQuickSetting(value: Node?) { quickSetting = value }

    val rectClip = Rectangle().apply {
        arcHeight = 13.0
        arcWidth = 13.0
    }

    init {
        FXMLLoader(javaClass.getResource("/view/components/settingElement.fxml")).apply {
            setRoot(this@SettingElement)
            setController(this@SettingElement)
            load()
        }

        expandablePane.managedProperty().set(expandedProperty.get())
        expandablePane.visibleProperty().set(expandedProperty.get())

        expandedProperty.addListener { _, _, newValue ->
            animateExpand()
            if (_expandableContent == null) {
                Globals.logger?.warning("Expandable content is null, cannot expand setting element")
                expandedProperty.set(false)
                return@addListener
            }
            if (newValue) {
                expandedIndicator.content = Constants.EXPANDED
            } else {
                expandedIndicator.content = Constants.COLLAPSED
            }
        }
    }

    @FXML
    fun initialize() {
        clip = rectClip
        layoutBoundsProperty().addListener { _, _, bounds ->
            rectClip.width = bounds.width
        }
        rectClip.height = displayPane.prefHeight

        displayPane.onMouseClicked = EventHandler { evt ->
            if (quickSetting != null && isDescendantOf(evt.target as? Node, quickSetting!!)) {
                return@EventHandler
            }
            if (_expandableContent != null) {
                expandedProperty.set(!expandedProperty.get())
            }
        }
        expandedIndicator.isManaged = false
        expandedIndicator.isVisible = false

        _titleLocalizationProperty.addListener {
            title.textProperty().bind(Lsp.lsb(_titleLocalizationProperty.value))
        }
        _descriptionLocalizationProperty.addListener {
            description.textProperty().bind(Lsp.lsb(_descriptionLocalizationProperty.value))
        }
        _contentProperty.addListener { _, _, newValue ->
            imageContainer.center = SVGPath().apply {
                content = newValue
            }
        }
    }

    private fun isDescendantOf(node: Node?, ancestor: Node): Boolean {
        var current = node
        while (current != null) {
            if (current == ancestor) return true
            current = current.parent
        }
        return false
    }

    fun animateExpand() {
        if (expandedProperty.get())
            pseudoClassStateChanged(PseudoClass.getPseudoClass("expanded"), expandedProperty.get())

        val newHeight = if (expandedProperty.get())
                (_expandableContent?.height ?: 0.0) +
                        (expandablePane.padding?.top ?: 8.0) +
                        (expandablePane.padding?.bottom ?: 8.0) +
                        displayPane.height
        else
            displayPane.height

        val animationDuration = if (ApplicationSettings.userSettings.disableAnimations.value) 1.0 else 200.0
        Timeline(
            KeyFrame(Duration.ZERO,
                {
                    if (expandedProperty.get()) {
                        expandablePane.visibleProperty().set(true)
                        expandablePane.managedProperty().set(true)
                    }
                }
            ),
            KeyFrame(
                Duration.millis(animationDuration),
                KeyValue( prefHeightProperty(), newHeight ),
                KeyValue( maxHeightProperty(), newHeight ),
                KeyValue( minHeightProperty(), newHeight ),
                KeyValue( rectClip.heightProperty(), newHeight )
            ),
            KeyFrame(Duration.millis(animationDuration),
                {
                    pseudoClassStateChanged(PseudoClass.getPseudoClass("expanded"), expandedProperty.get())
                    if (!expandedProperty.get()) {
                        expandablePane.visibleProperty().set(false)
                        expandablePane.managedProperty().set(false)
                    }
                }
            )
        ).play()
    }
}