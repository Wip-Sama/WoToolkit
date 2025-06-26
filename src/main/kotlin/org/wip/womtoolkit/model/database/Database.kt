package org.wip.womtoolkit.model.database

import javafx.scene.paint.Color
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.wip.womtoolkit.Globals

object Database {
	fun loadTheme(): String {
		return Globals.jsonDatabase["theme"]?.jsonPrimitive?.content ?: "dark"
	}

	fun writeTheme(theme: String) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("theme", JsonPrimitive(theme))
		})
	}

	fun loadAccentColor(): Color {
		return Color.rgb(
			Globals.jsonDatabase["accentColor"]?.jsonObject["r"]?.jsonPrimitive?.int ?: 255,
			Globals.jsonDatabase["accentColor"]?.jsonObject["g"]?.jsonPrimitive?.int ?: 255,
			Globals.jsonDatabase["accentColor"]?.jsonObject["b"]?.jsonPrimitive?.int ?: 255
		)
	}

	fun writeAccentColor(color: Color) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("accentColor", JsonObject(mapOf(
				"r" to JsonPrimitive((color.red * 255).toInt()),
				"g" to JsonPrimitive((color.green * 255).toInt()),
				"b" to JsonPrimitive((color.blue * 255).toInt())
			)))
		})
	}

	fun loadLocale(): String? {
		return Globals.jsonDatabase["locale"]?.jsonPrimitive?.content
	}

	fun writeLocale(locale: String) {
		Globals.jsonDatabase = JsonObject(Globals.jsonDatabase.toMutableMap().apply {
			put("locale", JsonPrimitive(locale))
		})
	}
}