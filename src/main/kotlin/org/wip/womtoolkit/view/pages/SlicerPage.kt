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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.processing.slicer.Slicer
import org.wip.womtoolkit.model.processing.slicer.SlicerSingleUseSettings
import org.wip.womtoolkit.view.components.NumberTextField
import org.wip.womtoolkit.view.components.SelectedFilesContainer
import org.wip.womtoolkit.view.components.Switch

class SlicerPage : BorderPane() {
    companion object {
        const val EXPANDED: String =
            "M4.29289 8.29289C4.68342 7.90237 5.31658 7.90237 5.70711 8.29289L12 14.5858L18.2929 8.29289C18.6834 7.90237 19.3166 7.90237 19.7071 8.29289C20.0976 8.68342 20.0976 9.31658 19.7071 9.70711L12.7071 16.7071C12.3166 17.0976 11.6834 17.0976 11.2929 16.7071L4.29289 9.70711C3.90237 9.31658 3.90237 8.68342 4.29289 8.29289Z"
        const val COLLAPSED: String =
            "M4.29289 15.7071C4.68342 16.0976 5.31658 16.0976 5.70711 15.7071L12 9.41421L18.2929 15.7071C18.6834 16.0976 19.3166 16.0976 19.7071 15.7071C20.0976 15.3166 20.0976 14.6834 19.7071 14.2929L12.7071 7.29289C12.3166 6.90237 11.6834 6.90237 11.2929 7.29289L4.29289 14.2929C3.90237 14.6834 3.90237 15.3166 4.29289 15.7071Z"
    }

    // FXML components
    @FXML lateinit var advancedModeToggle: ToggleButton
    @FXML lateinit var advancedIndicator: SVGPath
    @FXML lateinit var advancedModeContainer: VBox
    @FXML lateinit var advancedModeContent: ScrollPane
    @FXML lateinit var queuePane: ScrollPane
    @FXML lateinit var queueFlow: FlowPane

    // Temp data fields
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

    // User input fields
    @FXML lateinit var folderPath: TextField
    @FXML lateinit var addFromUserInput: Button
    @FXML lateinit var execute: Button
    @FXML lateinit var searchFile: Button
    @FXML lateinit var searchFolder: Button

    val minimumHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(100)
    val desiredHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(10000)
    val maximumHeightProperty: SimpleIntegerProperty = SimpleIntegerProperty(10000)

    val rectClip = Rectangle().apply {
        arcHeight = 13.0
        arcWidth = 13.0
    }
    val fileChooser = FileChooser().apply {
        extensionFilters.add(
            FileChooser.ExtensionFilter("Image Files", Globals.IMAGE_INPUT_FORMATS.map { "*.$it" }),
        )
    }
    val directoryChooser = DirectoryChooser().apply {
        title = "Select Directory"
    }

    val scope = MainScope()

    init {
        FXMLLoader(javaClass.getResource("/view/pages/slicer.fxml")).apply {
            setRoot(this@SlicerPage)
            setController(this@SlicerPage)
            load()
        }
    }

    @FXML
    fun initialize() {
        initializeAdvancedMode()
        initializeDefaults()
        folderPath.text = "C:\\Users\\sgroo\\Pictures\\converted"

        searchFile.onAction = EventHandler {
            fileChooser.showOpenMultipleDialog(this.scene.window)?.let {
                Slicer.addElementFromFiles(it.map { file -> file.absolutePath }, )
            }
        }

        searchFolder.onAction = EventHandler {
            directoryChooser.showDialog(this.scene.window)?.let { dir ->
                Slicer.addElementFromFolder(inputFolder = dir.absolutePath, createSingleUseSettings())
            }
        }

        addFromUserInput.onAction = EventHandler {
            if (folderPath.text.isNotBlank()) {
                // Valid inputs to parse:
                // Single folder path
                // Single file path
                // Multiple file paths separated by commas
                // Multiple folder paths separated by commas
                // Multiple files and folders separated by commas (all the files will be grouped together)


                // For now we will assume only one folder path is provided
                try {
                    Slicer.addElementFromFolder(inputFolder = folderPath.text, createSingleUseSettings())
                    //TODO: the notification should tell the user that the path was not found or is not a directory instead of throwing an exception
                } finally {
                    folderPath.text = ""
                }
            }
        }

        scope.launch {
            Slicer.queue.collect { queue ->
                queue.forEach { element ->
                    queueFlow.children.any { child ->
                        child is SelectedFilesContainer && child.getBoundElementToProcess() == element
                    }.let { exists ->
                        if (!exists) {
                            queueFlow.children.add(SelectedFilesContainer().apply {
                                bindToElementToProcess(element)
                                execute.onAction = EventHandler {
                                    scope.launch {
                                        Slicer.processOne(getBoundElementToProcess()!!, createSingleUseSettings())
                                    }
                                }
                                remove.onAction = EventHandler {
                                    scope.launch {
                                        Slicer.removeElement(getBoundElementToProcess()!!)
                                    }
                                }
                            })
                        }
                    }
                }
                queueFlow.children.filterIsInstance<SelectedFilesContainer>().forEach { container ->
                    if (!queue.contains(container.getBoundElementToProcess()!!)) {
                        queueFlow.children.remove(container)
                    }
                }
            }
        }

        execute.onAction = EventHandler {
            scope.launch {
                // We do not want this thing to be blocking the UI thread
                Slicer.processAll(createSingleUseSettings())
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
                advancedIndicator.content = if (newValue) EXPANDED else COLLAPSED
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

    private fun createSingleUseSettings(): SlicerSingleUseSettings {
        return SlicerSingleUseSettings(
            minimumHeight = minimumHeight.text.toIntOrNull() ?: ApplicationSettings.slicerSettings.minimumHeight.value,
            desiredHeight = desiredHeight.text.toIntOrNull() ?: ApplicationSettings.slicerSettings.desiredHeight.value,
            maximumHeight = maximumHeight.text.toIntOrNull() ?: ApplicationSettings.slicerSettings.maximumHeight.value,
            searchDirection = searchDirection.state,
            saveInSubfolder = saveInSubfolder.state,
            subfolderName = subfolderName.text.ifBlank { ApplicationSettings.slicerSettings.subFolderName.value },
            saveAsArchive = saveAsArchive.state,
            archiveName = archiveName.text.ifBlank { ApplicationSettings.slicerSettings.archiveName.value },
            archiveFormat = archiveFormat.value ?: ApplicationSettings.slicerSettings.archiveFormat.value,
            parallelExecution = parallelExecution.state,
            outputFormat = outputFormat.value ?: ApplicationSettings.slicerSettings.outputFormat.value,
            cutTolerance = cutTolerance.text.toIntOrNull() ?: ApplicationSettings.slicerSettings.cutTolerance.value
        )
    }
}