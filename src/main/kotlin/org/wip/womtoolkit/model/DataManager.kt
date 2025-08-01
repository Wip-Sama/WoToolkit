package org.wip.womtoolkit.model

import javafx.scene.paint.Color
import kotlinx.coroutines.flow.MutableStateFlow
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.services.localization.LocalizationService
import org.wip.womtoolkit.model.services.modulesManagment.ModuleManagementService
import org.wip.womtoolkit.model.services.modulesManagment.moduleDTO.ModuleInfo
import org.wip.womtoolkit.model.services.notification.NotificationData
import org.wip.womtoolkit.model.services.notification.NotificationService
import org.wip.womtoolkit.utils.serializers.ColorSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object DataManager {
	val jarDir: String = System.getProperty("user.dir")
	val dataFolder: Path = Path.of(jarDir, "Data")
	val modulesFolder: Path = Path.of(jarDir, "Modules")
	val logFolder: Path = Path.of(jarDir, "Log")
	val applicationSettings: Path = Path.of(jarDir, "Data", "applicationSettings.json")

	val module = SerializersModule {
		contextual(MutableStateFlow::class) { MutableStateFlowSerializer(ColorSerializer) }
		contextual(Color::class, ColorSerializer)
	}

	val customJsonSerializer = Json {
		serializersModule = module
		prettyPrint = true
		encodeDefaults = false // Could cause circular serialization loop
		isLenient = true // Allows for lenient parsing of JSON
//		ignoreUnknownKeys = true
	}

	fun init() {
		// Initialization phase
		// Data
		validateOrCreateFolder(dataFolder)
		validateOrCreateApplicationDataJSON()

		// Modules
		validateOrCreateFolder(modulesFolder)
		loadInstalledModules()
		addDefaultModules()
		validateModules()


		// Log
		validateOrCreateFolder(logFolder)

		// Loading phase
		loadApplicationDataJSON()

		// Forced sync phase
//		LocalizationService.currentLocaleProperty.addListener { _, _, newLocale -> }
	}

	fun close() {
		writeJson()
		updateModulesJson()
	}

	private fun validateOrCreateFolder(folder: Path) {
		if (!checkItsDirectoryAndICanReadWrite(folder)) {
			try {
				Files.createDirectories(folder)
			} catch (e: Exception) {
				//TODO: something better than this
				Globals.logger.severe("Failed to create folder: $folder, error: $e")
				NotificationService.addNotification(NotificationData(
					localizedContent = "error.failedToCreateFolder",
					type = NotificationTypes.ERROR,
					urgency = 1,
				))
				throw IllegalStateException("Failed to create folder: $folder", e)
			}
		}
	}

	private fun loadInstalledModules() {
		modulesFolder.listDirectoryEntries().forEach { file ->
			if (!file.isDirectory()) return@forEach
			if (!checkItsDirectoryAndICanReadWrite(file)) {
				Globals.logger.warning("Module folder is not valid or not readable/writable: $file")
				return@forEach
			}
			file.listDirectoryEntries().filter { it.endsWith("moduleInfo.json") }.forEach { moduleInfo ->
				try {
					val moduleInfo: ModuleInfo = customJsonSerializer.decodeFromString(
						ModuleInfo.serializer(),
						Files.readString(moduleInfo)
					)
					ModuleManagementService.addOrUpdateModule(moduleInfo)
				} catch (e: Exception) {
					Globals.logger.warning("Failed to load module info from $moduleInfo: ${e.message}")
					NotificationService.addNotification(NotificationData(
						localizedContent = "error.failedToLoadModule",
						type = NotificationTypes.ERROR,
						urgency = 1,
					))
				}
			}
		}
	}

	private fun addDefaultModules() {
		if (ModuleManagementService.modules["Slicer"] == null) {
			ModuleManagementService.addOrUpdateModule(ModuleInfo(
				name = MutableStateFlow("Slicer"),
				version = MutableStateFlow("0.1.0.0"),
				toolkitVersion = MutableStateFlow(Globals.TOOLKIT_VERSION.toString()),
				source = MutableStateFlow("github.com/wip-org/womtoolkit-modules"),
			))
		}

		if (ModuleManagementService.modules["Converter"] == null) {
			ModuleManagementService.addOrUpdateModule(ModuleInfo(
				name = MutableStateFlow("Converter"),
				version = MutableStateFlow("0.1.0.0"),
				toolkitVersion = MutableStateFlow(Globals.TOOLKIT_VERSION.toString()),
				source = MutableStateFlow("github.com/wip-org/womtoolkit-modules"),
			))
		}
	}

	private fun validateModules() {
		ModuleManagementService.modules.forEach { (name, module) ->
			if (!module.compatibleWithToolkitVersion) {
				Globals.logger.warning("Module $name is not compatible with the current toolkit version: ${module.toolkitVersion.value}")
				NotificationService.addNotification(NotificationData(
					localizedContent = "warning.moduleVersionNotCompatibleWithToolkitVersion",
					type = NotificationTypes.WARNING,
					urgency = 0,
				))
			}
			if (!module.compatibleWithPlatform) {
				Globals.logger.warning("Module $name is not compatible with the current platform: ${Globals.PLATFORM.name}")
				NotificationService.addNotification(NotificationData(
					localizedContent = "warning.moduleNotCompatiblePlatform",
					type = NotificationTypes.WARNING,
					urgency = 0,
				))
			}
			module._supportedPlatforms[Globals.PLATFORM.name]?.let { platform ->
				platform.validation.value.forEach { step ->
					when (step.type.value) {
						"hash" -> {
							step.file
							step.hash
						}
						"command" -> {
							step.command
							step.expectedResult
						}
					}
				}
			}
		}
		Globals.logger.info("All modules validated successfully.")
	}

	private fun validateOrCreateApplicationDataJSON() {
		if (!checkItsFileAndICanReadWrite(applicationSettings)) {
			try {
				val out = customJsonSerializer.encodeToString(ApplicationData)
				Files.createFile(applicationSettings).also {
					Files.write(applicationSettings, out.toByteArray())
				}
				Globals.logger.info(out)
			} catch (e: Exception) {
				//TODO: something better than this
				Globals.logger.warning("Failed to create application settings JSON: $applicationSettings")
				throw IllegalStateException("Failed to create application settings JSON: $applicationSettings", e)
			}
		}
	}

	private fun checkItsFileAndICanReadWrite(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isRegularFile(path)) {
			return false
		}
		if (!Files.isReadable(path) || !Files.isWritable(path)) {
			return false
		}
		return true
	}

	private fun checkItsDirectoryAndICanReadWrite(path: Path): Boolean {
		if (!Files.exists(path)) {
			return false
		}
		if (!Files.isDirectory(path)) {
			return false
		}
		if (!Files.isReadable(path) || !Files.isWritable(path)) {
			return false
		}
		return true
	}

	private fun loadApplicationDataJSON() {
		try {
			val jsonString = Files.readString(applicationSettings)
			customJsonSerializer.decodeFromString(ApplicationData.serializer(), jsonString)
			Globals.logger.info("ApplicationData loaded successfully: $ApplicationData")
		} catch (e: Exception) {
			Globals.logger.warning("Failed to load ApplicationData, using defaults: ${e.message}")
		}
	}

	private fun writeJson() {
		try {
			val jsonString = customJsonSerializer.encodeToString(ApplicationData)
			Globals.logger.info(jsonString)
			Files.writeString(applicationSettings, jsonString)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to write JSON database", e)
		}
	}

	private fun updateModulesJson() {
		for (module in ModuleManagementService.modules) {
			try {
				val moduleInfoPath = Paths.get(modulesFolder.absolutePathString(), module.value.name.value, "moduleInfo.json")
				val jsonString = customJsonSerializer.encodeToString(ModuleInfo.serializer(), module.value)
				Files.writeString(moduleInfoPath, jsonString)
			} catch (e: Exception) {
				Globals.logger.warning("Failed to write module info for ${module.value.name.value}: ${e.message}")
			}
		}
	}
}