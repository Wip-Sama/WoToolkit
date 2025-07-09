package org.wip.womtoolkit.model

import javafx.scene.paint.Color
import kotlinx.coroutines.flow.MutableStateFlow
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.wip.womtoolkit.utils.serializers.ColorSerializer
import org.wip.womtoolkit.utils.serializers.MutableStateFlowSerializer

object DataManager {
	val jarDir: String = System.getProperty("user.dir")
	val dataFolder: Path = Path.of(jarDir, "Data")
	val applicationSettings: Path = Path.of(jarDir, "Data", "applicationSettings.json")

	val module = SerializersModule {
		contextual(MutableStateFlow::class) { MutableStateFlowSerializer(ColorSerializer()) }
		contextual(Color::class, ColorSerializer())
	}

	val customJsonSerializer = Json {
		serializersModule = module
		prettyPrint = true
		encodeDefaults = true
		isLenient = true // Allows for lenient parsing of JSON
	}

	fun init() {
		// Initialization phase
		validateOrCreateDataFolder()
		validateOrCreateApplicationSettingsJSON()

		// Loading phase
		loadApplicationSettingsJSON()

		// Forced sync phase
		LocalizationService.currentLocaleProperty.addListener { _, _, newLocale -> }
	}

	fun close() {
		writeJson()
	}

	private fun validateOrCreateDataFolder() {
		//Is invalid
		if (!checkItsDirectoryAndICanReadWrite(dataFolder)) {
			try {
				Files.createDirectories(dataFolder)
			} catch (e: Exception) {
				//TODO: something better than this
				Globals.logger?.warning("Failed to create data folder: $dataFolder")
				throw IllegalStateException("Failed to create data folder: $dataFolder", e)
			}
		}
	}

	private fun validateOrCreateApplicationSettingsJSON() {
		if (!checkItsFileAndICanReadWrite(applicationSettings)) {
			try {
				val out = customJsonSerializer.encodeToString(ApplicationSettings.serializer(), ApplicationSettings)
				Files.createFile(applicationSettings).also {
					Files.write(applicationSettings, out.toByteArray())
				}
				println(out)
			} catch (e: Exception) {
				//TODO: something better than this
				Globals.logger?.warning("Failed to create application settings JSON: $applicationSettings")
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

	private fun loadApplicationSettingsJSON() {
		try {
			val jsonString = Files.readString(applicationSettings)
			val appSettings = customJsonSerializer.decodeFromString(ApplicationSettings.serializer(), jsonString)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to read JSON database", e)
		}
	}

	private fun writeJson() {
		try {
			val jsonString = customJsonSerializer.encodeToString(ApplicationSettings)
			println(jsonString)
			Files.writeString(applicationSettings, jsonString)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to write JSON database", e)
		}
	}
}