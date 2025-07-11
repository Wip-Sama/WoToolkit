package org.wip.womtoolkit.view.pages

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.view.components.NumberTextField
import org.wip.womtoolkit.view.components.SelectedFilesContainer
import org.wip.womtoolkit.view.components.Switch
import java.io.File

class Slicer : BorderPane() {
    object Constants {
        const val EXPANDED: String =
            "M4.29289 8.29289C4.68342 7.90237 5.31658 7.90237 5.70711 8.29289L12 14.5858L18.2929 8.29289C18.6834 7.90237 19.3166 7.90237 19.7071 8.29289C20.0976 8.68342 20.0976 9.31658 19.7071 9.70711L12.7071 16.7071C12.3166 17.0976 11.6834 17.0976 11.2929 16.7071L4.29289 9.70711C3.90237 9.31658 3.90237 8.68342 4.29289 8.29289Z"
        const val COLLAPSED: String =
            "M4.29289 15.7071C4.68342 16.0976 5.31658 16.0976 5.70711 15.7071L12 9.41421L18.2929 15.7071C18.6834 16.0976 19.3166 16.0976 19.7071 15.7071C20.0976 15.3166 20.0976 14.6834 19.7071 14.2929L12.7071 7.29289C12.3166 6.90237 11.6834 6.90237 11.2929 7.29289L4.29289 14.2929C3.90237 14.6834 3.90237 15.3166 4.29289 15.7071Z"
    }

    val minimumHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(100)
    val desiredHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(10000)
    val maximumHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(10000)

    @FXML lateinit var advancedModeToggle: ToggleButton
    @FXML lateinit var advancedIndicator: SVGPath
    @FXML lateinit var advancedModeContainer: VBox
    @FXML lateinit var advancedModeContent: ScrollPane
    @FXML lateinit var queuePane: ScrollPane
    @FXML lateinit var queueFlow: FlowPane

    @FXML lateinit var minimumHeight: NumberTextField
    @FXML lateinit var desiredHeight: NumberTextField
    @FXML lateinit var maximumHeight: NumberTextField
    @FXML lateinit var searchDirection: Switch
    @FXML lateinit var saveInSubfolder: Switch
    @FXML lateinit var subfolderName: TextField
    @FXML lateinit var saveAsArchive: Switch
    @FXML lateinit var archiveName: TextField
    @FXML lateinit var archiveFormat: ChoiceBox<String>
    @FXML lateinit var parallelExecution: Switch
    @FXML lateinit var outputFormat: ChoiceBox<String>
    @FXML lateinit var cutTolerance: NumberTextField

    @FXML lateinit var folderPath: TextField
    @FXML lateinit var addFolder: Button
    @FXML lateinit var execute: Button
    @FXML lateinit var searchFile: Button
    @FXML lateinit var searchFolder: Button

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
        initializeDefaults()
        folderPath.text = "C:\\Users\\sgroo\\Pictures\\converted"

        val fileChooser = FileChooser().apply {
            extensionFilters.add(
                FileChooser.ExtensionFilter("Image Files", Globals.IMAGE_INPUT_FORMATS.map { "*.$it" }),
            )
        }
        searchFile.onAction = EventHandler {
           fileChooser.showOpenMultipleDialog(this.scene.window)
        }

        val directoryChooser = DirectoryChooser().apply {
            title = "Select Directory"
        }
        searchFolder.onAction = EventHandler {
            directoryChooser.showDialog(this.scene.window)?.let { dir ->
                folderPath.text = dir.absolutePath
            }
        }

        addFolder.onAction = EventHandler {
            if (folderPath.text.isNotBlank()) {
                queueFlow.children.addLast(
                    //TODO: this is bad, loaded files and variables should be container in the model not the ui
                SelectedFilesContainer().apply {
                        inputPath = folderPath.text
                        outputPath = if (saveInSubfolder.state) {
                            "${folderPath.text}/${subfolderName.text}"
                        } else {
                            folderPath.text
                        }
                        File(folderPath.text).listFiles { it ->
                            it.isFile && Globals.IMAGE_INPUT_FORMATS.any { ext -> it.extension.equals(ext, ignoreCase = true) }
                        }?.map { it.name }?.let { fileList ->
                            this.fileList = fileList
                        } ?: run {
                            this.fileList = emptyList()
                        }
                    }
                )
                folderPath.text = ""
            }
        }

        queuePane.widthProperty().addListener { _, _, value ->
            queueFlow.prefWidth = value.toDouble()
            queueFlow.minWidth = value.toDouble()
            queueFlow.maxWidth = value.toDouble()
        }
    }

    private fun initializeAdvancedMode() {
        fun animateExpand() {
            val newHeight = if (advancedModeToggle.isSelected)
                (advancedModeToggle.height) +
                        (advancedModeContainer.spacing * (advancedModeContainer.children.size - 1)) +
                        80.0
            else
                advancedModeToggle.height

            val animationDuration = if (ApplicationSettings.userSettings.disableAnimations.value) 1.0 else 200.0
            Timeline(
                KeyFrame(
                    Duration.millis(animationDuration),
                    KeyValue(advancedModeContainer.prefHeightProperty(), newHeight),
                    KeyValue(advancedModeContainer.maxHeightProperty(), newHeight),
                    KeyValue(advancedModeContainer.minHeightProperty(), newHeight),
                ),
            ).play()
        }

        /* On Change */
        advancedModeToggle.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue != oldValue) {
                animateExpand()
                advancedIndicator.content = if (newValue) Constants.EXPANDED else Constants.COLLAPSED
            }
        }

        /* Initialize */
        advancedModeContainer.clip = rectClip
        rectClip.widthProperty().bind(advancedModeContainer.widthProperty())
        rectClip.heightProperty().bind(advancedModeContainer.heightProperty())
        advancedModeContent.isVisible = false
        advancedModeContent.isManaged = false

        advancedModeToggle.heightProperty().addListener { _, _, newValue ->
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

    private fun initializeDefaults() {
        minimumHeight.text = ApplicationSettings.slicerSettings.minimumHeight.value.toString()
        desiredHeight.text = ApplicationSettings.slicerSettings.desiredHeight.value.toString()
        maximumHeight.text = ApplicationSettings.slicerSettings.maximumHeight.value.toString()
        searchDirection.state = ApplicationSettings.slicerSettings.searchDirection.value
        saveInSubfolder.state = ApplicationSettings.slicerSettings.saveInSubFolder.value
        subfolderName.text = ApplicationSettings.slicerSettings.subFolderName.value
        saveAsArchive.state = ApplicationSettings.slicerSettings.saveInArchive.value
        archiveName.text = ApplicationSettings.slicerSettings.archiveName.value
        archiveFormat.items.addAll(Globals.ARCHIVE_OUTPUT_FORMATS)
        archiveFormat.value = ApplicationSettings.slicerSettings.archiveFormat.value
        parallelExecution.state = ApplicationSettings.slicerSettings.parallelExecution.value
        outputFormat.items.addAll(Globals.IMAGE_OUTPUT_FORMATS)
        outputFormat.value = ApplicationSettings.slicerSettings.outputFormat.value
        cutTolerance.text = ApplicationSettings.slicerSettings.cutTolerance.value.toString()
    }
}