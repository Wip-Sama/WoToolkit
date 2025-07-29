package org.wip.womtoolkit.model.processing.slicer

import com.pty4j.PtyProcessBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.enums.ThreadMode
import org.wip.womtoolkit.model.processing.ElementToProcess
import org.wip.womtoolkit.model.services.activityMonitor.ActivityMonitorService
import org.wip.womtoolkit.model.services.notification.NotificationData
import org.wip.womtoolkit.model.services.notification.NotificationService
import org.wip.womtoolkit.utils.CommandRunner
import java.io.File
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.EnumSet
import kotlin.concurrent.withLock


/**
 * UI (add folder/files) -> slicer (check folder and add ElementsToProcess to queue) -> UI (update ui and show new elements)
 * */
object Slicer {
	private var _queue: MutableStateFlow<MutableList<ElementToProcess>> = MutableStateFlow(mutableListOf())
	val queue: StateFlow<List<ElementToProcess>>
		get() = _queue.asStateFlow()

	private fun processElementImplementation(element: ElementToProcess, settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		with(element) {
			lock.withLock {
				val lockedChannels = mutableListOf<FileLock>()
				val fileChannels = mutableListOf<FileChannel>()
				try {
					val paths = elements.value.map { file ->
						if (inputFolder.value.isNotEmpty())
							Paths.get(inputFolder.value, file)
						else
							Paths.get(file)
					}
					for (path in paths) {
						val absPath = path.toAbsolutePath()
						val channel = Files.newByteChannel(absPath, EnumSet.of(StandardOpenOption.READ)) as FileChannel
						val fileLock = channel.lock(0L, Long.MAX_VALUE, true) // true = lock condiviso (lettura)
						lockedChannels.add(fileLock)
						fileChannels.add(channel)
					}

					Globals.logger.info("Tutti i file selezionati sono lockati a livello OS")

					val pythonPath: String = "Modules\\python_portable\\WPy64-31350\\python\\python.exe"
					val scriptPath: String = "Modules\\Slicer\\slicer_handler.py"

					val jsonString = Json {
						encodeDefaults = true
					}.encodeToString(
						SlicerDTO(
							images = paths.map { it.toAbsolutePath().toString() },
							minimumHeight = settings.minimumHeight,
							desiredHeight = settings.desiredHeight,
							maximumHeight = settings.maximumHeight,
							cutTolerance = settings.cutTolerance,
							searchDirection = settings.searchDirection,
							outputFolder = outputFolder.value,
						)
					)

					// if outputFolder does not exist, create it
					val outputPath = Paths.get(outputFolder.value)
					if (!Files.exists(outputPath)) {
						Files.createDirectories(outputPath)
					}

					val slicerSettings = "${outputFolder.value}\\slicer_settings.json"
					Files.writeString(Paths.get(slicerSettings), jsonString)

					val commands = CommandRunner.runInPowershell()

//					commands.add("ls")

					commands.add(pythonPath)
					commands.add("'$scriptPath'")
					commands.add("'$slicerSettings'") // path al file json dei settings

					val process = ProcessBuilder(commands)
						.directory(File(System.getProperty("user.dir")))
						.start()
//
//					val process = PtyProcessBuilder()
//						.setCommand(commands.toTypedArray())
//						.setDirectory(System.getProperty("user.dir"))
//						.start()

					process.inputStream.bufferedReader().use { reader ->
						reader.lines().forEach { line ->
							println(CommandRunner.cleanAnsiCodes(line))
						}
					}

					process.errorStream.bufferedReader().use { reader ->
						reader.lines().forEach { line ->
							println(CommandRunner.cleanAnsiCodes(line))
						}
					}

					Globals.logger.info("Processing done for element: ${elements.value}")


				} catch (e: Exception) {
					e.printStackTrace()
					throw e
				} finally {
					lockedChannels.forEach { it.release() }
					fileChannels.forEach { it.close() }
					element.setProcessing(false)
				}
			}
		}
	}

	private fun processElement(element: ElementToProcess, settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		ActivityMonitorService["slicer"].apply {
			(if (settings.parallelExecution) ThreadMode.MULTI_THREAD else ThreadMode.SINGLE_THREAD).let {
				if (threadMode != it) {
					threadMode = it
				}
			}
			element.setProcessing(true)
			submit { processElementImplementation(element, settings) }
		}
	}

	private fun addElement(element: ElementToProcess) {
		_queue.value = _queue.value.toMutableList().apply {
			add(element)
		}
	}

	private fun processOne(index: Int = 0, settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		val s = settings.copy()
		_queue.value.getOrNull(index)?.let {
			if (!canProcessElement(it)) {
				Globals.logger.info("Element at index $index is not ready for processing: ${it.elements.value}, output folder: ${it.outputFolder.value}")
				NotificationService.addNotification(NotificationData(
					localizedContent = "info.elementNotReadyForProcessing",
					type = NotificationTypes.INFO,
				))
			} else {
				processElement(it, s)
			}
		}
	}

	fun processOne(element: ElementToProcess, settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		val s = settings.copy()

		if (!canProcessElement(element)) {
			Globals.logger.info("This element is not ready for processing!")
			NotificationService.addNotification(NotificationData(
				localizedContent = "info.elementNotReadyForProcessing",
				type = NotificationTypes.INFO,
			))
			return
		}

		if (!_queue.value.contains(element)) {
			Globals.logger.severe { "Element ${element.elements.value} not found in the queue, skipping processing" }
			NotificationService.addNotification(NotificationData(
				localizedContent = "error.elementNotFoundInQueue",
			 type = NotificationTypes.ERROR,
			))
			return
		}

		processElement(element, s)
	}

	fun canProcessElement(element: ElementToProcess): Boolean {
		if (element.elements.value.isEmpty()) return false
		if (element.outputFolder.value.isEmpty()) return false
		return true
	}

	fun processSome(indexes: List<Int>, settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		val s = settings.copy()
		for (index in indexes) {
			processOne(index, s)
		}
	}

	fun processAll(settings: SlicerSingleUseSettings = SlicerSingleUseSettings()) {
		val s = settings.copy()
		for (index in _queue.value.indices) {
			processOne(index, s)
		}
	}

	/**
	 * This function will add an element taking all the supported image formats from the input folder.
	 * @param inputFolder the folder containing the images to process
	 * @param outputFolder the folder where the processed images will be saved, defaults to inputFolder/${subFolderName} if saveInSubfolder is true WARNING: this will also default to inputFolder if saveInSubfolder is false and that could cause problems
	 * @param supportedFormats in the list of supported formats, defaults to Globals.IMAGE_INPUT_FORMATS
	 * */
	fun addElementFromFolder(
		inputFolder: String,
		settings: SlicerSingleUseSettings = SlicerSingleUseSettings(),
		outputFolder: String = if (settings.saveInSubfolder) {
			"${inputFolder}\\${settings.subfolderName}"
		} else {
			inputFolder
		},
		supportedFormats: List<String> = Globals.IMAGE_INPUT_FORMATS,
	) {
		val elements = mutableListOf<String>()
		val folderPath = Paths.get(inputFolder)

		if (!folderPath.toFile().exists()) {
			NotificationService.addNotification(NotificationData(
				localizedContent = "info.folderDoesNotExist",
				type = NotificationTypes.INFO,
			))
			Globals.logger.info { "Folder $inputFolder does not exist" }
			return
		}

		if (!folderPath.toFile().isDirectory) {
			NotificationService.addNotification(NotificationData(
				localizedContent = "info.pathIsNotADirectory",
				type = NotificationTypes.INFO,
			))
			Globals.logger.info { "Path $inputFolder is not a directory" }
			return
		}

		queue.value.any { element ->
			element.inputFolder.value == inputFolder
		}.let { exists ->
			if (exists) {
				NotificationService.addNotification(NotificationData(
					localizedContent = "info.folderAlreadyAdded",
				 type = NotificationTypes.INFO,
				))
				return
			}
		}

		folderPath.toFile().listFiles { file ->
			file.isFile && supportedFormats.any { ext -> file.extension.equals(ext, ignoreCase = true) }
		}?.forEach { file ->
			elements.add(file.name) // add only name and extension if inputFolder is present
		}

		addElement(
			ElementToProcess(
				inputFolder = inputFolder,
				elements = elements,
				outputFolder = outputFolder,
			)
		)
	}

	/**
	 * This function will add an element taking all the files from the list and will ensure the files exist and are supported.
	 * @param files the list of files to add to the queue
	 * @param outputFolder the folder where the processed images will be saved, defaults to empty string (no output folder) Warning: if not provided, you won't be able to process the element
	 * */
	fun addElementFromFiles(
		files: List<String>,
		outputFolder: String = ""
	) {
		if (files.isNotEmpty()) {
			// Filter out non-existing files and directories
			val _files = files.filter { file ->
				val path = Paths.get(file)
				path.toFile().exists() && path.toFile().isFile && Globals.IMAGE_INPUT_FORMATS.any { ext ->
					path.toFile().extension.equals(ext, ignoreCase = true)
				}
			}

			if (_files.isEmpty()) {
				NotificationService.addNotification(NotificationData(
					localizedContent = "warning.noValidFilesProvided",
					type = NotificationTypes.WARNING,
				))
				Globals.logger.warning("No valid files provided in the list: $files")
				return
			}

			addElement(
				ElementToProcess(
					elements = _files.toMutableList(),
					outputFolder = outputFolder
				)
			)
		}
	}

	fun removeElement(element: ElementToProcess) {
		_queue.value = _queue.value.filter { it != element }.toMutableList()
	}

	fun clearQueue() {
		_queue.value = mutableListOf()
	}
}