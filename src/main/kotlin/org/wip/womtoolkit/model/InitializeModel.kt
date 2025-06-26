package org.wip.womtoolkit.model

import javafx.scene.paint.Color
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.wip.womtoolkit.Globals
import org.wip.womtoolkit.model.database.Database.writeLocale

object InitializeModel {
	fun init() {
		validateOrCreateDataFolder()
		validateCreateOrUpdateJSONDatabase()
		loadJSONDatabase()
		LocalizationService.currentLocaleProperty.addListener { _, _, newLocale ->
			writeLocale(newLocale)
		}
	}

	fun close() {
		writeJson()
	}

	private fun loadJSONDatabase() {
		Globals.jsonDatabase = try {
			val jarDir = System.getProperty("user.dir")
			val jsonDbPath = Path.of(jarDir, "Data", "database.json")
			val jsonString = Files.readString(jsonDbPath)
			Json.decodeFromString<JsonObject>(jsonString)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to read JSON database", e)
		}
		println(Globals.jsonDatabase)
	}

	private fun validateOrCreateDataFolder() {
		val jarDir = System.getProperty("user.dir")
		val dataFolder = Path.of(jarDir, "Data")
		if (!Files.exists(dataFolder)) {
			createAndPopulateDataFolder()
		}
		if (!Files.isDirectory(dataFolder)) {
			throw IllegalStateException("Data folder already exists but is not a directory: $dataFolder")
		}
		if (!Files.isReadable(dataFolder) || !Files.isWritable(dataFolder)) {
			throw IllegalStateException("Data folder is not readable or writable: $dataFolder")
		}
	}

	private fun createAndPopulateDataFolder() {
		val jarDir = System.getProperty("user.dir")
		val dataFolder = Path.of(jarDir, "Data")
		if (!Files.exists(dataFolder)) {
			try {
				Files.createDirectories(dataFolder)
			} catch (e: Exception) {
				throw IllegalStateException("Failed to create data folder: $dataFolder", e)
			}
		}
	}

	private fun validateCreateOrUpdateJSONDatabase() {
		val jarDir = System.getProperty("user.dir")
		val jsonDbPath = Path.of(jarDir, "Data", "database.json")
		if (!Files.exists(jsonDbPath)) {
			createJSONDatabase()
		} else if (!Files.isReadable(jsonDbPath) || !Files.isWritable(jsonDbPath)) {
			throw IllegalStateException("JSON database is not readable or writable: $jsonDbPath")
		}
	}

	//TODO implement migration logic
	private fun createJSONDatabase() {
		Files.createFile(Path.of(System.getProperty("user.dir"), "Data", "database.json")).also {
			Files.writeString(it, "{}")
		}
	}

	private fun writeJson() {
		val jsonDbPath = Path.of(System.getProperty("user.dir"), "Data", "database.json")
		try {
			val jsonString = Json.encodeToString(Globals.jsonDatabase)
			Files.writeString(jsonDbPath, jsonString)
		} catch (e: Exception) {
			throw IllegalStateException("Failed to write JSON database", e)
		}
		println("JSON database written successfully.")
	}
}